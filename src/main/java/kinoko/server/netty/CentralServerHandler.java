package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.database.CharacterInfo;
import kinoko.database.DatabaseManager;
import kinoko.packet.CentralPacket;
import kinoko.packet.field.MessengerPacket;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.FriendPacket;
import kinoko.packet.world.PartyPacket;
import kinoko.server.friend.Friend;
import kinoko.server.friend.FriendRequest;
import kinoko.server.friend.FriendStatus;
import kinoko.server.header.CentralHeader;
import kinoko.server.messenger.Messenger;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.messenger.MessengerUser;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.CentralServerNode;
import kinoko.server.node.RemoteServerNode;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.server.party.Party;
import kinoko.server.party.PartyRequest;
import kinoko.server.party.PartyResultType;
import kinoko.server.user.RemoteUser;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;

public final class CentralServerHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(CentralServerHandler.class);
    private final CentralServerNode centralServerNode;

    public CentralServerHandler(CentralServerNode centralServerNode) {
        this.centralServerNode = centralServerNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final RemoteServerNode remoteServerNode = ctx.channel().attr(RemoteServerNode.NODE_KEY).get();
        if (remoteServerNode == null) {
            log.error("Received packet from unknown node {}", ctx.channel().remoteAddress());
            return;
        }
        final int op = inPacket.decodeShort();
        final CentralHeader header = CentralHeader.getByValue(op);
        log.log(Level.TRACE, "[CentralServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        ServerExecutor.submitService(() -> {
            switch (header) {
                case InitializeResult -> handleInitializeResult(remoteServerNode, inPacket);
                case ShutdownResult -> handleShutdownResult(remoteServerNode, inPacket);
                case OnlineRequest -> handleOnlineRequest(remoteServerNode, inPacket);
                case MigrateRequest -> handleMigrateRequest(remoteServerNode, inPacket);
                case TransferRequest -> handleTransferRequest(remoteServerNode, inPacket);
                case UserConnect -> handleUserConnect(remoteServerNode, inPacket);
                case UserUpdate -> handleUserUpdate(remoteServerNode, inPacket);
                case UserDisconnect -> handleUserDisconnect(remoteServerNode, inPacket);
                case UserPacketRequest -> handleUserPacketRequest(remoteServerNode, inPacket);
                case UserPacketReceive -> handleUserPacketReceive(remoteServerNode, inPacket);
                case UserPacketBroadcast -> handleUserPacketBroadcast(remoteServerNode, inPacket);
                case UserQueryRequest -> handleUserQueryRequest(remoteServerNode, inPacket);
                case ServerPacketBroadcast -> handleServerPacketBroadcast(remoteServerNode, inPacket);
                case MessengerRequest -> handleMessengerRequest(remoteServerNode, inPacket);
                case PartyRequest -> handlePartyRequest(remoteServerNode, inPacket);
                case FriendRequest -> handleFriendRequest(remoteServerNode, inPacket);
                case null -> log.error("Central Server received an unknown opcode : {}", op);
                default -> log.error("Central Server received an unhandled header : {}", header);
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final RemoteServerNode remoteServerNode = ctx.channel().attr(RemoteServerNode.NODE_KEY).get();
        if (remoteServerNode != null && !centralServerNode.isShutdown()) {
            log.error("Lost connection to channel {}", remoteServerNode.getChannelId() + 1);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }


    // HANDLER METHODS -------------------------------------------------------------------------------------------------

    private void handleInitializeResult(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final byte[] channelHost = inPacket.decodeArray(4);
        final int channelPort = inPacket.decodeInt();
        // Initialize remote node
        remoteServerNode.setChannelId(channelId);
        remoteServerNode.setChannelHost(channelHost);
        remoteServerNode.setChannelPort(channelPort);
        centralServerNode.addServerNode(remoteServerNode);
    }

    private void handleShutdownResult(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final boolean success = inPacket.decodeBoolean();
        if (!success) {
            log.error("Failed to shutdown channel {}, trying again", channelId + 1);
            remoteServerNode.write(CentralPacket.shutdownRequest());
            return;
        }
        centralServerNode.removeServerNode(channelId);
    }

    private void handleOnlineRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Login - check if account is already logged in
        final int requestId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final boolean online = centralServerNode.isOnline(accountId);
        remoteServerNode.write(CentralPacket.onlineResult(requestId, online));
    }

    private void handleMigrateRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Channel migration - complete stored migration request
        final int requestId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final byte[] clientKey = inPacket.decodeArray(8);
        final Optional<MigrationInfo> migrationResult = centralServerNode.completeMigrationRequest(remoteServerNode.getChannelId(), accountId, characterId, machineId, clientKey);
        remoteServerNode.write(CentralPacket.migrateResult(requestId, migrationResult.orElse(null)));
    }

    private void handleTransferRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Channel transfer - create migration request and reply with transfer info
        final int requestId = inPacket.decodeInt();
        final MigrationInfo migrationInfo = MigrationInfo.decode(inPacket);
        // Resolve target channel
        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(migrationInfo.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", migrationInfo.getChannelId() + 1);
            remoteServerNode.write(CentralPacket.transferResult(requestId, null));
            return;
        }
        final RemoteServerNode targetNode = targetNodeResult.get();
        // Submit migration request
        if (!centralServerNode.submitMigrationRequest(migrationInfo)) {
            log.error("Failed to submit migration request for character ID : {}", migrationInfo.getCharacterId());
            remoteServerNode.write(CentralPacket.transferResult(requestId, null));
            return;
        }
        // Reply with transfer info
        remoteServerNode.write(CentralPacket.transferResult(requestId, new TransferInfo(
                targetNode.getChannelHost(),
                targetNode.getChannelPort()
        )));
    }

    private void updatePartyMember(RemoteUser remoteUser) {
        final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
        if (partyResult.isEmpty()) {
            return;
        }
        try (var lockedParty = partyResult.get().acquire()) {
            // Check if still in party
            final Party party = lockedParty.get();
            if (!party.hasMember(remoteUser.getCharacterId())) {
                remoteUser.setPartyId(0);
                return;
            }
            // Update user for all members
            party.updateMember(remoteUser);
            final OutPacket outPacket = PartyPacket.loadPartyDone(party);
            forEachPartyMember(party, (member, node) -> {
                node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
            });
        }
    }

    private void handleUserConnect(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.addUser(remoteUser);
        updateMessengerUser(remoteUser);
        updatePartyMember(remoteUser);
        // Notify friends
        final Map<Integer, Friend> friendMap = loadFriends(remoteUser);
        final List<Integer> characterIds = friendMap.values().stream()
                .filter((f) -> f.getStatus() == FriendStatus.NORMAL)
                .map(Friend::getFriendId)
                .toList();
        final OutPacket outPacket = FriendPacket.notify(remoteUser.getCharacterId(), remoteUser.getChannelId(), false);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.userPacketBroadcast(characterIds, outPacket));
        }
    }

    private void handleUserUpdate(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.updateUser(remoteUser);
        updateMessengerUser(remoteUser);
        updatePartyMember(remoteUser);
    }

    private void handleUserDisconnect(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.removeUser(remoteUser);
        // Check if transfer
        if (centralServerNode.isMigrating(remoteUser.getAccountId())) {
            return;
        }
        // Leave messenger
        leaveMessenger(remoteUser);
        // Update party
        remoteUser.setChannelId(GameConstants.CHANNEL_OFFLINE);
        remoteUser.setFieldId(GameConstants.UNDEFINED_FIELD_ID);
        updatePartyMember(remoteUser);
        // Notify friends
        final Map<Integer, Friend> friendMap = loadFriends(remoteUser);
        final List<Integer> characterIds = friendMap.values().stream()
                .filter((f) -> f.getStatus() == FriendStatus.NORMAL)
                .map(Friend::getFriendId)
                .toList();
        final OutPacket outPacket = FriendPacket.notify(remoteUser.getCharacterId(), GameConstants.CHANNEL_OFFLINE, false);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.userPacketBroadcast(characterIds, outPacket));
        }
    }

    private void handleUserPacketRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final String characterName = inPacket.decodeString();
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        // Resolve target user
        final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterName(characterName);
        if (targetResult.isEmpty()) {
            return;
        }
        final RemoteUser target = targetResult.get();
        // Resolve target node
        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
            return;
        }
        // Send UserPacketReceive to target channel node
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
    }

    private void handleUserPacketReceive(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        // Resolve target user
        final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterId(characterId);
        if (targetResult.isEmpty()) {
            return;
        }
        final RemoteUser target = targetResult.get();
        // Resolve target node
        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
            return;
        }
        // Send UserPacketReceive to target channel node
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
    }

    private void handleUserPacketBroadcast(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final List<Integer> characterIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            characterIds.add(inPacket.decodeInt());
        }
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.userPacketBroadcast(characterIds, outPacket));
        }
    }

    private void handleUserQueryRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Resolve queried users
        final int requestId = inPacket.decodeInt();
        final int size = inPacket.decodeInt();
        final List<RemoteUser> remoteUsers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            final String characterName = inPacket.decodeString();
            centralServerNode.getUserByCharacterName(characterName).ifPresent(remoteUsers::add);
        }
        // Reply with queried remote users
        remoteServerNode.write(CentralPacket.userQueryResult(requestId, remoteUsers));
    }

    private void handleServerPacketBroadcast(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.serverPacketBroadcast(outPacket));
        }
    }

    private void handleMessengerRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final MessengerRequest messengerRequest = MessengerRequest.decode(inPacket);
        // Resolve requester user
        final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
        if (remoteUserResult.isEmpty()) {
            log.error("Failed to resolve user with character ID : {} for MessengerRequest", characterId);
            return;
        }
        final RemoteUser remoteUser = remoteUserResult.get();
        // Process request
        switch (messengerRequest.getRequestType()) {
            case MSMP_Enter -> {
                final int messengerId = messengerRequest.getMessengerId();
                final MessengerUser messengerUser = messengerRequest.getMessengerUser();
                // Check if already in messenger
                final Optional<Messenger> userMessengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (userMessengerResult.isPresent()) {
                    log.error("Tried to enter messenger ID {} while already in messenger ID {}", messengerId, remoteUser.getMessengerId());
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), BroadcastPacket.alert("This request has failed due to an unknown error.")));
                    return;
                }
                // Create messenger
                if (messengerId == 0) {
                    createMessenger(remoteServerNode, remoteUser, messengerUser);
                    return;
                }
                // Resolve messenger
                final Optional<Messenger> targetMessengerResult = centralServerNode.getMessengerById(messengerId);
                if (targetMessengerResult.isEmpty()) {
                    // Create messenger
                    createMessenger(remoteServerNode, remoteUser, messengerUser);
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.selfEnterResult(-1))); // You have been unable to join the invited chat room.
                    return;
                }
                // Join messenger
                try (var lockedMessenger = targetMessengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    if (!messenger.addUser(remoteUser, messengerUser)) {
                        // Create messenger
                        createMessenger(remoteServerNode, remoteUser, messengerUser);
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.selfEnterResult(-1))); // You have been unable to join the invited chat room.
                        return;
                    }
                    remoteUser.setMessengerId(messenger.getMessengerId());
                    remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), messenger.getMessengerId()));
                    // Update users
                    final int userIndex = messenger.getUserIndex(remoteUser);
                    final OutPacket outPacket = MessengerPacket.enter(userIndex, messengerUser, true);
                    forEachMessengerUser(messenger, (user, node) -> {
                        if (user.getCharacterId() == remoteUser.getCharacterId()) {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), MessengerPacket.selfEnterResult(userIndex)));
                            for (var entry : messenger.getMessengerUsers().entrySet()) {
                                if (entry.getKey() != userIndex) {
                                    node.write(CentralPacket.userPacketReceive(user.getCharacterId(), MessengerPacket.enter(entry.getKey(), entry.getValue(), false)));
                                }
                            }
                        } else {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
                        }
                    });
                }
            }
            case MSMP_Leave -> {
                leaveMessenger(remoteUser);
                remoteUser.setMessengerId(0);
                remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), 0));
            }
            case MSMP_Chat -> {
                final String message = messengerRequest.getMessage();
                // Resolve messenger
                final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (messengerResult.isEmpty()) {
                    log.error("Could not resolve messenger for MSMP_Chat");
                    return;
                }
                // Update users
                try (var lockedMessenger = messengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    final OutPacket outPacket = MessengerPacket.chat(message);
                    forEachMessengerUser(messenger, (user, node) -> {
                        if (user.getCharacterId() != remoteUser.getCharacterId()) {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
                        }
                    });
                }

            }
            case MSMP_Avatar -> {
                final MessengerUser messengerUser = messengerRequest.getMessengerUser();
                // Resolve messenger
                final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (messengerResult.isEmpty()) {
                    log.error("Could not resolve messenger for MSMP_Avatar");
                    return;
                }
                // Update users
                try (var lockedMessenger = messengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    final int userIndex = messengerResult.get().getUserIndex(remoteUser);
                    if (userIndex < 0) {
                        log.error("Could not update user avatar in messenger ID {}", messenger.getMessengerId());
                        return;
                    }
                    final OutPacket outPacket = MessengerPacket.avatar(userIndex, messengerUser.getAvatarLook());
                    forEachMessengerUser(messengerResult.get(), (user, node) -> {
                        if (user.getCharacterId() != remoteUser.getCharacterId()) {
                            node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
                        }
                    });
                }
            }
            case MSMP_Migrated -> {
                // Resolve messenger
                final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
                if (messengerResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), 0));
                    return;
                }
                try (var lockedMessenger = messengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    final int userIndex = messengerResult.get().getUserIndex(remoteUser);
                    if (userIndex < 0) {
                        log.error("Could not migrate in messenger ID {}", messenger.getMessengerId());
                        return;
                    }
                    for (var entry : messenger.getMessengerUsers().entrySet()) {
                        if (entry.getKey() != userIndex) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.enter(entry.getKey(), entry.getValue(), false)));
                        }
                    }
                }
            }
        }
    }

    private void handlePartyRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final PartyRequest partyRequest = PartyRequest.decode(inPacket);
        // Resolve requester user
        final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
        if (remoteUserResult.isEmpty()) {
            log.error("Failed to resolve user with character ID : {} for PartyRequest", characterId);
            return;
        }
        final RemoteUser remoteUser = remoteUserResult.get();
        // Process request
        switch (partyRequest.getRequestType()) {
            case LoadParty -> {
                // Remote user party ID is set on UserConnect
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), null));
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    final Party party = lockedParty.get();
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser)));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.loadPartyDone(party)));
                }
            }
            case CreateNewParty -> {
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isPresent()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.CreateNewParty_AlreadyJoined))); // Already have joined a party.
                    return;
                }
                // New party id
                final Optional<Integer> partyIdResult = DatabaseManager.idAccessor().nextPartyId();
                if (partyIdResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg("Your request has failed. Please try again later")));
                    return;
                }
                // Create party
                final Party party = centralServerNode.createNewParty(partyIdResult.get(), remoteUser);
                remoteUser.setPartyId(party.getPartyId());
                remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser)));
                remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.createNewPartyDone(party, remoteUser.getTownPortal()))); // You have created a new party.
            }
            case WithdrawParty -> {
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.WithdrawParty_NotJoined))); // You have yet to join a party.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    final Party party = lockedParty.get();
                    if (party.getPartyBossId() == remoteUser.getCharacterId()) {
                        // Disband party
                        if (!centralServerNode.removeParty(party)) {
                            log.error("Failed to disband party {}", party.getPartyId());
                            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.WithdrawParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                            return;
                        }
                        // Broadcast disband packet to party and update party ids
                        final OutPacket outPacket = PartyPacket.withdrawPartyDone(party, remoteUser, true, false); // You have quit as the leader of the party. The party has been disbanded. | You have left the party since the party leader quit.
                        forEachPartyMember(party, (member, node) -> {
                            member.setPartyId(0);
                            node.write(CentralPacket.partyResult(member.getCharacterId(), null));
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                        });
                    } else {
                        // Remove member
                        if (!party.removeMember(remoteUser)) {
                            log.error("Failed to remove member with character ID {} from party {}", remoteUser.getCharacterId(), party.getPartyId());
                            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.WithdrawParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                            return;
                        }
                        // Broadcast withdraw packet to party
                        final OutPacket outPacket = PartyPacket.withdrawPartyDone(party, remoteUser, false, false); // You have left the party. | '%s' have left the party.
                        forEachPartyMember(party, (member, node) -> {
                            node.write(CentralPacket.partyResult(member.getCharacterId(), party.createInfo(member))); // update member index
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                        });
                        // Update user
                        remoteUser.setPartyId(0);
                        remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), null));
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), outPacket));
                    }
                }
            }
            case JoinParty -> {
                // Check current party
                if (remoteUser.getPartyId() != 0) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_Unknown))); // Already have joined a party.
                    return;
                }
                // Resolve inviter
                final int inviterId = partyRequest.getCharacterId();
                final Optional<RemoteUser> inviterResult = centralServerNode.getUserByCharacterId(inviterId);
                if (inviterResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                    return;
                }
                final Optional<Party> partyResult = centralServerNode.getPartyById(inviterResult.get().getPartyId());
                if (partyResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    final Party party = lockedParty.get();
                    if (!party.addMember(remoteUser)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_AlreadyFull))); // The party you're trying to join is already in full capacity.
                        return;
                    }
                    // Broadcast join packet to party
                    final OutPacket outPacket = PartyPacket.joinPartyDone(party, remoteUser); // You have joined the party | '%s' has joined the party.
                    forEachPartyMember(party, (member, node) -> {
                        if (member.getCharacterId() == remoteUser.getCharacterId()) {
                            member.setPartyId(party.getPartyId());
                            node.write(CentralPacket.partyResult(member.getCharacterId(), party.createInfo(member)));
                        }
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                }
            }
            case InviteParty -> {
                // Resolve party
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isEmpty()) {
                    // New party id
                    final Optional<Integer> partyIdResult = DatabaseManager.idAccessor().nextPartyId();
                    if (partyIdResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg("Your request has failed. Please try again later")));
                        return;
                    }
                    // Create party
                    final Party party = centralServerNode.createNewParty(partyIdResult.get(), remoteUser);
                    remoteUser.setPartyId(party.getPartyId());
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser)));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.createNewPartyDone(party, remoteUser.getTownPortal()))); // You have created a new party.
                }
                // Resolve target
                final String targetName = partyRequest.getCharacterName();
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterName(targetName); // target name
                if (targetResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                final RemoteUser target = targetResult.get();
                if (target.getPartyId() != 0) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg(String.format("'%s' is already in a party.", targetName))));
                    return;
                }
                // Resolve target node
                final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                // Send party invite to target
                targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), PartyPacket.inviteParty(remoteUser)));
            }
            case KickParty -> {
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.KickParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    // Check that the kick request is valid
                    final Party party = lockedParty.get();
                    final int targetId = partyRequest.getCharacterId();
                    final Optional<RemoteUser> targetMember = party.getMember(targetId);
                    if (party.getPartyBossId() != remoteUser.getCharacterId() || targetMember.isEmpty() || !party.removeMember(targetMember.get())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.KickParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                        return;
                    }
                    // Broadcast kick packet to party
                    final OutPacket outPacket = PartyPacket.withdrawPartyDone(party, targetMember.get(), false, true); // You have been expelled from the party. | '%s' have been expelled from the party.
                    forEachPartyMember(party, (member, node) -> {
                        if (member.getCharacterId() == targetId) {
                            member.setPartyId(0);
                            node.write(CentralPacket.partyResult(member.getCharacterId(), null));
                        }
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                }
            }
            case ChangePartyBoss -> {
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, PartyPacket.of(PartyResultType.ChangePartyBoss_Unknown))); // Your request for a party didn't work due to an unexpected error.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    // Try setting new party boss
                    final Party party = lockedParty.get();
                    final int targetId = partyRequest.getCharacterId();
                    if (!party.setPartyBossId(remoteUser.getCharacterId(), targetId)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, PartyPacket.of(PartyResultType.ChangePartyBoss_Unknown))); // Your request for a party didn't work due to an unexpected error.
                        return;
                    }
                    // Broadcast packet to party
                    final OutPacket outPacket = PartyPacket.changePartyBossDone(targetId, false); // %s has become the leader of the party.
                    forEachPartyMember(party, (member, node) -> {
                        node.write(CentralPacket.partyResult(member.getCharacterId(), party.createInfo(member)));
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                }
            }
        }
    }

    private void handleFriendRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final FriendRequest friendRequest = FriendRequest.decode(inPacket);
        // Resolve requester user
        final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
        if (remoteUserResult.isEmpty()) {
            log.error("Failed to resolve user with character ID : {} for FriendRequest", characterId);
            return;
        }
        final RemoteUser remoteUser = remoteUserResult.get();
        switch (friendRequest.getRequestType()) {
            case LoadFriend -> {
                final Map<Integer, Friend> friendMap = loadFriends(remoteUser);
                remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.loadFriendDone(friendMap.values())));
            }
            case SetFriend -> {
                final Map<Integer, Friend> friendMap = loadFriends(remoteUser);
                final Optional<Friend> friendResult = friendMap.values().stream().filter((f) -> f.getFriendName().equals(friendRequest.getTargetName())).findFirst();
                if (friendResult.isPresent() && friendResult.get().getStatus() == FriendStatus.NORMAL) {
                    // Update friend group
                    final Friend friend = friendResult.get();
                    friend.setFriendGroup(friendRequest.getFriendGroup());
                    if (!DatabaseManager.friendAccessor().saveFriend(friend, true)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendUnknown())); // The request was denied due to an unknown error.
                        return;
                    }
                    // Update client
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendDone(friendMap.values())));
                } else {
                    // Create new friend, resolve target info
                    final Optional<CharacterInfo> characterInfoResult = DatabaseManager.characterAccessor().getCharacterInfoByName(friendRequest.getTargetName());
                    if (characterInfoResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendUnknownUser())); // That character is not registered.
                        return;
                    }
                    final int targetCharacterId = characterInfoResult.get().getCharacterId();
                    final String targetCharacterName = characterInfoResult.get().getCharacterName();
                    // Check if target can be added as a friend
                    if (friendMap.size() >= friendRequest.getFriendMax()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendFullMe())); // Your buddy list is full.
                        return;
                    }
                    if (friendMap.containsKey(targetCharacterId)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendAlreadySet())); // That character is already registered as your buddy.
                        return;
                    }
                    // Add target as friend, force creation
                    final Friend friendForUser = new Friend(remoteUser.getCharacterId(), targetCharacterId, targetCharacterName, friendRequest.getFriendGroup(), FriendStatus.NORMAL);
                    if (!DatabaseManager.friendAccessor().saveFriend(friendForUser, true)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendUnknown())); // The request was denied due to an unknown error.
                        return;
                    }
                    // Update client
                    friendMap.put(targetCharacterId, friendForUser);
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendDone(friendMap.values())));
                    // Add user as a friend for target, not forced - existing friends, requests, and refused records
                    final Friend friendForTarget = new Friend(targetCharacterId, remoteUser.getCharacterId(), remoteUser.getCharacterName(), GameConstants.DEFAULT_FRIEND_GROUP, FriendStatus.REQUEST);
                    if (DatabaseManager.friendAccessor().saveFriend(friendForTarget, false)) {
                        // Send invite to target if request was created
                        final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterId(targetCharacterId);
                        if (targetResult.isPresent()) {
                            final RemoteUser target = targetResult.get();
                            friendForUser.setChannelId(target.getChannelId());
                            final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
                            targetNodeResult.ifPresent(serverNode -> serverNode.write(CentralPacket.userPacketReceive(target.getCharacterId(), FriendPacket.invite(friendForTarget))));
                        }
                    }
                }

            }
            case AcceptFriend -> {
                final Map<Integer, Friend> friendMap = loadFriends(remoteUser);
                final Friend friend = friendMap.get(friendRequest.getFriendId());
                if (friend == null) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.acceptFriendUnknown())); // The request was denied due to an unknown error.
                    return;
                }
                friend.setStatus(FriendStatus.NORMAL);
                if (!DatabaseManager.friendAccessor().saveFriend(friend, true)) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.acceptFriendUnknown())); // The request was denied due to an unknown error.
                    return;
                }
                // Notify target if online
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterId(friend.getFriendId());
                if (targetResult.isPresent()) {
                    final RemoteUser target = targetResult.get();
                    friend.setChannelId(target.getChannelId());
                    final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
                    targetNodeResult.ifPresent(serverNode -> serverNode.write(CentralPacket.userPacketReceive(target.getCharacterId(), FriendPacket.notify(remoteUser.getCharacterId(), remoteUser.getChannelId(), false))));
                }
                // Update client
                remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.setFriendDone(friendMap.values())));
            }
            case DeleteFriend -> {
                final Map<Integer, Friend> friendMap = loadFriends(remoteUser);
                final Friend friend = friendMap.get(friendRequest.getFriendId());
                if (friend == null) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.deleteFriendUnknown())); // The request was denied due to an unknown error.
                    return;
                }
                // Delete friend
                if (!DatabaseManager.friendAccessor().deleteFriend(remoteUser.getCharacterId(), friend.getFriendId())) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.deleteFriendUnknown())); // The request was denied due to an unknown error.
                    return;
                }
                // Notify deleted friend if online
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterId(friend.getFriendId());
                if (targetResult.isPresent()) {
                    final RemoteUser target = targetResult.get();
                    final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
                    targetNodeResult.ifPresent(serverNode -> serverNode.write(CentralPacket.userPacketReceive(target.getCharacterId(), FriendPacket.notify(remoteUser.getCharacterId(), GameConstants.CHANNEL_OFFLINE, false))));
                }
                // Update client
                friendMap.remove(friend.getFriendId());
                remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), FriendPacket.deleteFriendDone(friendMap.values())));
            }
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    private void createMessenger(RemoteServerNode remoteServerNode, RemoteUser remoteUser, MessengerUser messengerUser) {
        final Messenger newMessenger = centralServerNode.createNewMessenger(remoteUser, messengerUser);
        remoteUser.setMessengerId(newMessenger.getMessengerId());
        remoteServerNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), newMessenger.getMessengerId()));
    }

    private void leaveMessenger(RemoteUser remoteUser) {
        final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
        if (messengerResult.isEmpty()) {
            return;
        }
        try (var lockedMessenger = messengerResult.get().acquire()) {
            final Messenger messenger = lockedMessenger.get();
            final int userIndex = messenger.removeUser(remoteUser);
            if (userIndex < 0) {
                log.error("Could not remove user from messenger ID : {}", messenger.getMessengerId());
                return;
            }
            // Check if empty
            if (messenger.getMessengerUsers().isEmpty()) {
                if (!centralServerNode.removeMessenger(messenger)) {
                    log.error("Could not remove messenger ID : {}", messenger.getMessengerId());
                }
                return;
            }
            // Update users
            final OutPacket outPacket = MessengerPacket.leave(userIndex);
            forEachMessengerUser(messenger, (user, node) -> {
                node.write(CentralPacket.userPacketReceive(user.getCharacterId(), outPacket));
            });
        }
    }

    private void updateMessengerUser(RemoteUser remoteUser) {
        final Optional<Messenger> messengerResult = centralServerNode.getMessengerById(remoteUser.getMessengerId());
        if (messengerResult.isEmpty()) {
            return;
        }
        try (var lockedMessenger = messengerResult.get().acquire()) {
            lockedMessenger.get().updateUser(remoteUser);
        }
    }

    private void forEachMessengerUser(Messenger messenger, BiConsumer<RemoteUser, RemoteServerNode> biConsumer) {
        messenger.forEachUser((member) -> {
            final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                return;
            }
            biConsumer.accept(member, targetNodeResult.get());
        });
    }

    private void forEachPartyMember(Party party, BiConsumer<RemoteUser, RemoteServerNode> biConsumer) {
        party.forEachMember((member) -> {
            final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                return;
            }
            biConsumer.accept(member, targetNodeResult.get());
        });
    }

    private Map<Integer, Friend> loadFriends(RemoteUser remoteUser) {
        final Map<Integer, Friend> friendMap = new HashMap<>();
        for (Friend friend : DatabaseManager.friendAccessor().getFriendsByCharacterId(remoteUser.getCharacterId())) {
            friendMap.put(friend.getFriendId(), friend);
        }
        for (Friend mutualFriend : DatabaseManager.friendAccessor().getFriendsByFriendId(remoteUser.getCharacterId())) {
            if (mutualFriend.getStatus() != FriendStatus.NORMAL) {
                continue;
            }
            final Friend friend = friendMap.get(mutualFriend.getCharacterId());
            if (friend == null || friend.getStatus() != FriendStatus.NORMAL) {
                continue;
            }
            final Optional<RemoteUser> friendUserResult = centralServerNode.getUserByCharacterId(friend.getFriendId());
            friendUserResult.ifPresent((friendUser) -> friend.setChannelId(friendUser.getChannelId()));
        }
        return friendMap;
    }
}
