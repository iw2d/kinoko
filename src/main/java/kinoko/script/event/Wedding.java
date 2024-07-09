package kinoko.script.event;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

public final class Wedding extends ScriptHandler {
    @Script("Thomas")
    public static void Thomas(ScriptManager sm) {
        // Thomas Swift : Amoria Ambassador (9201022)
        //   Henesys : Henesys (100000000)
        //   Amoria : Amoria (680000000)
        if (sm.getFieldId() == 100000000) {
            // Henesys : Henesys
            if (sm.askYesNo("I can take you to Amoria Village. Are you ready to go?")) {
                sm.sayNext("I hope you had a great time! See you around!");
                sm.warp(680000000); // Amoria : Amoria
            } else {
                sm.sayOk("Ok, feel free to hang around until you're ready to go!");
            }
        } else if (sm.getFieldId() == 680000000) {
            // Amoria : Amoria
            if (sm.askYesNo("I can take you back to your original location. Are you ready to go?")) {
                sm.sayNext("I hope you had a great time! See you around!");
                sm.warp(100000000); // Henesys : Henesys
            } else {
                sm.sayOk("Ok, feel free to hang around until you're ready to go!");
            }
        }
    }

    @Script("ProofHene")
    public static void ProofHene(ScriptManager sm) {
        // Nana(H) : Love Fairy (9201001)
        //   Henesys : Henesys (100000000)
        sm.sayNext("Nice to meet you! I am Nana the Fairy from Amoria. I am waiting for you to prove your devotion to your loved one by obtaining a Proof of Love! To start, you'll have to venture to Amoria to find my good friend, Moony the Ringmaker. Even if you are not interested in marriage yet, Amoria is open for everyone! Go visit Thomas Swift at Henesys to head to Amoria. If you are interested in weddings, be sure to speak with Ames the Wise once you get there!");
    }

    @Script("ProofElli")
    public static void ProofElli(ScriptManager sm) {
        // Nana(E) : Love Fairy (9201024)
        //   Ellinia : Ellinia (101000000)
        sm.sayNext("Nice to meet you! I am Nana the Fairy from Amoria. I am waiting for you to prove your devotion to your loved one by obtaining a Proof of Love! To start, you'll have to venture to Amoria to find my good friend, Moony the Ringmaker. Even if you are not interested in marriage yet, Amoria is open for everyone! Go visit Thomas Swift at Henesys to head to Amoria. If you are interested in weddings, be sure to speak with Ames the Wise once you get there!");
    }

    @Script("ProofPeri")
    public static void ProofPeri(ScriptManager sm) {
        // Nana(P) : Love Fairy (9201027)
        //   Perion : Perion (102000000)
        sm.sayNext("Nice to meet you! I am Nana the Fairy from Amoria. I am waiting for you to prove your devotion to your loved one by obtaining a Proof of Love! To start, you'll have to venture to Amoria to find my good friend, Moony the Ringmaker. Even if you are not interested in marriage yet, Amoria is open for everyone! Go visit Thomas Swift at Henesys to head to Amoria. If you are interested in weddings, be sure to speak with Ames the Wise once you get there!");
    }

    @Script("ProofKern")
    public static void ProofKern(ScriptManager sm) {
        // Nana(K) : Love Fairy (9201023)
        //   Kerning City : Kerning City (103000000)
        //   Sunset Road : Magatia (261000000)
        //   Singapore : Boat Quay Town (541000000)
        sm.sayNext("Nice to meet you! I am Nana the Fairy from Amoria. I am waiting for you to prove your devotion to your loved one by obtaining a Proof of Love! To start, you'll have to venture to Amoria to find my good friend, Moony the Ringmaker. Even if you are not interested in marriage yet, Amoria is open for everyone! Go visit Thomas Swift at Henesys to head to Amoria. If you are interested in weddings, be sure to speak with Ames the Wise once you get there!");
    }

    @Script("ProofOrbi")
    public static void ProofOrbi(ScriptManager sm) {
        // Nana(O) : Love Fairy (9201025)
        //   Orbis : Orbis (200000000)
        sm.sayNext("Nice to meet you! I am Nana the Fairy from Amoria. I am waiting for you to prove your devotion to your loved one by obtaining a Proof of Love! To start, you'll have to venture to Amoria to find my good friend, Moony the Ringmaker. Even if you are not interested in marriage yet, Amoria is open for everyone! Go visit Thomas Swift at Henesys to head to Amoria. If you are interested in weddings, be sure to speak with Ames the Wise once you get there!");
    }
}
