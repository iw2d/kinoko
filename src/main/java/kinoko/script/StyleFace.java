package kinoko.script;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Util;
import kinoko.world.user.User;

import java.util.List;
import java.util.Map;

public final class StyleFace extends ScriptHandler {
    public static final int ROYAL_FACE_COUPON = 5152053;
    public static final int FACE_COUPON_VIP = 5152057;
    public static final int FACE_COUPON_REG = 5152056;
    public static final int SKIN_COUPON = 5153015;

    public static List<Integer> getFaceOptions(User user, List<Integer> maleOptions, List<Integer> femaleOptions) {
        final int face = user.getCharacterStat().getFace();
        final int color = (face % 1000) - (face % 100);
        return (user.getGender() == 0 ? maleOptions : femaleOptions).stream()
                .map(option -> option + color)
                .toList();
    }

    public static void handleSkinCare(ScriptManager sm) {
        final List<Integer> options = List.of(0, 1, 2, 3, 4, 5, 9, 10, 11);
        final int answer = sm.askAvatar("We have the latest in beauty equipment. With our technology, you can preview what your skin will look like in advance! Which treatment would you like?", options);
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(SKIN_COUPON, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Here's the mirror, check it out! Doesn't your skin look beautiful and glowing like mine? Hehe, it's wonderful. Please come again!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a treatment without it. I'm sorry...");
            }
        }
    }

    @Script("face_royalGL")
    public static void face_royalGL(ScriptManager sm) {
        // Nurse Pretty : Traveling Plastic Surgeon (9201148)
        //   Henesys : Henesys Plastic Surgery (100000103)
        final List<Integer> royalFaceM = List.of(
                20036, // Male Aran Face
                20037 // Male Evan Face
        );
        final List<Integer> royalFaceF = List.of(
                21034, // Female Aran Face
                21035 // Female Evan Face
        );
        if (sm.askMenu("Hello, my name is #p9201148# and I'm a plastic surgery specialist. You can undergo my special plastic surgery if you have a #bRoyal Face Coupon#k", Map.of(0, "I'd love some special plastic surgery.")) != 0) {
            return;
        }
        if (sm.askYesNo("If you use a #bRoyal Face Coupon#k, I'll perform a special Royal plastic surgery on you, but no one knows what the results of the plastic surgery will be. It all depends on my mood. Hehehe. Shall we begin?")) {
            if (sm.removeItem(ROYAL_FACE_COUPON, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), royalFaceM, royalFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext(String.format("#b#t%d##k Do you like it? I think it looks fabulous! Come back and see me again soon!", face));
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_ariant1")
    public static void face_ariant1(ScriptManager sm) {
        // Vard : Plastic Surgery Director (2100008)
        //   The Burning Road : Ariant (260000000)
        final List<Integer> vipFaceM = List.of(
                20013, // Insomniac Daze
                20000, // Motivated Look
                20002, // Leisure Look
                20004, // Rebel's Fire
                20005, // Alert Face
                20012 // Curious Dog
        );
        final List<Integer> vipFaceF = List.of(
                21009, // Look of Death
                21000, // Motivated Look
                21002, // Leisure Look
                21003, // Strong Stare
                21006, // Pucker Up Face
                21012 // Soul's Window
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("Your face may be covered to combat the heat here in the desert, but truly beautiful faces seem to glow regardless. If you have #b#t5152057##k, I can assist you in uncovering your radiant potential. What do you say, shall we begin?", options); // GPT
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_ariant2")
    public static void face_ariant2(ScriptManager sm) {
        // Aldin : Plastic Surgery Doctor (2100009)
        //   The Burning Road : Ariant (260000000)
        final List<Integer> regFaceM = List.of(
                20013, // Insomniac Daze
                20000, // Motivated Look
                20002, // Leisure Look
                20004, // Rebel's Fire
                20005, // Alert Face
                20012 // Curious Dog
        );
        final List<Integer> regFaceF = List.of(
                21009, // Look of Death
                21000, // Motivated Look
                21002, // Leisure Look
                21003, // Strong Stare
                21006, // Pucker Up Face
                21012 // Soul's Window
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_edel1")
    public static void face_edel1(ScriptManager sm) {
        // Botoxie : Plastic Surgeon (2150005)
        //   Black Wing Territory : Edelstein (310000000)
        final List<Integer> vipFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20016, // Ghostface Stare
                20020, // Fierce Edge
                20017, // Demure Poise
                20013, // Insomniac Daze
                20022, // Child's Play
                20025, // Edge of Emotion
                20027, // Pensive Look
                20028, // Sarcastic Face
                20029, // Shade of Cool
                20031 // Fearful Glance
        );
        final List<Integer> vipFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21016, // Beauty Stare
                21020, // Gentle Glow
                21017, // Demure Poise Eyes
                21013, // Wide-eyed Girl
                21021, // Compassion Look
                21023, // Innocent Look
                // Lazy Look?
                21026, // Tender Love
                21027, // Glamorous Edge
                21029 // Kitty Cat
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("You can change your face to have a completely new look. Feel free to try it out! All you need is a #b#t5152057##k to receive your makeover. Ready for a stunning transformation?", options); // GPT
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_edel2")
    public static void face_edel2(ScriptManager sm) {
        // Wendelline : Plastic Surgery Assistant (2150006)
        //   Black Wing Territory : Edelstein (310000000)
        final List<Integer> regFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20016, // Ghostface Stare
                20020, // Fierce Edge
                20017, // Demure Poise
                20013, // Insomniac Daze
                20022, // Child's Play
                20025, // Edge of Emotion
                20027, // Pensive Look
                20028, // Sarcastic Face
                20029, // Shade of Cool
                20031 // Fearful Glance
        );
        final List<Integer> regFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21016, // Beauty Stare
                21020, // Gentle Glow
                21017, // Demure Poise Eyes
                21013, // Wide-eyed Girl
                21021, // Compassion Look
                21023, // Innocent Look
                // Lazy Look?
                21026, // Tender Love
                21027, // Glamorous Edge
                21029 // Kitty Cat
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_henesys1")
    public static void face_henesys1(ScriptManager sm) {
        // Denma the Owner : Plastic Surgeon (1052004)
        //   Henesys : Henesys Plastic Surgery (100000103)
        final List<Integer> vipFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20007, // Sad Innocence
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20015, // Eye of the Lion
                20022, // Child's Play
                20028 // Sarcastic Face
        );
        final List<Integer> vipFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21013, // Wide-eyed Girl
                21023, // Innocent Look
                21026 // Tender Love
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("Let's see... for #b#t5152057##k, you can get a new face. That's right. I can completely transform your face! Wanna give it a shot? Please consider your choice carefully.", options);
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_henesys2")
    public static void face_henesys2(ScriptManager sm) {
        // Dr. Feeble : Doctor w/o License (1052005)
        //   Henesys : Henesys Plastic Surgery (100000103)
        final List<Integer> regFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20007, // Sad Innocence
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20015, // Eye of the Lion
                20022, // Child's Play
                20028 // Sarcastic Face
        );
        final List<Integer> regFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21013, // Wide-eyed Girl
                21023, // Innocent Look
                21026 // Tender Love
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_ludi1")
    public static void face_ludi1(ScriptManager sm) {
        // Ellie : Plastic Surgeon (2041010)
        //   Ludibrium : Ludibrium Plastic Surgery (220000003)
        final List<Integer> vipFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20011 // Cool Guy Gaze
        );
        final List<Integer> vipFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21010 // Wisdom Glance
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("Let's see... for #b#t5152057##k, you can get a new face. That's right. I can completely transform your face! Wanna give it a shot? Please consider your choice carefully.", options);
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_ludi2")
    public static void face_ludi2(ScriptManager sm) {
        // Everton : Doctor Assistant (2040019)
        //   Ludibrium : Ludibrium Plastic Surgery (220000003)
        final List<Integer> regFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20011 // Cool Guy Gaze
        );
        final List<Integer> regFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21010 // Wisdom Glance
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_mureung1")
    public static void face_mureung1(ScriptManager sm) {
        // Pata : Plastic Surgeon (2090103)
        //   Mu Lung : Mu Lung (250000000)
        final List<Integer> vipFaceM = List.of(
                20010, // Anger's Blaze
                20000, // Motivated Look
                20002, // Leisure Look
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20012, // Curious Dog
                20009, // Smart Aleck
                20022, // Child's Play
                20028 // Sarcastic Face
        );
        final List<Integer> vipFaceF = List.of(
                21011, // Hypnotized Look
                21000, // Motivated Look
                21002, // Leisure Look
                21003, // Strong Stare
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21009, // Look of Death
                21023, // Innocent Look
                21026 // Tender Love
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("With our specialized machine, you can see the results of your potential treatment in advance. What kind of face would you like to have? ", options);
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_mureung2")
    public static void face_mureung2(ScriptManager sm) {
        // Noma : Assistant Plastic Surgeon (2090104)
        //   Mu Lung : Mu Lung (250000000)
        final List<Integer> regFaceM = List.of(
                20010, // Anger's Blaze
                20000, // Motivated Look
                20002, // Leisure Look
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20012, // Curious Dog
                20009, // Smart Aleck
                20022, // Child's Play
                20028 // Sarcastic Face
        );
        final List<Integer> regFaceF = List.of(
                21011, // Hypnotized Look
                21000, // Motivated Look
                21002, // Leisure Look
                21003, // Strong Stare
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21009, // Look of Death
                21023, // Innocent Look
                21026 // Tender Love
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_orbis1")
    public static void face_orbis1(ScriptManager sm) {
        // Franz the Owner : Plastic Surgeon (2010002)
        //   Orbis Park : Orbis Plastic Surgery (200000201)
        final List<Integer> vipFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20022, // Child's Play
                20028 // Sarcastic Face
        );
        final List<Integer> vipFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21023, // Innocent Look
                21026 // Tender Love
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("Welcome, welcome! Not happy with your look? Neither am I. But for #b#t5152057##k, I can transform your face and get you the look you've always wanted.", options); // GPT
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_orbis2")
    public static void face_orbis2(ScriptManager sm) {
        // Riza the Assistant : Doctor Assistant (2012009)
        //   Orbis Park : Orbis Plastic Surgery (200000201)
        final List<Integer> regFaceM = List.of(
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008, // Worrisome Glare
                20012, // Curious Dog
                20014, // Look of Wonder
                20022, // Child's Play
                20028 // Sarcastic Face
        );
        final List<Integer> regFaceF = List.of(
                21000, // Motivated Look
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21008, // Hopeless Gaze
                21012, // Soul's Window
                21023, // Innocent Look
                21026 // Tender Love
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_sg1")
    public static void face_sg1(ScriptManager sm) {
        // Kelvin : Plastic Surgery (9270024)
        //   Singapore : CBD (540000000)
        final List<Integer> vipFaceM = List.of(
                20020, // Fierce Edge
                20013, // Insomniac Daze
                20021, // Overjoyed Smile
                20026, // Shuteye
                20005, // Alert Face
                20012 // Curious Dog
        );
        final List<Integer> vipFaceF = List.of(
                21021, // Compassion Look
                21011, // Hypnotized Look
                21009, // Look of Death
                21025, // Shuteye
                21006, // Pucker Up Face
                21012 // Soul's Window
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("Let's see... for #b#t5152057##k, you can get a new face. That's right. I can completely transform your face! Wanna give it a shot? Please consider your choice carefully.", options);
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_sg2")
    public static void face_sg2(ScriptManager sm) {
        // Noel : Plastic Surgery Assistant (9270023)
        //   Singapore : CBD (540000000)
        final List<Integer> regFaceM = List.of(
                20020, // Fierce Edge
                20013, // Insomniac Daze
                20021, // Overjoyed Smile
                20026, // Shuteye
                20005, // Alert Face
                20012 // Curious Dog
        );
        final List<Integer> regFaceF = List.of(
                21021, // Compassion Look
                21011, // Hypnotized Look
                21009, // Look of Death
                21025, // Shuteye
                21006, // Pucker Up Face
                21012 // Soul's Window
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_shouwa1")
    public static void face_shouwa1(ScriptManager sm) {
        // Hikekuro : Plastic Surgeon (9120102)
        //   Zipangu : Plastic Surgery (801000002)
        final List<Integer> vipFaceM = List.of(
                20020, // Fierce Edge
                20000, // Motivated Look
                20002, // Leisure Look
                20004, // Rebel's Fire
                20005, // Alert Face
                20012 // Curious Dog
        );
        final List<Integer> vipFaceF = List.of(
                21021, // Compassion Look
                21000, // Motivated Look
                21002, // Leisure Look
                21003, // Strong Stare
                21006, // Pucker Up Face
                21008 // Hopeless Gaze
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("You can change your face to have a completely new look. Feel free to try it out! All you need is a #b#t5152057##k to receive your makeover. Ready for a stunning transformation?", options); // GPT
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_shouwa2")
    public static void face_shouwa2(ScriptManager sm) {
        // Saeko : Assistant (9120103)
        //   Zipangu : Plastic Surgery (801000002)
        final List<Integer> regFaceM = List.of(
                20020, // Fierce Edge
                20000, // Motivated Look
                20002, // Leisure Look
                20004, // Rebel's Fire
                20005, // Alert Face
                20012 // Curious Dog
        );
        final List<Integer> regFaceF = List.of(
                21021, // Compassion Look
                21000, // Motivated Look
                21002, // Leisure Look
                21003, // Strong Stare
                21006, // Pucker Up Face
                21008 // Hopeless Gaze
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_wedding1")
    public static void face_wedding1(ScriptManager sm) {
        // Dr. 90212 : Makeover Magician (9201018)
        //   Amoria : Amoria Plastic Surgery  (680000003)
        final List<Integer> vipFaceM = List.of(
                20018, // Champion Focus
                20019, // Irritable Face
                20000, // Motivated Look
                20001, // Perplexed Stare
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008 // Worrisome Glare
        );
        final List<Integer> vipFaceF = List.of(
                21018, // Athena's Grace
                21019, // Hera's Radiance
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21012 // Soul's Window
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("Ready to look like a million mesos? For #b#t5152057##k I can guarantee you'll look like a new person!.", options);
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("face_wedding2")
    public static void face_wedding2(ScriptManager sm) {
        // Intern Shakihands : Visage Nurse (9201019)
        //   Amoria : Amoria Plastic Surgery  (680000003)
        final List<Integer> regFaceM = List.of(
                20018, // Champion Focus
                20019, // Irritable Face
                20000, // Motivated Look
                20001, // Perplexed Stare
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008 // Worrisome Glare
        );
        final List<Integer> regFaceF = List.of(
                21018, // Athena's Grace
                21019, // Hera's Radiance
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21007, // Dollface Look
                21012 // Soul's Window
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("NLC_FaceVip")
    public static void NLC_FaceVip(ScriptManager sm) {
        // V. Isage : Plastic Surgeon (9201069)
        //   New Leaf City : NLC Mall (600000001)
        final List<Integer> vipFaceM = List.of(
                20012, // Curious Dog
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008 // Worrisome Glare
        );
        final List<Integer> vipFaceF = List.of(
                21016, // Beauty Stare
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21008, // Hopeless Gaze
                21012 // Soul's Window
        );
        final List<Integer> options = getFaceOptions(sm.getUser(), vipFaceM, vipFaceF);
        final int answer = sm.askAvatar("Let's see... for #b#t5152057##k, you can get a new face. That's right. I can completely transform your face! Wanna give it a shot? Please consider your choice carefully.", options);
        if (answer >= 0 && answer < options.size()) {
            if (sm.removeItem(FACE_COUPON_VIP, 1)) {
                sm.setAvatar(options.get(answer));
                sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }

    @Script("NLC_FaceExp")
    public static void NLC_FaceExp(ScriptManager sm) {
        // Nerbit : Doctor w/o License (9201070)
        //   New Leaf City : NLC Mall (600000001)
        final List<Integer> regFaceM = List.of(
                20012, // Curious Dog
                20000, // Motivated Look
                20001, // Perplexed Stare
                20002, // Leisure Look
                20003, // Dramatic Face
                20004, // Rebel's Fire
                20005, // Alert Face
                20006, // Babyface Pout
                20008 // Worrisome Glare
        );
        final List<Integer> regFaceF = List.of(
                21016, // Beauty Stare
                21001, // Fearful Stare
                21002, // Leisure Look
                21003, // Strong Stare
                21004, // Angel Glow
                21005, // Babyface Pout
                21006, // Pucker Up Face
                21008, // Hopeless Gaze
                21012 // Soul's Window
        );
        if (sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?")) {
            if (sm.removeItem(FACE_COUPON_REG, 1)) {
                final List<Integer> options = getFaceOptions(sm.getUser(), regFaceM, regFaceF);
                final int face = Util.getRandomFromCollection(options).orElseThrow();
                sm.setAvatar(face);
                sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!");
            } else {
                sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...");
            }
        }
    }


    // SKIN SCRIPTS ----------------------------------------------------------------------------------------------------

    @Script("skin_ariant1")
    public static void skin_ariant1(ScriptManager sm) {
        // Lila : Skin Care Expert (2100007)
        //   The Burning Road : Ariant (260000000)
        handleSkinCare(sm);
    }

    @Script("skin_edel1")
    public static void skin_edel1(ScriptManager sm) {
        // Mel Nomie : Skin Care Expert (2150004)
        //   Black Wing Territory : Edelstein (310000000)
        handleSkinCare(sm);
    }

    @Script("skin_henesys1")
    public static void skin_henesys1(ScriptManager sm) {
        // Ms. Tan : Dermatologist (1012105)
        //   Henesys : Henesys Skin-Care (100000105)
        sm.sayNext("Welcome to Henesys Skin-Care! For just one teeny-weeny #b#t5153015##k, I can make your skin supple and glow-y, like mine! Trust me, you don't want to miss my facials.");
        handleSkinCare(sm);
    }

    @Script("skin_ludi1")
    public static void skin_ludi1(ScriptManager sm) {
        // Gina : Dermatologist (2041013)
        //   Ludibrium : Ludibrium Skin Care (220000005)
        handleSkinCare(sm);
    }

    @Script("skin_mureung1")
    public static void skin_mureung1(ScriptManager sm) {
        // Naran : Dermatologist (2090102)
        //   Mu Lung : Mu Lung (250000000)
        handleSkinCare(sm);
    }

    @Script("skin_orbis1")
    public static void skin_orbis1(ScriptManager sm) {
        // Romi : Dermatologist (2012008)
        //   Orbis Park : Orbis Skin-Care (200000203)
        handleSkinCare(sm);
    }

    @Script("skin_sg1")
    public static void skin_sg1(ScriptManager sm) {
        // Xan : Skin Care Master (9270025)
        //   Singapore : CBD (540000000)
        handleSkinCare(sm);
    }

    @Script("NLC_Skin")
    public static void NLC_Skin(ScriptManager sm) {
        // Miranda : Dermatologist (9201065)
        //   New Leaf City : NLC Mall (600000001)
        handleSkinCare(sm);
    }


    // LENS SCRIPTS ----------------------------------------------------------------------------------------------------

    @Script("lens_henesys1")
    public static void lens_henesys1(ScriptManager sm) {
    }

    @Script("lens_ludi1")
    public static void lens_ludi1(ScriptManager sm) {
    }

    @Script("lens_orbis1")
    public static void lens_orbis1(ScriptManager sm) {
    }

    @Script("lens_sg1")
    public static void lens_sg1(ScriptManager sm) {
    }

    @Script("lens_wedding1")
    public static void lens_wedding1(ScriptManager sm) {
    }

    @Script("NLC_LensVip")
    public static void NLC_LensVip(ScriptManager sm) {
    }

    @Script("NLC_LensExp")
    public static void NLC_LensExp(ScriptManager sm) {
    }
}
