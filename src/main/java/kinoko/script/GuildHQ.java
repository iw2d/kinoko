package kinoko.script;

import kinoko.packet.world.GuildPacket;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.server.guild.GuildRank;
import kinoko.server.guild.GuildRequest;
import kinoko.world.GameConstants;
import kinoko.world.user.User;

import java.util.Map;

public final class GuildHQ extends ScriptHandler {
    public static final int GUILD_HEADQUARTERS = 200000301;

    @Script("guild_proc")
    public static void guild_proc(ScriptManager sm) {
        // Heracle (2010007)
        //   Orbis : Guild Headquarters <Hall of Fame> (200000301)
        final User user = sm.getUser();
        if (!user.hasGuild()) {
            sm.sayNext("Hey...would you happen to be interested in GUILDS by any chance?");
            final int answer = sm.askMenu(null, Map.of( // null shows a bar
                    0, "What's a guild?",
                    1, "What do I do to form a guild?",
                    2, "I want to start a guild."
            ));
            if (answer == 0) {
                sm.sayNext("You can think of a guild as a small crew full of people with similar interests and goals, except it will be officially registered in our Guild Headquarters and be accepted as a valid GUILD.");
                // sm.sayBoth("There are a variety of benefits that you can get through guild activities. For example, you can obtain a guild skill or an item that is exclusive to guilds.");
            } else if (answer == 1) {
                sm.sayNext("You must be at least Lv. 101 to create a guild.");
                sm.sayBoth(String.format("You also need %,d mesos. This is the registration fee.", GameConstants.CREATE_GUILD_COST));
                sm.sayBoth("So, come see me if you would like to register a guild! Oh, and of course you can't be already registered to another guild!");
            } else if (answer == 2) {
                if (!sm.askYesNo(String.format("Oh! So you're here to register a guild... You need %,d mesos to register a guild. I trust that you are ready. Would you like to create a guild?", GameConstants.CREATE_GUILD_COST))) {
                    sm.sayNext("You're not ready yet? Come back to me when you want to create a guild.");
                    return;
                }
                if (sm.getLevel() < 101) {
                    sm.sayNext("Hey, you level is a bit low to be a guild leader. You need to be at least level 101 to create a guild.");
                    return;
                }
                if (!sm.canAddMoney(-GameConstants.CREATE_GUILD_COST)) {
                    sm.sayNext("Please check again. You'll need to pay the service fee to create and register a guild.");
                    return;
                }
                sm.sayNext("Enter the name of your guild, and your guild will be created. The guild will also be officially registered under our Guild Headquarters, so best of luck to you and your guild!");
                sm.write(GuildPacket.inputGuildName());
            }
        } else {
            final int answer = sm.askMenu("What do you need help with?", Map.of(
                    0, "I want to expand the guild.",
                    1, "I want to disband the guild.",
                    2, "I want to change the guild leader."
            ));
            if (user.getGuildRank() != GuildRank.MASTER) {
                sm.sayNext("Hey, you're not the Guild Master!! This decision can only be made by the Guild Master.");
                return;
            }
            if (answer == 0) {
                sm.sayNext(String.format("Are you here because you want to expand your guild? To increase the number of people you can accept into your guild, you'll have to re-register. You'll also have to pay a fee. Just so you know, the absolute maximum size for a guild is %d members.", GameConstants.GUILD_CAPACITY_MAX));
                if (!sm.askYesNo("#TODO")) {

                }
                // TODO
            } else if (answer == 1) {
                if (!sm.askYesNo("Are you sure you want to break up your guild?\r\nRemember, once you break up your guild, it will be gone forever. Are you sure you still want to do it?")) {
                    sm.sayNext("Good choice. You can't just give up on a guild that you build yourself...");
                    return;
                }
                if (!sm.askYesNo("I'll ask one more time. Would you like to give up all guild privileges and disband the guild?")) {
                    sm.sayNext("Good choice. You can't just give up on a guild that you build yourself...");
                    return;
                }
                user.getConnectedServer().submitGuildRequest(user, GuildRequest.removeGuild(user.getGuildId()));
            }
        }
    }

    @Script("guild_mark")
    public static void guild_mark(ScriptManager sm) {
        // Lea : Guild Emblem Creator (2010008)
        //   Orbis : Guild Headquarters <Hall of Fame> (200000301)
        final User user = sm.getUser();
        final int answer = sm.askMenu("Hi! My name is #bLea#k. I am in charge of the #bGuild Emblem#k.", Map.of(
                0, "I'd like to register a guild emblem.",
                1, "I'd like to delete a guild emblem."
        ));
        if (user.getGuildRank() != GuildRank.MASTER) {
            sm.sayNext("Hey, you're not the Guild Master!! This decision can only be made by the Guild Master.");
            return;
        }
        if (answer == 0) {
            if (!sm.askYesNo(String.format("There is a fee of #r%,d mesos#k for creating a Guild Emblem. To further explain, a Guild Emblem is like a coat of arms that is unique to a guild. It will be displayed to the left of the guild name. How does that sound? Would you like to create a Guild Emblem?", GameConstants.CREATE_EMBLEM_COST))) {
                sm.sayNext("Oh... okay... A guild emblem would make the guild more unified. Do you need more time for preparing for the guild emblem? Please come back to me when you are ready...");
                return;
            }
            if (!sm.canAddMoney(-GameConstants.CREATE_EMBLEM_COST)) {
                sm.sayNext("Oh... You don't have enough mesos to create an emblem...");
                return;
            }
            sm.write(GuildPacket.inputMark());
        } else if (answer == 1) {
            if (user.getGuildInfo().getMark() == 0) {
                sm.sayNext("You don't have a guild emblem to delete...");
                return;
            }
            if (!sm.askYesNo(String.format("If you delete the current guild emblem, you can make a new guild emblem. You will need #r%,d mesos#k to delete your guild emblem. Would you like to do it?", GameConstants.DELETE_EMBLEM_COST))) {
                sm.sayNext("Please come back to me when you are ready.");
                return;
            }
            if (!sm.addMoney(-GameConstants.DELETE_EMBLEM_COST)) {
                sm.sayNext("Oh... You don't have enough mesos to delete the emblem.");
                return;
            }
            user.getConnectedServer().submitGuildRequest(user, GuildRequest.setMark((short) 0, (byte) 0, (short) 0, (byte) 0));
        }
    }

    @Script("guild_union")
    public static void guild_union(ScriptManager sm) {
        // Lenario : Manager of Guild Union (2010009)
        //   Orbis : Guild Headquarters <Hall of Fame> (200000301)
        final int answer = sm.askMenu("Hello there! I'm #bLenario#k.", Map.of(
                0, "Can you please tell me what a Guild Union is all about?",
                1, "How do I make a Guild Union?",
                2, "I want to make a Guild Union.",
                3, "I want to add more guilds to the Guild Union.",
                4, "I want to disband the Guild Union."
        ));
        if (answer == 0) {
            sm.sayNext("Guild Union is just as its name suggests, which is a union of several guilds, joined together to form a super group. I am in charge of managing these Guild Unions");
        } else if (answer == 1) {
            sm.sayNext("To create a guild union, the 2 guild leaders must make a party. The leader of the party will become the guild union leader.");
            sm.sayBoth("Once you have 2 guild leaders together, you'll need 5 million mesos to register the guild union.");
            sm.sayBoth("One more thing! It should be obvious, but you cannot create a new Guild Union if you already belong as a member to another one!");
        } else if (answer == 2) {
            if (sm.getUser().getGuildRank() != GuildRank.MASTER) {
                sm.sayNext("Only the guild leader can form a Guild Union.");
                return;
            }
            if (!sm.getUser().isPartyBoss()) {
                sm.sayNext("Only the party leader can form a Guild Union.");
                return;
            }
            sm.sayNext("You can create a Guild Union if your party consists of two people.");
            // TODO
        } else if (answer == 3) {
            if (sm.getUser().getGuildRank() != GuildRank.MASTER) {
                sm.sayNext("Only the Guild Union Master can expand the number of guilds in the Union.");
            }
            // TODO
        } else if (answer == 4) {
            if (sm.getUser().getGuildRank() != GuildRank.MASTER) {
                sm.sayNext("Only the Guild Union Master may disband the Guild Union.");
            }
            // TODO
        }
    }
}
