package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.database.DatabaseManager;
import kinoko.packet.CentralPacket;
import kinoko.packet.field.MessengerPacket;
import kinoko.packet.world.AlliancePacket;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.GuildPacket;
import kinoko.packet.world.PartyPacket;
import kinoko.server.alliance.*;
import kinoko.server.guild.*;
import kinoko.server.header.CentralHeader;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;
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
import kinoko.world.user.GuildInfo;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                case WorldSpeakerRequest -> handleWorldSpeakerRequest(remoteServerNode, inPacket);
                case ServerPacketBroadcast -> handleServerPacketBroadcast(remoteServerNode, inPacket);
                case MessengerRequest -> handleMessengerRequest(remoteServerNode, inPacket);
                case PartyRequest -> handlePartyRequest(remoteServerNode, inPacket);
                case GuildRequest -> handleGuildRequest(remoteServerNode, inPacket);
                case AllianceRequest -> handleAllianceRequest(remoteServerNode, inPacket);
                case BoardRequest -> handleBoardRequest(remoteServerNode, inPacket);
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

    private void handleUserConnect(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.addUser(remoteUser);
        updateMessengerUser(remoteUser);
        updatePartyMember(remoteUser, false);
        updateGuildMember(remoteUser, false);
    }

    private void handleUserUpdate(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.updateUser(remoteUser);
        updateMessengerUser(remoteUser);
        updatePartyMember(remoteUser, true);
        updateGuildMember(remoteUser, true);
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
        // Update party and guild
        remoteUser.setChannelId(GameConstants.CHANNEL_OFFLINE);
        remoteUser.setFieldId(GameConstants.UNDEFINED_FIELD_ID);
        updatePartyMember(remoteUser, false);
        updateGuildMember(remoteUser, false);
    }

    private void handleUserPacketRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final String characterName = inPacket.decodeString();
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
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
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), remotePacket));
    }

    private void handleUserPacketReceive(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
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
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), remotePacket));
    }

    private void handleUserPacketBroadcast(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final List<Integer> characterIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            characterIds.add(inPacket.decodeInt());
        }
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.userPacketBroadcast(characterIds, remotePacket));
        }
    }

    private void handleUserQueryRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Resolve queried users
        final int requestId = inPacket.decodeInt();
        final int size = inPacket.decodeInt();
        final List<RemoteUser> remoteUsers;
        if (size < 0) {
            remoteUsers = centralServerNode.getUsers(); // Get all connected users
        } else {
            remoteUsers = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                final String characterName = inPacket.decodeString();
                centralServerNode.getUserByCharacterName(characterName).ifPresent(remoteUsers::add);
            }
        }
        // Reply with queried remote users
        remoteServerNode.write(CentralPacket.userQueryResult(requestId, remoteUsers));
    }

    private void handleWorldSpeakerRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final boolean avatar = inPacket.decodeBoolean();
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.worldSpeakerRequest(characterId, avatar, remotePacket));
        }
    }

    private void handleServerPacketBroadcast(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.serverPacketBroadcast(remotePacket));
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
                final Optional<Party> partyResult = centralServerNode.getPartyById(partyRequest.getPartyId());
                if (partyResult.isEmpty()) {
                    remoteUser.setPartyId(0);
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), null, null));
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    final Party party = lockedParty.get();
                    if (!party.hasMember(remoteUser.getCharacterId())) {
                        remoteUser.setPartyId(0);
                        remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), null, null));
                        return;
                    }
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser), PartyPacket.loadPartyDone(party)));
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
                try (var lockedParty = party.acquire()) {
                    remoteUser.setPartyId(party.getPartyId());
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser), PartyPacket.createNewPartyDone(party, remoteUser.getTownPortal()))); // You have created a new party.
                }
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
                            node.write(CentralPacket.partyResult(member.getCharacterId(), null, outPacket));
                        });
                    } else {
                        // Remove member
                        if (!party.removeMember(remoteUser)) {
                            log.error("Failed to remove member with character ID {} from party {}", remoteUser.getCharacterId(), party.getPartyId());
                            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.WithdrawParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                            return;
                        }
                        // Update user
                        final OutPacket outPacket = PartyPacket.withdrawPartyDone(party, remoteUser, false, false);
                        remoteUser.setPartyId(0);
                        remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), null, outPacket)); // You have left the party.
                        // Broadcast withdraw packet to party
                        forEachPartyMember(party, (member, node) -> {
                            node.write(CentralPacket.partyResult(member.getCharacterId(), party.createInfo(member), outPacket)); // '%s' have left the party.
                        });
                    }
                }
            }
            case JoinParty -> {
                // Check current party
                if (remoteUser.getPartyId() != 0) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_AlreadyJoined))); // Already have joined a party.
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
                    // Check if invite was valid
                    if (!party.unregisterInvite(inviterId, remoteUser.getCharacterId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                        return;
                    }
                    // Add user to party
                    if (!party.addMember(remoteUser)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_AlreadyFull))); // The party you're trying to join is already in full capacity.
                        return;
                    }
                    // Update user
                    final OutPacket outPacket = PartyPacket.joinPartyDone(party, remoteUser);
                    remoteUser.setPartyId(party.getPartyId());
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser), outPacket)); // You have joined the party
                    // Broadcast join packet to party
                    forEachPartyMember(party, (member, node) -> {
                        if (member.getCharacterId() != remoteUser.getCharacterId()) {
                            node.write(CentralPacket.partyResult(member.getCharacterId(), party.createInfo(member), outPacket)); // '%s' has joined the party.
                        }
                    });
                }
            }
            case InviteParty -> {
                // Resolve party
                final Party party;
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isPresent()) {
                    party = partyResult.get();
                } else {
                    // New party id
                    final Optional<Integer> partyIdResult = DatabaseManager.idAccessor().nextPartyId();
                    if (partyIdResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg("Your request has failed. Please try again later")));
                        return;
                    }
                    // Create party
                    party = centralServerNode.createNewParty(partyIdResult.get(), remoteUser);
                    remoteUser.setPartyId(party.getPartyId());
                    remoteServerNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser), PartyPacket.createNewPartyDone(party, remoteUser.getTownPortal()))); // You have created a new party.
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
                final RemoteServerNode targetNode = targetNodeResult.get();
                // Check if target can be added to party
                try (var lockedParty = party.acquire()) {
                    if (!party.canAddMember(target)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg("Your request has failed. Please try again later")));
                        return;
                    }
                    // Register party invite and write invite packet to client
                    party.registerInvite(remoteUser.getCharacterId(), target.getCharacterId());
                    targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), PartyPacket.inviteParty(remoteUser)));
                }
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
                    final Optional<RemoteUser> targetMemberResult = party.getMember(targetId);
                    if (party.getPartyBossId() != remoteUser.getCharacterId() || targetMemberResult.isEmpty() || !party.removeMember(targetMemberResult.get())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.KickParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                        return;
                    }
                    final RemoteUser targetMember = targetMemberResult.get();
                    targetMember.setPartyId(0);
                    // Update client
                    final OutPacket outPacket = PartyPacket.withdrawPartyDone(party, targetMember, false, true);
                    final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(targetMember.getChannelId());
                    if (targetNodeResult.isPresent()) {
                        final RemoteServerNode node = targetNodeResult.get();
                        node.write(CentralPacket.partyResult(targetMember.getCharacterId(), null, outPacket)); // You have been expelled from the party.
                    }
                    // Broadcast kick packet to party
                    forEachPartyMember(party, (member, node) -> {
                        node.write(CentralPacket.partyResult(member.getCharacterId(), party.createInfo(member), outPacket)); // '%s' have been expelled from the party.
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
                    final OutPacket outPacket = PartyPacket.changePartyBossDone(targetId, partyRequest.isDisconnect()); // Due to the party leader disconnecting from the game %s has been assigned as the new leader. | %s has become the leader of the party.
                    forEachPartyMember(party, (member, node) -> {
                        node.write(CentralPacket.partyResult(member.getCharacterId(), party.createInfo(member), outPacket));
                    });
                }
            }
        }
    }

    private void handleGuildRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final GuildRequest guildRequest = GuildRequest.decode(inPacket);
        // Resolve requester user
        final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
        if (remoteUserResult.isEmpty()) {
            log.error("Failed to resolve user with character ID : {} for GuildRequest", characterId);
            return;
        }
        final RemoteUser remoteUser = remoteUserResult.get();
        switch (guildRequest.getRequestType()) {
            case LoadGuild -> {
                // Load guild from storage / database
                final int guildId = guildRequest.getGuildId() != 0 ? guildRequest.getGuildId() : remoteUser.getGuildId();
                final Optional<Guild> guildResult = centralServerNode.getGuildById(guildId);
                if (guildResult.isEmpty()) {
                    remoteUser.setGuildId(0);
                    remoteServerNode.write(CentralPacket.guildResult(characterId, null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.loadGuildDone(null)));
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    if (!guild.hasMember(characterId)) {
                        remoteUser.setGuildId(0);
                        remoteServerNode.write(CentralPacket.guildResult(characterId, null));
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.loadGuildDone(null)));
                        return;
                    }
                    guild.updateMember(remoteUser);
                    remoteUser.setGuildId(guild.getGuildId());
                    remoteServerNode.write(CentralPacket.guildResult(characterId, GuildInfo.from(guild, characterId)));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.loadGuildDone(guild)));
                }
            }
            case CreateNewGuild -> {
                // Create new guild in storage + database
                final Optional<Guild> guildResult = centralServerNode.createNewGuild(guildRequest.getGuildId(), guildRequest.getGuildName(), remoteUser);
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.guildResult(characterId, null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.createNewGuildUnknown())); // The problem has happened during the process of forming the guild... Plese try again later..
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    remoteUser.setGuildId(guild.getGuildId());
                    remoteServerNode.write(CentralPacket.guildResult(characterId, GuildInfo.from(guild, characterId)));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.createNewGuildDone(guild)));
                }
            }
            case InviteGuild -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                // Resolve target
                final String targetName = guildRequest.getTargetName();
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterName(targetName); // target name
                if (targetResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                final RemoteUser target = targetResult.get();
                if (target.getGuildId() != 0) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(String.format("'%s' is already in a guild.", targetName))));
                    return;
                }
                // Resolve target node
                final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                final RemoteServerNode targetNode = targetNodeResult.get();
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    final GuildRank guildRank = guild.getMember(characterId).getGuildRank();
                    if (guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Check capacity
                    if (!guild.canAddMember(target.getCharacterId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You cannot invite more people to the guild.")));
                        return;
                    }
                    // Register invite and send guild invite to target
                    guild.registerInvite(characterId, target.getCharacterId());
                    targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), GuildPacket.inviteGuild(remoteUser)));
                }
            }
            case JoinGuild -> {
                if (remoteUser.getGuildId() != 0) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You have already joined a guild.")));
                    return;
                }
                // Resolve inviter
                final int inviterId = guildRequest.getInviterId();
                final Optional<RemoteUser> inviterResult = centralServerNode.getUserByCharacterId(inviterId);
                if (inviterResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.joinGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                final RemoteUser inviter = inviterResult.get();
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(inviter.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.joinGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check if user has been invited by the inviter
                    if (!guild.unregisterInvite(inviterId, characterId)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.joinGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    // Try adding user as a member to the guild
                    final GuildMember newMember = GuildMember.from(remoteUser);
                    newMember.setGuildRank(GuildRank.MEMBER3);
                    if (!guild.addMember(newMember)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.joinGuildAlreadyFull())); // The guild you are trying to join has already reached the max number of users.
                        return;
                    }
                    // Update user
                    remoteUser.setGuildId(guild.getGuildId());
                    remoteServerNode.write(CentralPacket.guildResult(characterId, GuildInfo.from(guild, characterId)));
                    // Update members
                    final OutPacket outPacket = GuildPacket.joinGuildDone(guild.getGuildId(), newMember);
                    forEachGuildMember(guild, (member, node) -> {
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                }
            }
            case WithdrawGuild -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(guildRequest.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.guildResult(characterId, null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.withdrawGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check if user can leave the guild
                    if (!guild.hasMember(characterId)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.withdrawGuildNotJoined())); // You are not in the guild.
                        return;
                    }
                    final GuildMember removedMember = guild.getMember(characterId);
                    if (removedMember.getGuildRank() == GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You cannot quit the guild since you are the master of the guild.")));
                        return;
                    }
                    // Remove user from guild
                    guild.removeMember(removedMember);
                    // Update members
                    final OutPacket outPacket = GuildPacket.withdrawGuildDone(guild.getGuildId(), removedMember);
                    remoteUser.setGuildId(0);
                    remoteServerNode.write(CentralPacket.guildResult(characterId, null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, outPacket));
                    forEachGuildMember(guild, (member, node) -> {
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                }
            }
            case KickGuild -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.kickGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    final GuildRank guildRank = guild.getMember(characterId).getGuildRank();
                    if (guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Check if target can be kicked
                    if (!guild.hasMember(guildRequest.getTargetId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(String.format("Unable to find '%s'", guildRequest.getTargetName()))));
                        return;
                    }
                    final GuildMember targetMember = guild.getMember(guildRequest.getTargetId());
                    if (targetMember.getGuildRank() == GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You may not ban the Guild Master.")));
                        return;
                    }
                    if (targetMember.getGuildRank() == GuildRank.SUBMASTER && guildRank != GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("Only Guild Master can expel Jr.Masters.")));
                        return;
                    }
                    // Remove target from guild
                    guild.removeMember(targetMember);
                    // Update members
                    final OutPacket outPacket = GuildPacket.kickGuildDone(guild.getGuildId(), targetMember);
                    forEachGuildMember(guild, (member, node) -> {
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                    // Resolve target
                    final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterId(guildRequest.getTargetId());
                    if (targetResult.isPresent()) {
                        final RemoteUser targetUser = targetResult.get();
                        targetUser.setGuildId(0);
                        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(targetUser.getChannelId());
                        if (targetNodeResult.isEmpty()) {
                            log.error("Could not resolve server node for kicked guild member");
                            return;
                        }
                        targetNodeResult.get().write(CentralPacket.guildResult(targetUser.getCharacterId(), null));
                        targetNodeResult.get().write(CentralPacket.userPacketReceive(targetUser.getCharacterId(), outPacket)); // You have been expelled from the guild.
                    } else {
                        // Write memo
                        final Optional<Integer> memoIdResult = DatabaseManager.idAccessor().nextMemoId();
                        if (memoIdResult.isEmpty()) {
                            log.error("Could not resolve memo ID for kicked guild member");
                            return;
                        }
                        final Memo memo = new Memo(MemoType.DEFAULT, memoIdResult.get(), remoteUser.getCharacterName(), "You have been expelled from the guild.", Instant.now());
                        if (!DatabaseManager.memoAccessor().newMemo(memo, guildRequest.getTargetId())) {
                            log.error("Could not create memo for kicked guild member");
                        }
                    }
                }
            }
            case RemoveGuild -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(guildRequest.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.guildResult(characterId, null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.removeGuildUnknown())); // The problem has happened during the process of disbanding the guild... Plese try again later...
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    if (guild.getMember(characterId).getGuildRank() != GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Remove guild from storage + database
                    if (!centralServerNode.removeGuild(guild)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.removeGuildUnknown())); // The problem has happened during the process of disbanding the guild... Plese try again later...
                        return;
                    }
                    // Update members
                    final OutPacket outPacket = GuildPacket.removeGuildDone(guild.getGuildId());
                    forEachGuildMember(guild, (member, node) -> {
                        member.setGuildId(0);
                        node.write(CentralPacket.guildResult(member.getCharacterId(), null));
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                }
            }
            case IncMaxMemberNum -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.incMaxMemberNumUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    if (guild.getMember(characterId).getGuildRank() != GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Check that max member can be increased
                    if (guild.getMemberMax() >= GameConstants.GUILD_CAPACITY_MAX || guild.getMemberMax() >= guildRequest.getMemberMax()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.incMaxMemberNumUnknown())); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    // Set guild capacity
                    guild.setMemberMax(guildRequest.getMemberMax());
                    // Update members
                    final OutPacket outPacket = GuildPacket.incMaxMemberNumDone(guild.getGuildId(), guild.getMemberMax());
                    forEachGuildMember(guild, (member, node) -> {
                        node.write(CentralPacket.guildResult(member.getCharacterId(), GuildInfo.from(guild, member.getCharacterId())));
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                }
            }
            case SetGradeName -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.setGradeNameUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Set grade names and update members
                    guild.setGradeNames(guildRequest.getGradeNames());
                    final OutPacket outPacket = GuildPacket.setGradeNameDone(guild.getGuildId(), guild.getGradeNames());
                    forEachGuildMember(guild, (member, node) -> {
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                }
            }
            case SetMemberGrade -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.setMemberGradeUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Resolve target
                    if (!guild.hasMember(guildRequest.getTargetId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.setMemberGradeUnknown())); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    final GuildMember targetMember = guild.getMember(guildRequest.getTargetId());
                    // Check if requester can modify target's rank
                    if (targetMember.getGuildRank() == GuildRank.MASTER || guildRequest.getGuildRank() == GuildRank.MASTER || guildRequest.getGuildRank() == GuildRank.NONE) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.setMemberGradeUnknown())); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    final GuildRank requesterRank = guild.getMember(characterId).getGuildRank();
                    if ((requesterRank != GuildRank.MASTER && requesterRank != GuildRank.SUBMASTER) ||
                            (requesterRank != GuildRank.MASTER && targetMember.getGuildRank() == GuildRank.SUBMASTER) ||
                            (requesterRank != GuildRank.MASTER && guildRequest.getGuildRank() == GuildRank.SUBMASTER)) {
                        // Non-master/submaster trying to modify rank; Non-master trying to modify rank of submaster; Non-master trying to modify rank to submaster
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Update rank
                    targetMember.setGuildRank(guildRequest.getGuildRank());
                    // Update members
                    final OutPacket outPacket = GuildPacket.setMemberGradeDone(guild.getGuildId(), targetMember);
                    forEachGuildMember(guild, (member, node) -> {
                        if (member.getCharacterId() == targetMember.getCharacterId()) {
                            // Update target's guild rank
                            node.write(CentralPacket.guildResult(member.getCharacterId(), GuildInfo.from(guild, member.getCharacterId())));
                        }
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                }
            }
            case SetMark -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.setMarkUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Apply new mark
                    guild.setMarkBg(guildRequest.getMarkBg());
                    guild.setMarkBgColor(guildRequest.getMarkBgColor());
                    guild.setMark(guildRequest.getMark());
                    guild.setMarkColor(guildRequest.getMarkColor());
                    // Update members
                    final OutPacket outPacket = GuildPacket.setMarkDone(guild.getGuildId(), guild.getMarkBg(), guild.getMarkBgColor(), guild.getMark(), guild.getMarkColor());
                    forEachGuildMember(guild, (member, node) -> {
                        node.write(CentralPacket.guildResult(member.getCharacterId(), GuildInfo.from(guild, member.getCharacterId())));
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                }
            }
            case SetNotice -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.setMarkUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Set notice and update members
                    guild.setNotice(guildRequest.getGuildNotice());
                    final OutPacket outPacket = GuildPacket.setNoticeDone(guild.getGuildId(), guild.getNotice());
                    forEachGuildMember(guild, (member, node) -> {
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                    });
                    // Save to database
                    DatabaseManager.guildAccessor().saveGuild(guild);
                }
            }
        }
    }

    private void handleAllianceRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final AllianceRequest allianceRequest = AllianceRequest.decode(inPacket);
        // Resolve requester user
        final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
        if (remoteUserResult.isEmpty()) {
            log.error("Failed to resolve user with character ID : {} for AllianceRequest", characterId);
            return;
        }
        final RemoteUser remoteUser = remoteUserResult.get();
        // Resolve guild
        final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
        if (guildResult.isEmpty()) {
            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not in a guild yet.")));
            return;
        }
        
        final Guild guild = guildResult.get();
        switch (allianceRequest.getRequestType()) {
	        case Create -> {
	        	String allianceName = allianceRequest.getGuildName();
	        	// Check if alliance name is available
	            if (!DatabaseManager.allianceAccessor().checkAllianceNameAvailable(allianceName)) {
	            	remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("Alliance Name is already in use. Please use another one.")));
	                return;
	            }
	            
	            int otherGuildId = allianceRequest.getGuildId();
	            final Optional<Guild> otherGuildResult = centralServerNode.getGuildById(otherGuildId);
	            
	            if (otherGuildResult.isEmpty()) {
	            	remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("Player not in a guild.")));
	            	return;
	            }
	            
	            if (guild.getGuildId() == otherGuildId) {
	            	remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("Cannot form an alliance for the same guild.")));
	            	return;
	            }
	        	
	        	// Resolve new alliance ID
	            final Optional<Integer> allianceIdResult = DatabaseManager.idAccessor().nextAllianceId();
	            if (allianceIdResult.isEmpty()) {
	            	OutPacket outPacket = GuildPacket.serverMsg(null); // The guild request has not been accepted due to unknown reason.
	            	remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), outPacket));
	                log.error("Database error: allianceId");
	                return;
	            }
	            
	            int allianceId = allianceIdResult.get();
	        	Optional<Alliance> allianceResult = centralServerNode.createNewAlliance(allianceId, allianceName, remoteUser);
	        	if (allianceResult.isEmpty()) {
	        		OutPacket outPacket = GuildPacket.serverMsg(null); // The guild request has not been accepted due to unknown reason.
	            	remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), outPacket));
	            	
	        		log.error("Could not create alliance '{}' for user '{}'", allianceName, remoteUser.getCharacterName());
	        		return;
	        	}
	        	
	        	Alliance alliance = allianceResult.get();
	        	Guild otherGuild = otherGuildResult.get();
	        	if (!alliance.addGuild(otherGuild)) {
	        		log.error("Could not add guild '{}' into alliance '{}' for user '{}'", otherGuild.getGuildName(), allianceName, remoteUser.getCharacterName());
	        		return;
	        	}
	        	
	        	// Save to database
	            DatabaseManager.allianceAccessor().saveAlliance(alliance);
	            
	            List<Guild> guilds = new ArrayList<>(2);
	            guilds.add(guild);
	            guilds.add(otherGuild);
	            
	            // Update clients
                final OutPacket createPacket = AlliancePacket.createDone(alliance, guilds);
	            
	            forEachAllianceMember(alliance, (member, node) -> {
                    node.write(CentralPacket.userPacketReceive(member.getCharacterId(), createPacket));
                });
	        }
            
            case Load -> {
                // Load alliance from storage / database
                final int allianceId = allianceRequest.getAllianceId() != 0 ? allianceRequest.getAllianceId() : guild.getAllianceId();
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(allianceId);
                if (allianceResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.guildResult(characterId, GuildInfo.from(guild, characterId)));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, AlliancePacket.loadDone(null)));
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, AlliancePacket.loadDone(alliance)));
                    final List<Guild> guilds = new ArrayList<>();
                    for (int guildId : alliance.getGuilds()) {
                        centralServerNode.getGuildById(guildId).ifPresent(guilds::add);
                    }
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, AlliancePacket.loadGuildDone(guilds)));
                }
            }
            case Withdraw -> {
                // Resolve alliance
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance to withdraw from");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                // Remove guild from alliance
                try (var lockedGuild = guild.acquire()) {
                    final GuildMember leader = guild.getMember(characterId);
                    if (leader == null || leader.getGuildRank() != GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    try (var lockedAlliance = allianceResult.get().acquire()) {
                        final Alliance alliance = lockedAlliance.get();
                        if (!alliance.removeGuild(guild.getGuildId())) {
                            log.error("Could not remove guild from alliance");
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                            return;
                        }
                        guild.setAllianceId(0);
                        // Update clients
                        final OutPacket withdrawPacket = AlliancePacket.withdrawDone(alliance, guild, false);
                        forEachGuildMember(guild, (member, node) -> {
                            node.write(CentralPacket.guildResult(member.getCharacterId(), GuildInfo.from(guild, member.getCharacterId())));
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), withdrawPacket));
                        });
                        forEachAllianceMember(alliance, (member, node) -> {
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), withdrawPacket));
                        });
                        // Save to database
                        DatabaseManager.guildAccessor().saveGuild(guild);
                        DatabaseManager.allianceAccessor().saveAlliance(alliance);
                    }
                }
            }
            case Invite -> {
                // Resolve alliance
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for invite");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    try (var lockedGuild = guild.acquire()) {
                        final GuildMember master = guild.getMember(characterId);
                        if (master == null || master.getGuildRank() != GuildRank.MASTER || master.getAllianceRank() != GuildRank.MASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the alliance.")));
                            return;
                        }
                        // Resolve target guild
                        final Optional<Guild> targetGuildResult = centralServerNode.getGuildByName(allianceRequest.getGuildName());
                        if (targetGuildResult.isEmpty()) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(String.format("Could not invite guild \"%s\" to the alliance.", allianceRequest.getGuildName()))));
                            return;
                        }
                        final Guild targetGuild = targetGuildResult.get();
                        if (targetGuild.getAllianceId() != 0 || !alliance.canAddGuild(targetGuild.getGuildId())) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(String.format("Could not invite guild \"%s\" to the alliance.", allianceRequest.getGuildName()))));
                            return;
                        }
                        // Resolve target user
                        final Optional<RemoteUser> targetResult = targetGuild.getGuildMaster().flatMap((member) -> centralServerNode.getUserByCharacterId(member.getCharacterId()));
                        if (targetResult.isEmpty()) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("Unable to find the master of the guild.")));
                            return;
                        }
                        final RemoteUser target = targetResult.get();
                        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
                        if (targetNodeResult.isEmpty()) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("Unable to find the master of the guild.")));
                            return;
                        }
                        // Register invite and send alliance invite to target
                        alliance.registerInvite(characterId, target.getCharacterId());
                        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), AlliancePacket.invite(characterId, remoteUser.getCharacterName())));
                    }
                }
            }
            case Join -> {
                // Resolve inviter
                final int inviterId = allianceRequest.getInviterId();
                final Optional<RemoteUser> inviterResult = centralServerNode.getUserByCharacterId(inviterId);
                if (inviterResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                final RemoteUser inviter = inviterResult.get();
                // Resolve alliance
                final Optional<Guild> inviterGuildResult = centralServerNode.getGuildById(inviter.getGuildId());
                if (inviterGuildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                final Guild inviterGuild = inviterGuildResult.get();
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(inviterGuild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for join");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    // Check if user has been invited by the inviter
                    if (!alliance.unregisterInvite(inviterId, characterId)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    // Try adding guild to alliance
                    try (var lockedGuild = guild.acquire()) {
                        final GuildMember master = guild.getMember(characterId);
                        if (master == null || master.getGuildRank() != GuildRank.MASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the guild.")));
                            return;
                        }
                        if (guild.getAllianceId() != 0) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You have already joined an alliance.")));
                            return;
                        }
                        if (!alliance.addGuild(guild.getGuildId(), guild)) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                            return;
                        }
                        guild.setAllianceId(alliance.getAllianceId());
                        master.setAllianceRank(GuildRank.SUBMASTER);
                        // Update members
                        final OutPacket inviteDonePacket = AlliancePacket.inviteDone(alliance, guild);
                        forEachAllianceMember(alliance, (member, node) -> {
                            if (member.getGuildId() == guild.getGuildId()) {
                                node.write(CentralPacket.guildResult(member.getCharacterId(), GuildInfo.from(guild, member.getCharacterId())));
                            }
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), inviteDonePacket));
                        });
                        // Save to database
                        DatabaseManager.guildAccessor().saveGuild(guild);
                        DatabaseManager.allianceAccessor().saveAlliance(alliance);
                    }
                }
            }
            case Kick -> {
                // Resolve alliance
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for kick");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    try (var lockedGuild = guild.acquire()) {
                        final GuildMember master = guild.getMember(characterId);
                        if (master == null || master.getGuildRank() != GuildRank.MASTER || master.getAllianceRank() != GuildRank.MASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the alliance.")));
                            return;
                        }
                        // Resolve and target guild from alliance
                        final Optional<Guild> targetGuildResult = centralServerNode.getGuildById(allianceRequest.getGuildId());
                        if (targetGuildResult.isEmpty()) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                            return;
                        }
                        final Guild targetGuild = targetGuildResult.get();
                        if (targetGuild.getGuildId() == guild.getGuildId() || !alliance.removeGuild(targetGuild.getGuildId())) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(String.format("Cannot kick guild \"%s\" from the alliance.", targetGuild.getGuildName()))));
                            return;
                        }
                        targetGuild.setAllianceId(0);
                        // Update clients
                        final OutPacket withdrawPacket = AlliancePacket.withdrawDone(alliance, targetGuild, true);
                        forEachGuildMember(targetGuild, (member, node) -> {
                            node.write(CentralPacket.guildResult(member.getCharacterId(), GuildInfo.from(targetGuild, member.getCharacterId())));
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), withdrawPacket));
                        });
                        forEachAllianceMember(alliance, (member, node) -> {
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), withdrawPacket));
                        });
                        // Save to database
                        DatabaseManager.guildAccessor().saveGuild(targetGuild);
                        DatabaseManager.allianceAccessor().saveAlliance(alliance);
                    }
                }
            }
            
            case UpdateMemberCountMax -> {
            	// Resolve alliance
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for updateMemberCountMax");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    try (var lockedGuild = guild.acquire()) {
                        final GuildMember master = guild.getMember(characterId);
                        if (master == null || master.getGuildRank() != GuildRank.MASTER || master.getAllianceRank() != GuildRank.MASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the alliance.")));
                            return;
                        }
                        
                        if (alliance.getMemberMax() >= GameConstants.UNION_CAPACITY_MAX) {
                        	remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("The alliance already reached maximum capacity.")));
                        	return;
                        }
                        
                        int count = Math.min(alliance.getMemberMax() + 1, GameConstants.UNION_CAPACITY_MAX);
                        alliance.setMemberMax(count);
                        
                        // Save to database
                        DatabaseManager.allianceAccessor().saveAlliance(alliance);
                    }
                }
            }
            
            case SetGradeName -> {
            	// Resolve alliance
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for setGradeName");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    try (var lockedGuild = guild.acquire()) {
                        final GuildMember master = guild.getMember(characterId);
                        if (master == null || master.getGuildRank() != GuildRank.MASTER || master.getAllianceRank() != GuildRank.MASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the alliance.")));
                            return;
                        }
                        
                        List<String> gradeNames = allianceRequest.getGradeNames();
                        final OutPacket gradeNamesPacket = AlliancePacket.setGradeNameDone(alliance.getAllianceId(), gradeNames);
                        
                        forEachAllianceMember(alliance, (member, node) -> {
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), gradeNamesPacket));
                        });
                        // Save to database
                        DatabaseManager.allianceAccessor().saveAlliance(alliance);
                    }
                }
            }
            
            case ChangeGrade -> {
            	int targetId = allianceRequest.getTargetId();
            	boolean gradeUp = allianceRequest.isGradeUp();
            	
            	// Resolve alliance
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for ChangeGrade");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                
            	GuildMember targetMember = null;
                try (var lockedGuild = guild.acquire()) {
                	targetMember = guild.getMember(targetId);
                    
                    int newRank = targetMember.getAllianceRank().getValue() + (gradeUp ? -1 : 1);
                    newRank = Math.clamp(newRank, GuildRank.MASTER.getValue(), GuildRank.MEMBER3.getValue());
                    
                    targetMember.setAllianceRank(GuildRank.getByValue(newRank));
                }
                
                if (targetMember != null) {
                	try (var lockedAlliance = allianceResult.get().acquire()) {
                        final Alliance alliance = lockedAlliance.get();
                        final OutPacket changeGradePacket = AlliancePacket.changeGradeDone(targetMember);
                        forEachAllianceMember(alliance, (member, node) -> {
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), changeGradePacket));
                        });
                    }
                }
            }
            
            case SetNotice -> {
            	String notice = allianceRequest.getNotice();
            	
            	// Resolve alliance
                final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for SetNotice");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    try (var lockedGuild = guild.acquire()) {
                        final GuildMember master = guild.getMember(characterId);
                        if (master == null || master.getGuildRank() != GuildRank.MASTER || master.getAllianceRank() != GuildRank.MASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the alliance.")));
                            return;
                        }
                        
                        alliance.setNotice(notice);
                        
                        // Save to database
                        DatabaseManager.allianceAccessor().saveAlliance(alliance);
                        
                        final OutPacket noticePacket = AlliancePacket.setNoticeDone(alliance.getAllianceId(), notice);
                        forEachAllianceMember(alliance, (member, node) -> {
                            node.write(CentralPacket.userPacketReceive(member.getCharacterId(), noticePacket));
                        });
                    }
                }
            }
            
            case Destroy -> {
            	final Optional<Alliance> allianceResult = centralServerNode.getAllianceById(guild.getAllianceId());
                if (allianceResult.isEmpty()) {
                    log.error("Could not resolve alliance for destroy");
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedAlliance = allianceResult.get().acquire()) {
                    final Alliance alliance = lockedAlliance.get();
                    
                    // Update clients
                    final OutPacket destroyPacket = AlliancePacket.destroyDone(alliance.getAllianceId());
                    forEachAllianceMember(alliance, (member, node) -> {
                        node.write(CentralPacket.userPacketReceive(member.getCharacterId(), destroyPacket));
                    });
                    for (int guildId : alliance.getGuilds()) {
                    	final Optional<Guild> targetGuildResult = centralServerNode.getGuildById(guildId);
                        if (targetGuildResult.isEmpty()) {
                            continue;
                        }
                        
                        final Guild targetGuild = targetGuildResult.get();
                        targetGuild.setAllianceId(0);
                    	
                    	// Save to database
                    	DatabaseManager.guildAccessor().saveGuild(targetGuild);
                    }
                    
                    DatabaseManager.allianceAccessor().deleteAlliance(alliance.getAllianceId());
                }
            }
        }
    }

    private void handleBoardRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final GuildBoardRequest boardRequest = GuildBoardRequest.decode(inPacket);
        // Resolve requester user
        final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
        if (remoteUserResult.isEmpty()) {
            log.error("Failed to resolve user with character ID : {} for GuildBoardRequest", characterId);
            return;
        }
        final RemoteUser remoteUser = remoteUserResult.get();
        // Resolve guild
        final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
        if (guildResult.isEmpty()) {
            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not in a guild yet.")));
            return;
        }
        try (var lockedGuild = guildResult.get().acquire()) {
            final Guild guild = lockedGuild.get();
            final GuildRank guildRank = guild.getMember(characterId).getGuildRank();
            switch (boardRequest.getRequestType()) {
                case Register -> {
                    if (boardRequest.isModify()) {
                        // Resolve entry
                        final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                        if (entryResult.isEmpty()) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.boardEntryNotFound()));
                            return;
                        }
                        final GuildBoardEntry entry = entryResult.get();
                        if (entry.getCharacterId() != characterId && guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You cannot edit this entry.")));
                            return;
                        }
                        // Modify entry
                        entry.setTitle(boardRequest.getTitle());
                        entry.setText(boardRequest.getText());
                        entry.setDate(Instant.now());
                        entry.setEmoticon(boardRequest.getEmoticon());
                    } else {
                        if (boardRequest.isNotice()) {
                            // Check if notice can be created
                            if (guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                                remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You are not the master of the guild yet.")));
                                return;
                            }
                            if (guild.getBoardNoticeEntry() != null) {
                                remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("Please delete the current notice and then try again.")));
                                return;
                            }
                        }
                        // Create board entry
                        final GuildBoardEntry entry = new GuildBoardEntry(
                                guild.getNextBoardEntryId(),
                                characterId,
                                boardRequest.getTitle(),
                                boardRequest.getText(),
                                Instant.now(),
                                boardRequest.getEmoticon()
                        );
                        if (boardRequest.isNotice()) {
                            guild.setBoardNoticeEntry(entry);
                        } else {
                            guild.addBoardEntry(entry);
                        }
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.viewEntryResult(entry)));
                    }
                }
                case Delete -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    if (entryResult.get().getCharacterId() != characterId && guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You cannot delete this entry.")));
                        return;
                    }
                    guild.removeBoardEntry(boardRequest.getEntryId());
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.loadEntryListResult(guild.getBoardNoticeEntry(), guild.getBoardEntryList(0), guild.getBoardEntries().size())));
                }
                case LoadListRequest -> {
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.loadEntryListResult(guild.getBoardNoticeEntry(), guild.getBoardEntryList(boardRequest.getStart()), guild.getBoardEntries().size())));
                }
                case ViewEntryRequest -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.viewEntryResult(entryResult.get())));
                }
                case RegisterComment -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    final GuildBoardEntry entry = entryResult.get();
                    entry.addComment(new GuildBoardComment(
                            entry.getNextCommentSn(),
                            characterId,
                            boardRequest.getText(),
                            Instant.now()
                    ));
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.viewEntryResult(entry)));
                }
                case DeleteComment -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    final GuildBoardEntry entry = entryResult.get();
                    final Optional<GuildBoardComment> commentResult = entry.getComment(boardRequest.getCommentSn());
                    if (commentResult.isEmpty() || (commentResult.get().getCharacterId() != characterId && guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.serverMsg("You cannot delete this comment.")));
                        return;
                    }
                    entry.removeComment(boardRequest.getCommentSn());
                    remoteServerNode.write(CentralPacket.userPacketReceive(characterId, GuildPacket.viewEntryResult(entry)));
                }
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

    private void updatePartyMember(RemoteUser remoteUser, boolean isUserUpdate) {
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
                if (!isUserUpdate && remoteUser.getCharacterId() == member.getCharacterId()) {
                    return;
                }
                node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
            });
        }
    }

    private void updateGuildMember(RemoteUser remoteUser, boolean isUserUpdate) {
        final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
        if (guildResult.isEmpty()) {
            return;
        }
        try (var lockedGuild = guildResult.get().acquire()) {
            // Update member
            final Guild guild = lockedGuild.get();
            final boolean isOnline = remoteUser.getChannelId() != GameConstants.CHANNEL_OFFLINE;
            final boolean wasOnline = guild.getMember(remoteUser.getCharacterId()).isOnline();
            guild.updateMember(remoteUser);
            // No update needed
            if (!isUserUpdate && isOnline == wasOnline) {
                return;
            }
            // Update user for all members
            final List<Integer> guildMemberIds = isUserUpdate ?
                    guild.getMemberIds() :
                    guild.getMemberIds(remoteUser.getCharacterId()); // do not need to update self online status
            final OutPacket outPacket = isUserUpdate ?
                    GuildPacket.changeLevelOrJob(guild.getGuildId(), remoteUser.getCharacterId(), remoteUser.getLevel(), remoteUser.getJob()) :
                    GuildPacket.notifyLoginOrLogout(guild.getGuildId(), remoteUser.getCharacterId(), isOnline);
            for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
                serverNode.write(CentralPacket.userPacketBroadcast(guildMemberIds, outPacket));
            }
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

    private void forEachGuildMember(Guild guild, BiConsumer<RemoteUser, RemoteServerNode> biConsumer) {
        for (int memberId : guild.getMemberIds()) {
            final Optional<RemoteUser> remoteMemberResult = centralServerNode.getUserByCharacterId(memberId);
            if (remoteMemberResult.isEmpty()) {
                continue;
            }
            final RemoteUser member = remoteMemberResult.get();
            final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                continue;
            }
            biConsumer.accept(member, targetNodeResult.get());
        }
    }

    private void forEachAllianceMember(Alliance alliance, BiConsumer<RemoteUser, RemoteServerNode> biConsumer) {
        for (int guildId : alliance.getGuilds()) {
            final Optional<Guild> guildResult = centralServerNode.getGuildById(guildId);
            if (guildResult.isEmpty()) {
                continue;
            }
            final Guild guild = guildResult.get();
            for (int memberId : guild.getMemberIds()) {
                final Optional<RemoteUser> remoteMemberResult = centralServerNode.getUserByCharacterId(memberId);
                if (remoteMemberResult.isEmpty()) {
                    continue;
                }
                final RemoteUser member = remoteMemberResult.get();
                final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(member.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    continue;
                }
                biConsumer.accept(member, targetNodeResult.get());
            }
        }
    }
}
