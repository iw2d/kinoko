package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.packet.CentralPacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.node.*;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.social.party.Party;
import kinoko.world.social.party.PartyRequest;
import kinoko.world.social.party.PartyResult;
import kinoko.world.social.party.PartyResultType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        final CentralPacketHeader header = CentralPacketHeader.getByValue(op);
        log.log(Level.DEBUG, "[CentralServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        switch (header) {
            case INITIALIZE_RESULT -> {
                final int channelId = inPacket.decodeInt();
                final byte[] channelHost = inPacket.decodeArray(4);
                final int channelPort = inPacket.decodeInt();
                // Initialize child node
                remoteChildNode.setChannelId(channelId);
                remoteChildNode.setChannelHost(channelHost);
                remoteChildNode.setChannelPort(channelPort);
                centralServerNode.addChildNode(remoteChildNode);
            }
            case SHUTDOWN_RESULT -> {
                final int channelId = inPacket.decodeInt();
                final boolean success = inPacket.decodeBoolean();
                if (!success) {
                    log.error("Failed to shutdown channel {}, trying again", channelId + 1);
                    remoteChildNode.write(CentralPacket.shutdownRequest());
                    return;
                }
                centralServerNode.removeChildNode(channelId);
            }
            case MIGRATION_REQUEST -> {
                // Channel migration - complete stored migration request
                final int requestId = inPacket.decodeInt();
                final MigrationInfo migrationInfo = MigrationInfo.decode(inPacket);
                final Optional<MigrationInfo> migrationResult = centralServerNode.completeMigrationRequest(migrationInfo);
                remoteChildNode.write(CentralPacket.migrationResult(requestId, migrationResult.orElse(null)));
            }
            case TRANSFER_REQUEST -> {
                // Channel transfer - create migration request and reply with transfer info
                final int requestId = inPacket.decodeInt();
                final MigrationInfo migrationInfo = MigrationInfo.decode(inPacket);
                if (!centralServerNode.submitMigrationRequest(migrationInfo)) {
                    // Transfer request failed
                    log.error("Failed to submit migration request for character ID : {}", migrationInfo.getCharacterId());
                    remoteChildNode.write(CentralPacket.transferResult(requestId, null));
                    return;
                }
                // Resolve target channel
                final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(migrationInfo.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    // Transfer request failed
                    log.error("Failed to resolve channel ID {}", migrationInfo.getChannelId() + 1);
                    remoteChildNode.write(CentralPacket.transferResult(requestId, null));
                    return;
                }
                final RemoteChildNode targetNode = targetNodeResult.get();
                // Reply with transfer info
                remoteChildNode.write(CentralPacket.transferResult(requestId, new TransferInfo(
                        targetNode.getChannelHost(),
                        targetNode.getChannelPort()
                )));
            }
            case USER_CONNECT -> {
                final RemoteUser remoteUser = RemoteUser.decode(inPacket);
                centralServerNode.addUser(remoteUser);
                // Update party
                final Optional<Party> partyResult = centralServerNode.getPartyByCharacterId(remoteUser.getCharacterId());
                if (partyResult.isPresent()) {
                    try (var lockedParty = partyResult.get().acquire()) {
                        // Set party ID
                        final Party party = lockedParty.get();
                        remoteUser.setPartyId(party.getPartyId());
                        remoteChildNode.write(CentralPacket.partyResult(remoteUser.getCharacterId(), party.getPartyId()));
                        // Update user and load party for all members
                        party.updateMember(remoteUser);
                        final OutPacket outPacket = WvsContext.partyResult(PartyResult.load(party));
                        party.forEachMember((member) -> {
                            final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(member.getChannelId());
                            if (targetNodeResult.isEmpty()) {
                                return;
                            }
                            targetNodeResult.get().write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                        });
                    }
                }
            }
            case USER_UPDATE -> {
                final RemoteUser remoteUser = RemoteUser.decode(inPacket);
                centralServerNode.updateUser(remoteUser);
                // Update party
                final Optional<Party> partyResult = centralServerNode.getPartyByCharacterId(remoteUser.getCharacterId());
                if (partyResult.isPresent()) {
                    try (var lockedParty = partyResult.get().acquire()) {
                        // Set party ID
                        final Party party = lockedParty.get();
                        remoteUser.setPartyId(party.getPartyId());
                        // Update user for all members
                        party.updateMember(remoteUser);
                        final OutPacket outPacket = WvsContext.partyResult(PartyResult.load(party));
                        party.forEachMember((member) -> {
                            final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(member.getChannelId());
                            if (targetNodeResult.isEmpty()) {
                                return;
                            }
                            targetNodeResult.get().write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                        });
                    }
                }
            }
            case USER_DISCONNECT -> {
                final RemoteUser remoteUser = RemoteUser.decode(inPacket);
                centralServerNode.removeUser(remoteUser);
                // TODO update party
            }
            case USER_PACKET_REQUEST -> {
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
                    // Transfer request failed
                    log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
                    return;
                }
                // Send USER_PACKET_RECEIVE to target channel node
                targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
            }
            case USER_PACKET_RECEIVE -> {
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
                    // Transfer request failed
                    log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
                    return;
                }
                // Send USER_PACKET_RECEIVE to target channel node
                targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
            }
            case USER_PACKET_BROADCAST -> {
                final int size = inPacket.decodeInt();
                final Set<Integer> characterIds = new HashSet<>();
                for (int i = 0; i < size; i++) {
                    characterIds.add(inPacket.decodeInt());
                }
                final int packetLength = inPacket.decodeInt();
                final byte[] packetData = inPacket.decodeArray(packetLength);
                for (RemoteChildNode childNode : centralServerNode.getConnectedNodes()) {
                    childNode.write(CentralPacket.userPacketBroadcast(characterIds, OutPacket.of(packetData)));
                }
            }
            case USER_QUERY_REQUEST -> {
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
            case PARTY_REQUEST -> {
                final int characterId = inPacket.decodeInt();
                final PartyRequest partyRequest = PartyRequest.decode(inPacket);
                // Resolve requester user
                final Optional<RemoteUser> remoteUserResult = centralServerNode.getUserByCharacterId(characterId);
                if (remoteUserResult.isEmpty()) {
                    log.error("Failed to resolve user with character ID : {}", characterId);
                    return;
                }
                final RemoteUser remoteUser = remoteUserResult.get();
                // Process request
                switch (partyRequest.getRequestType()) {
                    case LOAD_PARTY -> {
                        // Remote user party ID is set on USER_CONNECT
                        final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                        if (partyResult.isEmpty()) {
                            remoteChildNode.write(CentralPacket.partyResult(characterId, 0));
                            return;
                        }
                        try (var lockedParty = partyResult.get().acquire()) {
                            final Party party = lockedParty.get();
                            remoteChildNode.write(CentralPacket.partyResult(characterId, party.getPartyId()));
                            remoteChildNode.write(CentralPacket.userPacketReceive(characterId, WvsContext.partyResult(PartyResult.load(party))));
                        }
                    }
                    case CREATE_NEW_PARTY -> {
                        final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                        if (partyResult.isPresent()) {
                            log.error("Failed to create party for user already in party {}", remoteUser.getPartyId());
                            remoteChildNode.write(CentralPacket.userPacketReceive(characterId, WvsContext.partyResult(PartyResult.of(PartyResultType.CREATE_NEW_PARTY_ALREADY_JOINED))));
                            return;
                        }
                        final Party party = centralServerNode.createNewParty(remoteUser);
                        remoteUser.setPartyId(party.getPartyId());
                        remoteChildNode.write(CentralPacket.partyResult(characterId, party.getPartyId()));
                        remoteChildNode.write(CentralPacket.userPacketReceive(characterId, WvsContext.partyResult(PartyResult.create(party))));
                    }
                    case WITHDRAW_PARTY -> {
                        final Optional<Party> partyResult = centralServerNode.getPartyById(remoteUser.getPartyId());
                        if (partyResult.isEmpty()) {
                            log.error("Could not resolve party {}", remoteUser.getPartyId());
                            remoteChildNode.write(CentralPacket.userPacketReceive(characterId, WvsContext.partyResult(PartyResult.of(PartyResultType.WITHDRAW_PARTY_NOT_JOINED))));
                            return;
                        }
                        try (var lockedParty = partyResult.get().acquire()) {
                            final Party party = lockedParty.get();
                            if (party.getPartyBossId() == remoteUser.getCharacterId()) {
                                // Disband party
                                if (!centralServerNode.removeParty(party)) {
                                    log.error("Failed to disband party {}", party.getPartyId());
                                    remoteChildNode.write(CentralPacket.userPacketReceive(characterId, WvsContext.partyResult(PartyResult.of(PartyResultType.WITHDRAW_PARTY_UNKNOWN))));
                                    return;
                                }
                                // Broadcast disband packet to party
                                final OutPacket outPacket = WvsContext.partyResult(PartyResult.disband(party, remoteUser));
                                party.forEachMember((member) -> {
                                    final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(member.getChannelId());
                                    if (targetNodeResult.isEmpty()) {
                                        return;
                                    }
                                    member.setPartyId(0);
                                    targetNodeResult.get().write(CentralPacket.partyResult(member.getCharacterId(), 0));
                                    targetNodeResult.get().write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                                });
                            } else {
                                // Remove member
                                if (!party.removeMember(remoteUser)) {
                                    log.error("Failed to remove member with character ID {} from party {}", remoteUser.getCharacterId(), party.getPartyId());
                                    remoteChildNode.write(CentralPacket.userPacketReceive(characterId, WvsContext.partyResult(PartyResult.of(PartyResultType.WITHDRAW_PARTY_UNKNOWN))));
                                    return;
                                }
                                // Broadcast withdraw packet to party
                                final OutPacket outPacket = WvsContext.partyResult(PartyResult.leave(party, remoteUser));
                                party.forEachMember((member) -> {
                                    final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(member.getChannelId());
                                    if (targetNodeResult.isEmpty()) {
                                        return;
                                    }
                                    targetNodeResult.get().write(CentralPacket.userPacketReceive(member.getCharacterId(), outPacket));
                                });
                                // Update user
                                remoteUser.setPartyId(0);
                                remoteChildNode.write(CentralPacket.partyResult(characterId, 0));
                                remoteChildNode.write(CentralPacket.userPacketReceive(characterId, outPacket));
                            }
                        }
                    }
                    case JOIN_PARTY -> {
                    }
                    case INVITE_PARTY -> {
                    }
                    case KICK_PARTY -> {
                    }
                    case CHANGE_PARTY_BOSS -> {
                    }
                }
            }
            case null -> {
                log.error("Central Server received an unknown opcode : {}", op);
            }
            default -> {
                log.error("Central Server received an unhandled header : {}", header);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final RemoteChildNode remoteChildNode = ctx.channel().attr(RemoteChildNode.NODE_KEY).get();
        if (remoteChildNode != null) {
            log.error("Lost connection to channel {}", remoteChildNode.getChannelId());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }
}
