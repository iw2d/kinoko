package kinoko.script.party;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.user.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public final class AmoriaPQ extends ScriptHandler {
    public static final int EXIT = 910340000;
    public static final int STAGE_1 = 670010200;
    public static final int STAGE_2 = 670010300;
    public static final int STAGE_2_DUSK = 670010301;
    public static final int STAGE_3 = 910340300;
    public static final int STAGE_4 = 910340400;
    public static final int STAGE_5 = 910340500;
    public static final int ENTRANCE_TICKET = 4031592;

    @Script("PartyAmoria_enter")
    public static void PartyAmoria_enter(ScriptManager sm) {
        final int pqStatus = 8883;
        final int pqLastTime = 8884;
        String pqStatusValue = sm.getQRValue(pqStatus);
        String pqLastTimeValue = sm.getQRValue(pqLastTime);

        if (sm.getFieldId() == 670010000) {
            if (pqStatusValue.equals("end")) {
                sm.sayOk("Do you have the ticket with you? Okay, now I'll take you to the entrance of the Amoria Party Quest. Your fellow members of the party should be there waiting for you!");
                if (!sm.removeItem(ENTRANCE_TICKET, 1)) {
                    sm.sayOk("Don't you have the Entrance Ticket? Oh no. I'm sorry, but I'll have to ask you to reacquire 10 Lip Lock Keys and give them to me. Then, and only then, will I give you another ticket.");
                    return;
                }

                sm.warp(670010100, "st00");
                sm.setQRValue(pqStatus, "");
            } else if (pqStatusValue.equals("ing")) {
                // TODO: add check if married
                if (sm.getLevel() < 40) {
                    sm.sayOk("I see a fine fighting spirit in you, my friend. Sadly, it not fully developed. You'll need to be at least Level 40 to enter my Hunting ground!");
                    return;
                }

                sm.sayOk("I want you to gather 10 Lip Lock Keys to prove yourself worthy of entry. You might want to try hunting the Indigo Eyes, they seem to like the look of them. After that, I'll let you in to see what you're made of!");
                if (!sm.removeItem(4031593, 10)) {
                    sm.sayOk("Let's see, 1,2,3... not 10. My brother may be the wise one, but I'm no slouch either. You need 10 before I'll give you the Amorian Challenge Entrance Ticket.");
                    return;
                }

                sm.sayOk("Ah! A worthy warrior and his party! Here's the Ticket. Good luck!");
                sm.addItem(4031592, 1);
                sm.setQRValue(pqStatus, "end");
                sm.setQRValue(pqLastTime, ""); // TODO: current time
            } else {
                if (!sm.askYesNo("I am Amos the Strong! The warrior who once defeated a Balrog with nothing but my trusty sword'and wits! I have a challenge for your group should you be up for it! What do you say?")) {
                    sm.sayOk("Can't say I blame you, friend. Come on back when you're good and strong, I'll be waiting.");
                    return;
                }

                sm.sayOk("Stellar! Let me warn you-my challenges are not for those with weak weapons and puny minds! I built this hunting ground as a testament for those to protect their loved ones. To do this, you must be strong! I will put you to the test! Please talk to me again.");
                sm.setQRValue(pqStatus, "ing"); // TODO: only set if time passed
            }
        }
    }

    @Script("PartyAmoria_enter2")
    public static void PartyAmoria_enter2(ScriptManager sm) {
        if (sm.getFieldId() == 670010100) {
            final int selection = sm.askMenu("Okay. What would you like to do?", Map.of(
                    0, "I'd like to start the Party Quest.",
                    1, "Please get us out of here!"
            ));

            if (selection == 0) {
                // PQ
                if (!sm.getUser().getPartyInfo().isBoss()) {
                    sm.sayOk("How about you and your party members collectively beating a quest? Here you'll find obstacles and problems where you won't be able to beat it unless with great teamwork. If you want to try it, please tell the #bleader of your party#k to talk to me.");
                    return;
                }

                sm.sayOk("Good, the leader of the party is here. Now, are you and your party members ready for this? I'll send you guys now to the entrance of the Amoria Party Quest. Best of luck to each and every one of you!");

                if (!sm.checkParty(1, 15)) {
                    sm.sayOk("You are not in the party. You only can do this quest when you are in the party.");
                    return;
                }

                if (!checkGender(sm)) {
                    sm.sayOk("You need at least one bride and one groom to participate in this party quest.");
                    return;
                }

                sm.removeItem(4031592, 1);
            } else {
                sm.sayOk("Hmm... Well, see you next time. Bye~!");
                sm.warp(670010000, "st00");
            }
        }
    }

    private static boolean checkGender(ScriptManager sm) {
        boolean groom = false;
        boolean bride = false;
        final List<User> members = sm.getField().getUserPool().getPartyMembers(sm.getUser().getPartyId());
        for (User member : members) {
            if (member.getGender() == 0) {
                groom = true;
            } else if (member.getGender() == 1) {
                bride = true;
            }
        }
        return groom && bride;
    }

    @Script("PartyAmoria_play")
    public static void PartyAmoria_play(ScriptManager sm) {
        int warpTo = -1;
        final User user = sm.getUser();
        final Field field = sm.getField();
        switch (field.getFieldId()) {
            case STAGE_1 -> {
                String stage1 = sm.getInstanceVariable("stage1_clear");
                String value = sm.getInstanceVariable("stage1down");
                int hour = ZonedDateTime.now(ZoneId.of("UTC")).getHour();
                if (hour <= 16) {
                    warpTo = STAGE_2; // day
                } else {
                    warpTo = STAGE_2_DUSK; // dusk
                }

                if (stage1.equals("1")) {
                    sm.sayOk("Great job completing the first stage. I'll now take you to the second stage.");
                } else {
                    if (value.equals("ing")) {
                        sm.sayOk("Now now, you may want to think it over again. See which portal will open...");

                    }
                }
            }
        }
    }
}