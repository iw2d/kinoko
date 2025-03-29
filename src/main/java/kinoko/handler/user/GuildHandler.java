package kinoko.handler.user;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.world.GuildPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.script.GuildHQ;
import kinoko.server.alliance.AllianceRequestType;
import kinoko.server.alliance.AllianceResultType;
import kinoko.server.guild.*;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.GameConstants;
import kinoko.world.item.InventoryManager;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GuildHandler {
    private static final Logger log = LogManager.getLogger(GuildHandler.class);

    @Handler(InHeader.GuildRequest)
    public static void handleGuildRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final GuildRequestType requestType = GuildRequestType.getByValue(type);
        switch (requestType) {
            case LoadGuild -> {
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.loadGuild(user.getGuildId()));
            }
            case CheckGuildName -> {
                final String guildName = inPacket.decodeString(); // sGuildName
                // Check if in guild HQ map
                if (user.getFieldId() != GuildHQ.GUILD_HEADQUARTERS) {
                    user.write(GuildPacket.serverMsg(null)); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                // Check if already in guild
                if (user.hasGuild()) {
                    user.write(GuildPacket.createNewGuildAlreadyJoined()); // Already joined the guild.
                    return;
                }
                // Check level requirement
                if (user.getLevel() < 101) {
                    user.write(GuildPacket.createNewGuildBeginner()); // You cannot make a guild due to the limitation of minimum level requirement.
                    return;
                }
                // Check for creation cost
                final InventoryManager im = user.getInventoryManager();
                if (!im.canAddMoney(-GameConstants.CREATE_GUILD_COST)) {
                    user.write(GuildPacket.serverMsg("You do not have enough mesos to create a guild."));
                    return;
                }
                // Check if guild name is available
                if (!DatabaseManager.guildAccessor().checkGuildNameAvailable(guildName)) {
                    user.write(GuildPacket.checkGuildNameAlreadyUsed()); // The name is already in use... Please try other ones....
                    return;
                }
                // Resolve new guild ID
                final Optional<Integer> guildIdResult = DatabaseManager.idAccessor().nextGuildId();
                if (guildIdResult.isEmpty()) {
                    user.write(GuildPacket.serverMsg(null)); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                // Deduct creation cost
                if (!im.addMoney(-GameConstants.CREATE_GUILD_COST)) {
                    throw new IllegalStateException("Could not deduct guild creation cost");
                }
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
                user.write(MessagePacket.incMoney(-GameConstants.CREATE_GUILD_COST));
                // Submit guild creation request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.createNewGuild(guildIdResult.get(), guildName));
            }
            case InviteGuild -> {
                final String targetName = inPacket.decodeString();
                // Check if guild master or submaster
                if (user.getGuildRank() != GuildRank.MASTER && user.getGuildRank() != GuildRank.SUBMASTER) {
                    user.write(GuildPacket.serverMsg("You are not the master of the guild."));
                    return;
                }
                // Submit invite guild request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.inviteGuild(targetName));
            }
            case JoinGuild -> {
                final int inviterId = inPacket.decodeInt();
                inPacket.decodeInt(); // dwCharacterId
                // Check if already in guild
                if (user.hasGuild()) {
                    user.write(GuildPacket.joinGuildAlreadyJoined());
                    return;
                }
                // Submit join guild request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.joinGuild(inviterId));
            }
            case WithdrawGuild -> {
                inPacket.decodeInt(); // dwCharacterId
                inPacket.decodeString(); // sCharacterName
                // Check if user can leave the guild
                if (!user.hasGuild()) {
                    user.write(GuildPacket.withdrawGuildNotJoined()); // You are not in the guild.
                    return;
                }
                if (user.getGuildRank() == GuildRank.MASTER) {
                    user.write(GuildPacket.serverMsg("You cannot quit the guild since you are the master of the guild."));
                    return;
                }
                // Submit withdraw guild request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.withdrawGuild(user.getGuildId()));
            }
            case KickGuild -> {
                final int targetId = inPacket.decodeInt();
                final String targetName = inPacket.decodeString();
                // Check if guild master or submaster
                if (user.getGuildRank() != GuildRank.MASTER && user.getGuildRank() != GuildRank.SUBMASTER) {
                    user.write(GuildPacket.serverMsg("You are not the master of the guild."));
                    return;
                }
                // Submit invite guild request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.kickGuild(targetId, targetName));
            }
            case SetGradeName -> {
                final List<String> gradeNames = new ArrayList<>();
                for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
                    gradeNames.add(inPacket.decodeString());
                }
                // Check if guild master
                if (user.getGuildRank() != GuildRank.MASTER) {
                    user.write(GuildPacket.serverMsg("You are not the master of the guild."));
                    return;
                }
                // Submit guild grade name request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.setGradeName(gradeNames));
            }
            case SetMemberGrade -> {
                final int targetId = inPacket.decodeInt();
                final GuildRank guildRank = GuildRank.getByValue(inPacket.decodeByte());
                if (guildRank == GuildRank.MASTER || guildRank == GuildRank.NONE) {
                    user.write(GuildPacket.serverMsg(null)); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                // Check if guild master or submaster
                if (user.getGuildRank() != GuildRank.MASTER && user.getGuildRank() != GuildRank.SUBMASTER) {
                    user.write(GuildPacket.serverMsg("You are not the master of the guild."));
                    return;
                }
                // Submit set member grade request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.setMemberGrade(targetId, guildRank));
            }
            case SetMark -> {
                final short markBg = inPacket.decodeShort();
                final byte markBgColor = inPacket.decodeByte();
                final short mark = inPacket.decodeShort();
                final byte markColor = inPacket.decodeByte();
                // Check if in guild HQ map
                if (user.getFieldId() != GuildHQ.GUILD_HEADQUARTERS) {
                    user.write(GuildPacket.serverMsg(null)); // The guild request has not been accepted due to unknown reason.
                    return;
                }
                // Check if guild master
                if (user.getGuildRank() != GuildRank.MASTER) {
                    user.write(GuildPacket.serverMsg("You are not the master of the guild."));
                    return;
                }
                // Deduct emblem cost
                final InventoryManager im = user.getInventoryManager();
                if (!im.canAddMoney(-GameConstants.CREATE_EMBLEM_COST)) {
                    user.write(GuildPacket.serverMsg("You do not have enough mesos to register a guild emblem."));
                    return;
                }
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
                user.write(MessagePacket.incMoney(-GameConstants.CREATE_GUILD_COST));
                // Submit guild emblem request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.setMark(markBg, markBgColor, mark, markColor));
            }
            case SetNotice -> {
                final String notice = inPacket.decodeString();
                // Check if guild master or submaster
                if (user.getGuildRank() != GuildRank.MASTER && user.getGuildRank() != GuildRank.SUBMASTER) {
                    user.write(GuildPacket.serverMsg("You are not the master of the guild."));
                    return;
                }
                // Submit guild notice request
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.setNotice(notice));
            }
            case null -> {
                log.error("Unknown guild request type : {}", type);
            }
            default -> {
                log.error("Unhandled guild request type : {}", requestType);
            }
        }
    }

    @Handler(InHeader.GuildResult)
    public static void handleGuildResult(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final GuildResultType resultType = GuildResultType.getByValue(type);
        switch (resultType) {
            case InviteGuild_BlockedUser, InviteGuild_AlreadyInvited, InviteGuild_Rejected -> {
                final String inviterName = inPacket.decodeString();
                inPacket.decodeString(); // sCharacterName
                // Update inviter
                user.getConnectedServer().submitUserPacketRequest(inviterName, GuildPacket.inviteGuildFailed(resultType, user.getCharacterName()));
            }
            case null -> {
                log.error("Unknown guild result type : {}", type);
            }
            default -> {
                log.error("Unhandled guild result type : {}", resultType);
            }
        }
    }

    @Handler(InHeader.AllianceRequest)
    public static void handleAllianceRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final AllianceRequestType requestType = AllianceRequestType.getByValue(type);
        // TODO
    }

    @Handler(InHeader.AllianceResult)
    public static void handleAllianceResult(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final AllianceResultType resultType = AllianceResultType.getByValue(type);
        // TODO
    }

    @Handler(InHeader.GuildBBS)
    public static void handleGuildBBS(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        if (!user.hasGuild()) {
            log.error("Received GuildBBS without a guild");
            user.write(GuildPacket.serverMsg("You are not in a guild yet."));
            return;
        }
        final GuildBoardProtocol requestType = GuildBoardProtocol.getByValue(type);
        switch (requestType) {
            case Register -> {
                // CUIGuildBBS::OnRegister
                final boolean modify = inPacket.decodeBoolean(); // bModify
                final int entryId = modify ? inPacket.decodeInt() : -1; // nCurEntryID
                final boolean notice = inPacket.decodeBoolean(); // bNotice
                final String title = inPacket.decodeString(); // sTitle
                final String text = inPacket.decodeString(); // sText
                final int emoticon = inPacket.decodeInt(); // nEmoticonID
                user.getConnectedServer().submitBoardRequest(user, GuildBoardRequest.register(modify, entryId, notice, title, text, emoticon));
            }
            case Delete -> {
                // CUIGuildBBS::OnDelete
                final int entryId = inPacket.decodeInt(); // nCurEntryID
                user.getConnectedServer().submitBoardRequest(user, GuildBoardRequest.delete(entryId));
            }
            case LoadListRequest -> {
                // CUIGuildBBS::SendLoadListRequest
                final int start = inPacket.decodeInt(); // nEntryListStart
                user.getConnectedServer().submitBoardRequest(user, GuildBoardRequest.loadList(start));
            }
            case ViewEntryRequest -> {
                // CUIGuildBBS::SendViewEntryRequest
                final int entryId = inPacket.decodeInt(); // nViewRequestEntryID
                user.getConnectedServer().submitBoardRequest(user, GuildBoardRequest.viewEntry(entryId));
            }
            case RegisterComment -> {
                // CUIGuildBBS::OnComment
                final int entryId = inPacket.decodeInt(); // nCurEntryID
                final String text = inPacket.decodeString(); // sText
                user.getConnectedServer().submitBoardRequest(user, GuildBoardRequest.registerComment(entryId, text));
            }
            case DeleteComment -> {
                // CUIGuildBBS::OnCommentDelete
                final int entryId = inPacket.decodeInt(); // nCurEntryID
                final int commentSn = inPacket.decodeInt(); // nSN
                user.getConnectedServer().submitBoardRequest(user, GuildBoardRequest.deleteComment(entryId, commentSn));
            }
            case null -> {
                log.error("Unknown guild board request type : {}", type);
            }
            default -> {
                log.error("Unhandled guild board request type : {}", requestType);
            }
        }
    }
}
