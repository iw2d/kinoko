package kinoko.server.handler;

import kinoko.database.DatabaseManager;
import kinoko.packet.CentralPacket;
import kinoko.packet.world.PartyPacket;
import kinoko.server.node.CentralServerNode;
import kinoko.server.node.RemoteServerNode;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.server.party.Party;
import kinoko.server.party.PartyRequest;
import kinoko.server.party.PartyResultType;
import kinoko.server.user.RemoteUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.BiConsumer;

public final class CentralPartyHandler {
    private static final Logger log = LogManager.getLogger(CentralPartyHandler.class);
    private final CentralServerNode centralServerNode;

    public CentralPartyHandler(CentralServerNode centralServerNode) {
        this.centralServerNode = centralServerNode;
    }

    public void handlePartyRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
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


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void updatePartyMember(RemoteUser remoteUser, boolean isUserUpdate) {
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

    private void forEachPartyMember(Party party, BiConsumer<RemoteUser, RemoteServerNode> biConsumer) {
        party.forEachMember((member) -> {
            final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                return;
            }
            biConsumer.accept(member, targetNodeResult.get());
        });
    }

}
