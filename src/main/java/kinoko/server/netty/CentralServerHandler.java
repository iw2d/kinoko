package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.packet.CentralPacket;
import kinoko.packet.field.MessengerPacket;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.PartyPacket;
import kinoko.server.header.CentralHeader;
import kinoko.server.messenger.Messenger;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.messenger.MessengerUser;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.CentralServerNode;
import kinoko.server.node.RemoteChildNode;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public final class CentralServerHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(CentralServerHandler.class);
    private final CentralServerNode centralServerNode;

    public CentralServerHandler(CentralServerNode centralServerNode) {
        this.centralServerNode = centralServerNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final RemoteChildNode remoteChildNode = ctx.channel().attr(RemoteChildNode.NODE_KEY).get();
        if (remoteChildNode == null) {
            log.error("Received packet from unknown node {}", ctx.channel().remoteAddress());
            return;
        }
        final int op = inPacket.decodeShort();
        final CentralHeader header = CentralHeader.getByValue(op);
        log.log(Level.TRACE, "[CentralServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        ServerExecutor.submitService(() -> {
            switch (header) {
                case InitializeResult -> handleInitializeResult(remoteChildNode, inPacket);
                case ShutdownResult -> handleShutdownResult(remoteChildNode, inPacket);
                case MigrateRequest -> handleMigrateRequest(remoteChildNode, inPacket);
                case TransferRequest -> handleTransferRequest(remoteChildNode, inPacket);
                case UserConnect -> handleUserConnect(remoteChildNode, inPacket);
                case UserUpdate -> handleUserUpdate(remoteChildNode, inPacket);
                case UserDisconnect -> handleUserDisconnect(remoteChildNode, inPacket);
                case UserPacketRequest -> handleUserPacketRequest(remoteChildNode, inPacket);
                case UserPacketReceive -> handleUserPacketReceive(remoteChildNode, inPacket);
                case UserPacketBroadcast -> handleUserPacketBroadcast(remoteChildNode, inPacket);
                case UserQueryRequest -> handleUserQueryRequest(remoteChildNode, inPacket);
                case ServerPacketBroadcast -> handleServerPacketBroadcast(remoteChildNode, inPacket);
                case PartyRequest -> handlePartyRequest(remoteChildNode, inPacket);
                case MessengerRequest -> handleMessengerRequest(remoteChildNode, inPacket);
                case null -> log.error("Central Server received an unknown opcode : {}", op);
                default -> log.error("Central Server received an unhandled header : {}", header);
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final RemoteChildNode remoteChildNode = ctx.channel().attr(RemoteChildNode.NODE_KEY).get();
        if (remoteChildNode != null && !centralServerNode.isShutdown()) {
            log.error("Lost connection to channel {}", remoteChildNode.getChannelId());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }


    // HANDLER METHODS -------------------------------------------------------------------------------------------------

    private void handleInitializeResult(RemoteChildNode remoteChildNode, InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final byte[] channelHost = inPacket.decodeArray(4);
        final int channelPort = inPacket.decodeInt();
        // Initialize child node
        remoteChildNode.setChannelId(channelId);
        remoteChildNode.setChannelHost(channelHost);
        remoteChildNode.setChannelPort(channelPort);
        centralServerNode.addChildNode(remoteChildNode);
    }

    private void handleShutdownResult(RemoteChildNode remoteChildNode, InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final boolean success = inPacket.decodeBoolean();
        if (!success) {
            log.error("Failed to shutdown channel {}, trying again", channelId + 1);
            remoteChildNode.write(CentralPacket.shutdownRequest());
            return;
        }
        centralServerNode.removeChildNode(channelId);
    }

    private void handleMigrateRequest(RemoteChildNode remoteChildNode, InPacket inPacket) {
        // Channel migration - complete stored migration request
        final int requestId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final byte[] clientKey = inPacket.decodeArray(8);
        final Optional<MigrationInfo> migrationResult = centralServerNode.completeMigrationRequest(remoteChildNode.getChannelId(), accountId, characterId, machineId, clientKey);
        remoteChildNode.write(CentralPacket.migrateResult(requestId, migrationResult.orElse(null)));
    }

    private void handleTransferRequest(RemoteChildNode remoteChildNode, InPacket inPacket) {
        // Channel transfer - create migration request and reply with transfer info
        final int requestId = inPacket.decodeInt();
        final MigrationInfo migrationInfo = MigrationInfo.decode(inPacket);
        // Resolve target channel
        final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(migrationInfo.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", migrationInfo.getChannelId() + 1);
            remoteChildNode.write(CentralPacket.transferResult(requestId, null));
            return;
        }
        final RemoteChildNode targetNode = targetNodeResult.get();
        // Submit migration request
        if (!centralServerNode.submitMigrationRequest(migrationInfo)) {
            log.error("Failed to submit migration request for character ID : {}", migrationInfo.getCharacterId());
            remoteChildNode.write(CentralPacket.transferResult(requestId, null));
            return;
        }
        // Reply with transfer info
        remoteChildNode.write(CentralPacket.transferResult(requestId, new TransferInfo(
                targetNode.getChannelHost(),
                targetNode.getChannelPort()
        )));
    }

    private void updatePartyMember(RemoteUser remoteUser) {
        final Optional<Party> partyResult = centralServerNode.getPartyByCharacterId(remoteUser.getCharacterId());
        if (partyResult.isEmpty()) {
            return;
        }
        try (var lockedParty = partyResult.get().acquire()) {
            // Set party ID
            final Party party = lockedParty.get();
            remoteUser.setPartyId(party.getPartyId());
            // Update user for all members
            party.updateMember(remoteUser);
            final OutPacket outPacket = PartyPacket.loadPartyDone(party);
            forEachPartyMember(party, (member, node) -> {
                node.write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
            });
        }
    }

    private void handleUserConnect(RemoteChildNode remoteChildNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.addUser(remoteUser);
        updatePartyMember(remoteUser);
        updateMessengerUser(remoteUser);
    }

    private void handleUserUpdate(RemoteChildNode remoteChildNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.updateUser(remoteUser);
        updatePartyMember(remoteUser);
        updateMessengerUser(remoteUser);
    }

    private void handleUserDisconnect(RemoteChildNode remoteChildNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.removeUser(remoteUser);
        // Check if transfer
        if (centralServerNode.isMigrating(remoteUser.getAccountId())) {
            return;
        }
        // Update party
        remoteUser.setChannelId(GameConstants.CHANNEL_OFFLINE);
        remoteUser.setFieldId(GameConstants.UNDEFINED_FIELD_ID);
        updatePartyMember(remoteUser);
        // Leave messenger
        leaveMessenger(remoteUser);
    }

    private void handleUserPacketRequest(RemoteChildNode remoteChildNode, InPacket inPacket) {
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
        final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(target.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
            return;
        }
        // Send UserPacketReceive to target channel node
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
    }

    private void handleUserPacketReceive(RemoteChildNode remoteChildNode, InPacket inPacket) {
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
        final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(target.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
            return;
        }
        // Send UserPacketReceive to target channel node
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
    }

    private void handleUserPacketBroadcast(RemoteChildNode remoteChildNode, InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final Set<Integer> characterIds = new HashSet<>();
        for (int i = 0; i < size; i++) {
            characterIds.add(inPacket.decodeInt());
        }
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        for (RemoteChildNode childNode : centralServerNode.getConnectedNodes()) {
            childNode.write(CentralPacket.userPacketBroadcast(characterIds, outPacket));
        }
    }

    private void handleUserQueryRequest(RemoteChildNode remoteChildNode, InPacket inPacket) {
        // Resolve queried users
        final int requestId = inPacket.decodeInt();
        final int size = inPacket.decodeInt();
        final Set<RemoteUser> remoteUsers = new HashSet<>();
        for (int i = 0; i < size; i++) {
            final String characterName = inPacket.decodeString();
            centralServerNode.getUserByCharacterName(characterName)
                    .ifPresent(remoteUsers::add);
        }
        // Reply with queried remote users
        remoteChildNode.write(CentralPacket.userQueryResult(requestId, remoteUsers));
    }

    private void handleServerPacketBroadcast(RemoteChildNode remoteChildNode, InPacket inPacket) {
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        for (RemoteChildNode childNode : centralServerNode.getConnectedNodes()) {
            childNode.write(CentralPacket.serverPacketBroadcast(outPacket));
        }
    }

    private void handlePartyRequest(RemoteChildNode remoteChildNode, InPacket inPacket) {
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
                    remoteChildNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), null));
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    final Party party = lockedParty.get();
                    remoteChildNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser)));
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.loadPartyDone(party)));
                }
            }
            case CreateNewParty -> {
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isPresent()) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.CreateNewParty_AlreadyJoined))); // Already have joined a party.
                    return;
                }
                // Create party
                final Party party = centralServerNode.createNewParty(remoteUser);
                remoteUser.setPartyId(party.getPartyId());
                remoteChildNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser)));
                remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.createNewPartyDone(party, remoteUser.getTownPortal()))); // You have created a new party.
            }
            case WithdrawParty -> {
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isEmpty()) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.WithdrawParty_NotJoined))); // You have yet to join a party.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    final Party party = lockedParty.get();
                    if (party.getPartyBossId() == remoteUser.getCharacterId()) {
                        // Disband party
                        if (!centralServerNode.removeParty(party)) {
                            log.error("Failed to disband party {}", party.getPartyId());
                            remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.WithdrawParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
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
                            remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.WithdrawParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
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
                        remoteChildNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), null));
                        remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), outPacket));
                    }
                }
            }
            case JoinParty -> {
                // Check current party
                if (remoteUser.getPartyId() != 0) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_Unknown))); // Already have joined a party.
                    return;
                }
                // Resolve party
                final int inviterId = partyRequest.getCharacterId();
                final Optional<Party> partyResult = centralServerNode.getPartyByCharacterId(inviterId);
                if (partyResult.isEmpty()) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    final Party party = lockedParty.get();
                    if (!party.addMember(remoteUser)) {
                        remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.JoinParty_AlreadyFull))); // The party you're trying to join is already in full capacity.
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
                    // Create party
                    final Party party = centralServerNode.createNewParty(remoteUser);
                    remoteUser.setPartyId(party.getPartyId());
                    remoteChildNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.createInfo(remoteUser)));
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.createNewPartyDone(party, remoteUser.getTownPortal()))); // You have created a new party.
                }
                // Resolve target
                final String targetName = partyRequest.getCharacterName();
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterName(targetName); // target name
                if (targetResult.isEmpty()) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                final RemoteUser target = targetResult.get();
                if (target.getPartyId() != 0) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg(String.format("'%s' is already in a party.", targetName))));
                    return;
                }
                // Resolve target node
                final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(target.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                // Send party invite to target
                targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), PartyPacket.inviteParty(remoteUser)));
            }
            case KickParty -> {
                final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                if (partyResult.isEmpty()) {
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.KickParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    // Check that the kick request is valid
                    final Party party = lockedParty.get();
                    final int targetId = partyRequest.getCharacterId();
                    final Optional<RemoteUser> targetMember = party.getMember(targetId);
                    if (party.getPartyBossId() != remoteUser.getCharacterId() || targetMember.isEmpty() || !party.removeMember(targetMember.get())) {
                        remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), PartyPacket.of(PartyResultType.KickParty_Unknown))); // Your request for a party didn't work due to an unexpected error.
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
                    remoteChildNode.write(CentralPacket.userPacketReceive(characterId, PartyPacket.of(PartyResultType.ChangePartyBoss_Unknown))); // Your request for a party didn't work due to an unexpected error.
                    return;
                }
                try (var lockedParty = partyResult.get().acquire()) {
                    // Try setting new party boss
                    final Party party = lockedParty.get();
                    final int targetId = partyRequest.getCharacterId();
                    if (!party.setPartyBossId(remoteUser.getCharacterId(), targetId)) {
                        remoteChildNode.write(CentralPacket.userPacketReceive(characterId, PartyPacket.of(PartyResultType.ChangePartyBoss_Unknown))); // Your request for a party didn't work due to an unexpected error.
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

    private void handleMessengerRequest(RemoteChildNode remoteChildNode, InPacket inPacket) {
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
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), BroadcastPacket.alert("This request has failed due to an unknown error.")));
                    return;
                }
                // Create messenger
                if (messengerId == 0) {
                    createMessenger(remoteChildNode, remoteUser, messengerUser);
                    return;
                }
                // Resolve messenger
                final Optional<Messenger> targetMessengerResult = centralServerNode.getMessengerById(messengerId);
                if (targetMessengerResult.isEmpty()) {
                    // Create messenger
                    createMessenger(remoteChildNode, remoteUser, messengerUser);
                    remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.selfEnterResult(-1))); // You have been unable to join the invited chat room.
                    return;
                }
                // Join messenger
                try (var lockedMessenger = targetMessengerResult.get().acquire()) {
                    final Messenger messenger = lockedMessenger.get();
                    if (!messenger.addUser(remoteUser, messengerUser)) {
                        // Create messenger
                        createMessenger(remoteChildNode, remoteUser, messengerUser);
                        remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.selfEnterResult(-1))); // You have been unable to join the invited chat room.
                        return;
                    }
                    remoteUser.setMessengerId(messenger.getMessengerId());
                    remoteChildNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), messenger.getMessengerId()));
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
                remoteChildNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), 0));
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
                    remoteChildNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), 0));
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
                            remoteChildNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), MessengerPacket.enter(entry.getKey(), entry.getValue(), false)));
                        }
                    }
                }
            }
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    private void forEachPartyMember(Party party, BiConsumer<RemoteUser, RemoteChildNode> biConsumer) {
        party.forEachMember((member) -> {
            final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                return;
            }
            biConsumer.accept(member, targetNodeResult.get());
        });
    }

    private void createMessenger(RemoteChildNode remoteChildNode, RemoteUser remoteUser, MessengerUser messengerUser) {
        final Messenger newMessenger = centralServerNode.createNewMessenger(remoteUser, messengerUser);
        remoteUser.setMessengerId(newMessenger.getMessengerId());
        remoteChildNode.write(CentralPacket.messengerResult(remoteUser.getCharacterId(), newMessenger.getMessengerId()));
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

    private void forEachMessengerUser(Messenger messenger, BiConsumer<RemoteUser, RemoteChildNode> biConsumer) {
        messenger.forEachUser((member) -> {
            final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                return;
            }
            biConsumer.accept(member, targetNodeResult.get());
        });
    }
}
