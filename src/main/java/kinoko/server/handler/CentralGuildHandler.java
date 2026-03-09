package kinoko.server.handler;

import kinoko.database.DatabaseManager;
import kinoko.packet.CentralPacket;
import kinoko.packet.world.GuildPacket;
import kinoko.server.guild.*;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;
import kinoko.server.node.CentralServerNode;
import kinoko.server.node.RemoteServerNode;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteUser;
import kinoko.world.GameConstants;
import kinoko.world.user.GuildInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class CentralGuildHandler {
    private static final Logger log = LogManager.getLogger(CentralGuildHandler.class);
    private final CentralServerNode centralServerNode;

    public CentralGuildHandler(CentralServerNode centralServerNode) {
        this.centralServerNode = centralServerNode;
    }

    public void handleGuildRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
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
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.loadGuildDone(null)));
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    if (!guild.hasMember(remoteUser.getCharacterId())) {
                        remoteUser.setGuildId(0);
                        remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), null));
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.loadGuildDone(null)));
                        return;
                    }
                    guild.updateMember(remoteUser);
                    remoteUser.setGuildId(guild.getGuildId());
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), GuildInfo.from(guild, remoteUser.getCharacterId())));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.loadGuildDone(guild)));
                }
            }
            case CreateNewGuild -> {
                // Create new guild in storage + database
                final Optional<Guild> guildResult = centralServerNode.createNewGuild(guildRequest.getGuildId(), guildRequest.getGuildName(), remoteUser);
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.createNewGuildUnknown())); // The problem has happened during the process of forming the guild... Plese try again later..
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    remoteUser.setGuildId(guild.getGuildId());
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), GuildInfo.from(guild, remoteUser.getCharacterId())));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.createNewGuildDone(guild)));
                }
            }
            case InviteGuild -> {
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(remoteUser.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg(null))); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                // Resolve target
                final String targetName = guildRequest.getTargetName();
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterName(targetName); // target name
                if (targetResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                final RemoteUser target = targetResult.get();
                if (target.getGuildId() != 0) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg(String.format("'%s' is already in a guild.", targetName))));
                    return;
                }
                // Resolve target node
                final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg(String.format("Unable to find '%s'", targetName))));
                    return;
                }
                final RemoteServerNode targetNode = targetNodeResult.get();
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    final GuildRank guildRank = guild.getMember(remoteUser.getCharacterId()).getGuildRank();
                    if (guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Check capacity
                    if (!guild.canAddMember(target.getCharacterId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You cannot invite more people to the guild.")));
                        return;
                    }
                    // Register invite and send guild invite to target
                    guild.registerInvite(remoteUser.getCharacterId(), target.getCharacterId());
                    targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), GuildPacket.inviteGuild(remoteUser)));
                }
            }
            case JoinGuild -> {
                // Resolve inviter
                final int inviterId = guildRequest.getInviterId();
                final Optional<RemoteUser> inviterResult = centralServerNode.getUserByCharacterId(inviterId);
                if (inviterResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.joinGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                final RemoteUser inviter = inviterResult.get();
                // Resolve guild
                final Optional<Guild> guildResult = centralServerNode.getGuildById(inviter.getGuildId());
                if (guildResult.isEmpty()) {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.joinGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check if user has been invited by the inviter
                    if (!guild.unregisterInvite(inviterId, remoteUser.getCharacterId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.joinGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    // Try adding user as a member to the guild
                    final GuildMember newMember = GuildMember.from(remoteUser);
                    newMember.setGuildRank(GuildRank.MEMBER3);
                    if (!guild.addMember(newMember)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.joinGuildAlreadyFull())); // The guild you are trying to join has already reached the max number of users.
                        return;
                    }
                    // Update user
                    remoteUser.setGuildId(guild.getGuildId());
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), GuildInfo.from(guild, remoteUser.getCharacterId())));
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
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.withdrawGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check if user can leave the guild
                    if (!guild.hasMember(remoteUser.getCharacterId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.withdrawGuildNotJoined())); // You are not in the guild.
                        return;
                    }
                    final GuildMember removedMember = guild.getMember(remoteUser.getCharacterId());
                    if (removedMember.getGuildRank() == GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You cannot quit the guild since you are the master of the guild.")));
                        return;
                    }
                    // Remove user from guild
                    guild.removeMember(removedMember);
                    // Update members
                    final OutPacket outPacket = GuildPacket.withdrawGuildDone(guild.getGuildId(), removedMember);
                    remoteUser.setGuildId(0);
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), outPacket));
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
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.kickGuildUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    final GuildRank guildRank = guild.getMember(remoteUser.getCharacterId()).getGuildRank();
                    if (guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Check if target can be kicked
                    if (!guild.hasMember(guildRequest.getTargetId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg(String.format("Unable to find '%s'", guildRequest.getTargetName()))));
                        return;
                    }
                    final GuildMember targetMember = guild.getMember(guildRequest.getTargetId());
                    if (targetMember.getGuildRank() == GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You may not ban the Guild Master.")));
                        return;
                    }
                    if (targetMember.getGuildRank() == GuildRank.SUBMASTER && guildRank != GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("Only Guild Master can expel Jr.Masters.")));
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
                    remoteServerNode.write(CentralPacket.guildResult(remoteUser.getCharacterId(), null));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.removeGuildUnknown())); // The problem has happened during the process of disbanding the guild... Plese try again later...
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    if (guild.getMember(remoteUser.getCharacterId()).getGuildRank() != GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Remove guild from storage + database
                    if (!centralServerNode.removeGuild(guild)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.removeGuildUnknown())); // The problem has happened during the process of disbanding the guild... Plese try again later...
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
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.incMaxMemberNumUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Check requester rank
                    if (guild.getMember(remoteUser.getCharacterId()).getGuildRank() != GuildRank.MASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You are not the master of the guild.")));
                        return;
                    }
                    // Check that max member can be increased
                    if (guild.getMemberMax() >= GameConstants.GUILD_CAPACITY_MAX || guild.getMemberMax() >= guildRequest.getMemberMax()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.incMaxMemberNumUnknown())); // The guild request has not been accepted due to unknown reason.
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
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.setGradeNameUnknown())); // The guild request has not been accepted due to unknown reason.
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
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.setMemberGradeUnknown())); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                try (var lockedGuild = guildResult.get().acquire()) {
                    final Guild guild = lockedGuild.get();
                    // Resolve target
                    if (!guild.hasMember(guildRequest.getTargetId())) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.setMemberGradeUnknown())); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    final GuildMember targetMember = guild.getMember(guildRequest.getTargetId());
                    // Check if requester can modify target's rank
                    if (targetMember.getGuildRank() == GuildRank.MASTER || guildRequest.getGuildRank() == GuildRank.MASTER || guildRequest.getGuildRank() == GuildRank.NONE) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.setMemberGradeUnknown())); // The guild request has not been accepted due to unknown reason.
                        return;
                    }
                    final GuildRank requesterRank = guild.getMember(remoteUser.getCharacterId()).getGuildRank();
                    if ((requesterRank != GuildRank.MASTER && requesterRank != GuildRank.SUBMASTER) ||
                            (requesterRank != GuildRank.MASTER && targetMember.getGuildRank() == GuildRank.SUBMASTER) ||
                            (requesterRank != GuildRank.MASTER && guildRequest.getGuildRank() == GuildRank.SUBMASTER)) {
                        // Non-master/submaster trying to modify rank; Non-master trying to modify rank of submaster; Non-master trying to modify rank to submaster
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You are not the master of the guild.")));
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
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.setMarkUnknown())); // The guild request has not been accepted due to unknown reason.
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
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.setMarkUnknown())); // The guild request has not been accepted due to unknown reason.
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

    public void handleBoardRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
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
            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You are not in a guild yet.")));
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
                            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.boardEntryNotFound()));
                            return;
                        }
                        final GuildBoardEntry entry = entryResult.get();
                        if (entry.getCharacterId() != remoteUser.getCharacterId() && guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                            remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You cannot edit this entry.")));
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
                                remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You are not the master of the guild yet.")));
                                return;
                            }
                            if (guild.getBoardNoticeEntry() != null) {
                                remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("Please delete the current notice and then try again.")));
                                return;
                            }
                        }
                        // Create board entry
                        final GuildBoardEntry entry = new GuildBoardEntry(
                                guild.getNextBoardEntryId(),
                                remoteUser.getCharacterId(),
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
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.viewEntryResult(entry)));
                    }
                }
                case Delete -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    if (entryResult.get().getCharacterId() != remoteUser.getCharacterId() && guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You cannot delete this entry.")));
                        return;
                    }
                    guild.removeBoardEntry(boardRequest.getEntryId());
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.loadEntryListResult(guild.getBoardNoticeEntry(), guild.getBoardEntryList(0), guild.getBoardEntries().size())));
                }
                case LoadListRequest -> {
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.loadEntryListResult(guild.getBoardNoticeEntry(), guild.getBoardEntryList(boardRequest.getStart()), guild.getBoardEntries().size())));
                }
                case ViewEntryRequest -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.viewEntryResult(entryResult.get())));
                }
                case RegisterComment -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    final GuildBoardEntry entry = entryResult.get();
                    entry.addComment(new GuildBoardComment(
                            entry.getNextCommentSn(),
                            remoteUser.getCharacterId(),
                            boardRequest.getText(),
                            Instant.now()
                    ));
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.viewEntryResult(entry)));
                }
                case DeleteComment -> {
                    final Optional<GuildBoardEntry> entryResult = guild.getBoardEntry(boardRequest.getEntryId());
                    if (entryResult.isEmpty()) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.boardEntryNotFound()));
                        return;
                    }
                    final GuildBoardEntry entry = entryResult.get();
                    final Optional<GuildBoardComment> commentResult = entry.getComment(boardRequest.getCommentSn());
                    if (commentResult.isEmpty() || (commentResult.get().getCharacterId() != remoteUser.getCharacterId() && guildRank != GuildRank.MASTER && guildRank != GuildRank.SUBMASTER)) {
                        remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.serverMsg("You cannot delete this comment.")));
                        return;
                    }
                    entry.removeComment(boardRequest.getCommentSn());
                    remoteServerNode.write(CentralPacket.userPacketReceive(remoteUser.getCharacterId(), GuildPacket.viewEntryResult(entry)));
                }
            }
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void updateGuildMember(RemoteUser remoteUser, boolean isUserUpdate) {
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

    private void forEachGuildMember(Guild guild, BiConsumer<RemoteUser, RemoteServerNode> biConsumer) {
        for (int memberId : guild.getMemberIds()) {
            final Optional<RemoteUser> remoteMemberResult = centralServerNode.getUserByCharacterId(memberId);
            if (remoteMemberResult.isEmpty()) {
                continue;
            }
            final RemoteUser member = remoteMemberResult.get();
            final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(member.getChannelId());
            if (targetNodeResult.isEmpty()) {
                return;
            }
            biConsumer.accept(member, targetNodeResult.get());
        }
    }
}
