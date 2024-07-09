package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.server.event.EventState;
import kinoko.server.event.EventType;
import kinoko.server.event.Subway;

import java.util.Map;

public final class Masteria extends ScriptHandler {
    public static final int SUBWAY_TICKET_TO_NLC = 4031711;
    public static final int SUBWAY_TICKET_TO_KERNING_CITY = 4031713;

    @Script("NLC_ticketing")
    public static void NLC_ticketing(ScriptManager sm) {
        // Bell : NLC Subway Staff (9201057)
        //   Victoria Road : Subway Ticketing Booth (103000100)
        //   Kerning City Subway : Subway Ticketing Booth (103020000)
        //   New Leaf City : NLC Subway Station (600010001)
        //   New Leaf City : Waiting Room(From NLC to KC) (600010002)
        //   Kerning City Town Street : Waiting Room(From KC to NLC) (600010004)
        if (sm.getFieldId() == 103000100) {
            // Victoria Road : Subway Ticketing Booth
            if (sm.askMenu("Hello. Would you like to buy a ticket for the subway?", Map.of(0, "New Leaf City of Masteria")) != 0) {
                return;
            }
            if (!sm.askYesNo(String.format("The ride to New Leaf City of Masteria takes off every 10 minutes, beginning on the hour, and it'll cost you #b5000 mesos#k. Are you sure you want to purchase a #b#t%d##k?", SUBWAY_TICKET_TO_NLC))) {
                return;
            }
            if (sm.canAddItem(SUBWAY_TICKET_TO_NLC, 1) && sm.addMoney(-5000)) {
                sm.addItem(SUBWAY_TICKET_TO_NLC, 1);
            } else {
                sm.sayOk("Are you sure you have #b5000 mesos#k? If so, then I urge you to check your etc. inventory, and see if it's full or not.");
            }
        } else if (sm.getFieldId() == 600010001) {
            // New Leaf City : NLC Subway Station
            if (sm.askMenu("Hello. Would you like to buy a ticket for the subway?", Map.of(0, "Kerning City of Victoria Island")) != 0) {
                return;
            }
            if (!sm.askYesNo(String.format("The ride to Kerning City of Victoria Island takes off every 10 minutes, beginning on the hour, and it'll cost you #b5000 mesos#k. Are you sure you want to purchase a #b#t%d##k?", SUBWAY_TICKET_TO_KERNING_CITY))) {
                return;
            }
            if (sm.canAddItem(SUBWAY_TICKET_TO_KERNING_CITY, 1) && sm.addMoney(-5000)) {
                sm.addItem(SUBWAY_TICKET_TO_KERNING_CITY, 1);
            } else {
                sm.sayOk("Are you sure you have #b5000 mesos#k? If so, then I urge you to check your etc. inventory, and see if it's full or not.");
            }
        } else if (sm.getFieldId() == 600010002) {
            // New Leaf City : Waiting Room(From NLC to KC)
            if (!sm.askYesNo("Do you want to go back to New Leaf City subway station now?")) {
                return;
            }
            sm.warp(103000100, "st00"); // Victoria Road : Subway Ticketing Booth
        } else if (sm.getFieldId() == 600010004) {
            // Kerning City Town Street : Waiting Room(From KC to NLC)
            if (!sm.askYesNo("Do you want to go back to Kerning City subway station now?")) {
                return;
            }
            sm.warp(600010001, "st00"); // New Leaf City : NLC Subway Station
        }
    }

    @Script("NLC_Move")
    public static void NLC_Move(ScriptManager sm) {
        // NLC Ticket Gate (9201068)
        //   New Leaf City : NLC Subway Station (600010001)
        if (!sm.hasItem(SUBWAY_TICKET_TO_KERNING_CITY)) {
            sm.sayOk("Here's the ticket reader. You are not allowed in without the ticket.");
            return;
        }
        final EventState eventState = sm.getEventState(EventType.CM_SUBWAY);
        if (eventState == EventState.SUBWAY_BOARDING) {
            if (!sm.askYesNo("It looks like there's plenty of room for this ride. Please have your ticket ready so I can let you in. The ride will be long, but you'll get to your destination just fine. What do you think? Do you want to get on this ride?")) {
                return;
            }
            if (sm.removeItem(Masteria.SUBWAY_TICKET_TO_KERNING_CITY, 1)) {
                sm.warp(Subway.WAITING_ROOM_FROM_NLC_TO_KC, "st00"); // New Leaf City : Waiting Room(From NLC to KC)
            }
        } else if (eventState == EventState.SUBWAY_WAITING) {
            sm.sayNext("This subway is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the usher at the ticketing booth.");
        } else {
            sm.sayNext("We will begin boarding 5 minutes before the takeoff. Please be patient and wait for a few minutes. Be aware that the subway will take off right on time, and we stop receiving tickets 1 minute before that, so please make sure to be here on time.");
        }
    }

    @Script("NLC_Taxi")
    public static void NLC_Taxi(ScriptManager sm) {
        // NLC Taxi (9201056)
        //   New Leaf City : NLC Town Center (600000000)
        //   Phantom Forest : Haunted House (682000000)
        if (sm.getFieldId() == 600000000) {
            // New Leaf City : NLC Town Center
            if (sm.askYesNo("Hey, there. Want to take a trip deeper into the Masterian wilderness? A lot of this continent is still quite unknown and untamed... so there's still not much in the way of roads. Good thing we've got this baby... we can go offroading, and in style too! Right now, I can drive you to the #bPhantom Forest#k. The old #bPrendergast Mansion#k is located there. Some people say the place is haunted! What do you say... want to head over there?")) {
                sm.sayNext("Alright! Buckle your seat belt, and let's head to the Mansion!\r\nIt's going to get bumpy!");
                sm.warp(682000000, "st00"); // Phantom Forest : Haunted House
            } else {
                sm.sayOk("Really? I don't blame you... Sounds like a pretty scary place to me too! If you change your mind, I'll be right here.");
            }
        } else if (sm.getFieldId() == 682000000) {
            // Phantom Forest : Haunted House
            if (sm.askYesNo("Hey, there. Hope you had fun here! Ready to head back to #bNew Leaf City#k?")) {
                sm.sayNext("Back to civilization it is! Hop in and get comfortable back there... We'll have you back to the city in a jiffy!");
                sm.warp(600000000); // New Leaf City : NLC Town Center
            } else {
                sm.sayOk("Oh, you want to stay and look around some more? That's understandable. If you wish to go back to #bNew Leaf City#k, you know who to talk to!");
            }
        }
    }

    @Script("About_NLC")
    public static void About_NLC(ScriptManager sm) {
        // Icebyrd Slimm : NLC Mayor (9201050)
        //   New Leaf City : NLC Town Center (600000000)
        final int answer = sm.askMenu("What up! Name's Icebyrd Slimm, mayor of New Leaf City! Happy to see you accepted my invite. So, what can I do for you?", Map.ofEntries(
                Map.entry(0, "What is this place?"),
                Map.entry(1, "Who is Professor Foxwit?"),
                Map.entry(2, "What's a Foxwit Door?"),
                Map.entry(3, "Where are the MesoGears?"),
                Map.entry(4, "What is the Krakian Jungle?"),
                Map.entry(5, "What's a Gear Portal?"),
                Map.entry(6, "What do the street signs mean?"),
                Map.entry(7, "What's the deal with Jack Masque?"),
                Map.entry(8, "Lita Lawless looks like a tough cookie, what's her story?"),
                Map.entry(9, "When will new boroughs open up in the city?"),
                Map.entry(10, "I want to take the quiz!")
        ));
        switch (answer) {
            case 0 -> {
                sm.sayNext("I've always dreamed of building a city. Not just any city, but one where everyone was welcome. I used to live in Kerning City, so I decided to see if I could create a city. As I went along in finding the means to do so, I encountered many people, some of whom I've come to regard as friends. Like Professor Foxwit-he's our resident genius; saved him from a group of man-eating plants. Jack Masque is an old hunting buddy from Amoria-almost too smooth of a talker for his own good. Lita and I are old friends from Kerning City-she's saved me a few times with that weapon of hers; so I figured she was a perfect choice for Town Sheriff. It took a bit of persuasion, but she came to believe her destiny lies here. About our resident explorer, Barricade came searching for something; he agreed to bring whatever he found to the museum. I'd heard stories about him and his brother when I was still in Kerning City. And Elpam...well, let's just say he's not from around here. At all. We've spoken before, and he seems to mean well, so I've allowed him to stay. I just realized that I've rambled quite a bit! What else would you like to know?");
            }
            case 1 -> {
                sm.sayNext("A pretty spry guy for being 97. He' s a time-traveler I ran into outside the city one day. Old guy had a bit of trouble with some jungle creatures-like they tried to eat him. In return for me saving him, he agreed to build a time museum. I get the feeling that he's come here for another reason, as he's mentioned more than a few times that New Leaf City has an interesting role to play in the future. Maybe you can find out a bit more... ");
            }
            case 2 -> {
                sm.sayNext("Heh, I asked the same thing when I saw the Professor building them. They're warp points. Pressing Up will warp you to another location. I recommend getting the hang of them, they're our transport system.");
            }
            case 3 -> {
                sm.sayNext("The MesoGears are beneath Bigger Ben. It's a monster-infested section of Bigger Ben that Barricade discovered. It seems to reside in a separate section of the tower-quite strange if you ask me. I hear he needs a bit of help exploring it, you should see him. Be careful though, the Wolf Spiders in there are no joke.");
            }
            case 4 -> {
                sm.sayNext("Ah...well. The Krakian Jungle is located on the outskirts of New Leaf City. Many new and powerful creatures roam those areas, so you'd better be prepared to fight if you head out there. It's at the left end of town. Rumors abound that the Jungle leads to a lost city, but we haven't found anything yet.");
            }
            case 5 -> {
                sm.sayNext("Well, when John found himself in the MesoGears portion of Bigger Ben, he stood on one and went to another location. However, he could only head back and forth-they don't cycle through like the Foxwit Door. Ancient tech for you.");
            }
            case 6 -> {
                sm.sayNext("Well, you'll see them just about everywhere. They're areas under construction. The Red lights mean it's not finished, but the Green lights mean it's open. Check back often, we're always building!");
            }
            case 7 -> {
                sm.sayNext("Ah, Jack. You know those guys that are too cool for school? The ones who always seem to get away with everything? AND get the girl? Well, that's Jack, but without the girl. He thinks he blew his chance, and began wearing that mask to hide his true identity. My lips are sealed about who he is, but he's from Amoria. He might tell you a bit more if you ask him.");
            }
            case 8 -> {
                sm.sayNext("I've known Lita for a while, thought we've just recently rekindled our friendship. I didn't see her for a quite a bit, but I understand why. She trained for a very, very long time as a Thief. Matter of fact, that's how we first met? I was besieged a group of wayward Mushrooms, and she jumped in to help. When it was time to a pick a sheriff, it was a no-brainer. She's made a promise to help others in their training and protect the city, so if you're interested in a bit of civic duty, speak with her. ");
            }
            case 9 -> {
                sm.sayNext("Soon, my friend. Even though you can't see them, the city developers are hard at work. When they're ready, we'll open them. I know you're looking forward to it and so am I!");
            }
            case 10 -> {
                if (sm.getLevel() < 15) {
                    sm.sayNext("Sorry but this quiz is only available for level 15 and above. Please come back to me when you are ready to take this quiz.");
                    return;
                }
                if (sm.hasQuestCompleted(4900)) {
                    sm.sayNext("You've already solved my questions. Enjoy your trip in NLC!!");
                    return;
                }
                sm.sayNext("No problem. I'll give you something nice if you answer them correctly!");
                sm.forceStartQuest(4900); // Welcome to New Leaf City Quiz 1
            }
        }
    }

    @Script("Sunstone")
    public static void Sunstone(ScriptManager sm) {
        // Sunstone Grave (9201071)
        //   MesoGears : Fire Chamber (600020400)
        sm.sayOk("Tempt Fate. Discover the path.");
    }

    @Script("Moonstone")
    public static void Moonstone(ScriptManager sm) {
        // Moonstone Grave (9201072)
        //   MesoGears : Ice Chamber (600020500)
        sm.sayOk("30, 101, Hidden.");
    }

    @Script("Tombstone")
    public static void Tombstone(ScriptManager sm) {
        // Tombstone (9201073)
        //   MesoGears : Enigma Chamber (600020600)
        sm.sayOk("Here lies Christopher Crimsonheart, the immortal warrior.");
    }
}
