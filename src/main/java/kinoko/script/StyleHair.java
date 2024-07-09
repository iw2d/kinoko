package kinoko.script;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Util;
import kinoko.world.user.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class StyleHair extends ScriptHandler {
    public static final int SUMMER_ROYAL_HAIR_COUPON = 5150050;
    public static final int ROYAL_HAIR_COUPON = 5150040;
    public static final int HAIR_STYLE_COUPON_VIP = 5150053;
    public static final int HAIR_COLOR_COUPON_VIP = 5151036;
    public static final int HAIR_STYLE_COUPON_REG = 5150052;
    public static final int HAIR_COLOR_COUPON_REG = 5151035;
    public static final int CLAUDIAS_COUPON_EXP = 4031528;

    public static List<Integer> getHairOptions(User user, List<Integer> maleOptions, List<Integer> femaleOptions) {
        final int color = user.getCharacterStat().getHair() % 10;
        return (user.getGender() == 0 ? maleOptions : femaleOptions).stream()
                .map(option -> option + color)
                .toList();
    }

    public static List<Integer> getColorOptions(User user) {
        final int color = user.getCharacterStat().getHair() % 10;
        final int hair = user.getCharacterStat().getHair() - color;
        return Stream.of(0, 1, 2, 3, 4, 5, 6, 7)
                .map(option -> hair + option)
                .toList();
    }

    @Script("hair_royal")
    public static void hair_royal(ScriptManager sm) {
        // Big Headward : Prince (1012117)
        //   Henesys : Henesys Hair Salon (100000104)
        final List<Integer> summerHairM = List.of(
                33190, // Black Battle Mage Hair
                33210, // Black Heavy Metal Hair
                33220, // Black Sun Bleached
                33240, // Black Clean-Cut Short Hair
                33250, // Black Bed Head Hair
                33290 // Black Updo
        );
        final List<Integer> summerHairF = List.of(
                34160, // White Lilin Hair
                34180, // Black Wave Ponytail
                34190, // Black Wild Hunter Hair
                34210, // Black Lively Waved Hair
                34220, // Black Messy Pigtails
                34260, // Black Top Tied Hair
                34270 // Black Hime Hair
        );
        final List<Integer> royalHairM = List.of(
                30010, // Zeta
                30070, // All Back
                30080, // Military Buzzcut
                30090, // Mohawk
                30100, // Blue Fantasy
                30690, // Black Metro Man
                30760, // Black Bowling Ball
                33000 // Black Prince Cut
        );
        final List<Integer> royalHairF = List.of(
                31130, // Black Jolie
                31530, // Black Zessica
                31820, // Black Grace
                31920, // Black CL Hair
                31940, // Black Spunky Do
                34000, // Black Palm Tree Hair
                34030 // Black Designer Hair
        );
        final int answer = sm.askMenu("Hi. I'm Big Head Kingdom's #bBig Headward#k. If you have a #bSummer Royal Hair Coupon#k or #bRoyal Hair Coupon#k, why not let me take care of your hair?", Map.of(
                0, "Change Hairstyle (Summer Royal Hair Coupon)",
                1, "Change Hairstyle (Royal Hair Coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("When you use the Summer Royal Hair Coupon, you get a new, random hairdo. Are you sure you want to use #bSummer Royal Hair Coupon#k and change your hair?")) {
                if (sm.removeItem(SUMMER_ROYAL_HAIR_COUPON, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), summerHairM, summerHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext(String.format("How do you like it? It's the latest style, known as #b#t%d##k. Oh my, you seriously look elegant and beautiful. Ha ha ha! Well, of course! I styled it after all! Come back whenever you need me. Heh heh.", hair));
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("When you use the Royal Hair Coupon, you get a new, random hairdo. Are you sure you want to use #bRoyal Hair Coupon#k and change your hair?")) {
                if (sm.removeItem(ROYAL_HAIR_COUPON, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), royalHairM, royalHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext(String.format("How do you like it? It's the latest style, known as #b#t%d##k. Oh my, you seriously look elegant and beautiful. Ha ha ha! Well, of course! I styled it after all! Come back whenever you need me. Heh heh.", hair));
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_ariant1")
    public static void hair_ariant1(ScriptManager sm) {
        // Mazra : Hair Salon Director (2100006)
        //   The Burning Road : Ariant (260000000)
        final List<Integer> vipHairM = List.of(
                30320, // Black Afro
                30330, // Black Cabana Boy
                30150, // Black Dreadlocks
                30900, // Black Kravitz Locks
                30170, // Black Line Scratch
                30180, // Black Mane
                30820, // Black Matinee Idol
                30410, // Black Natural
                30460 // Black Tornade Hair
        );
        final List<Integer> vipHairF = List.of(
                31090, // Black Bridget
                31190, // Black Celeb
                31040, // Black Edgy
                31420, // Black Lana
                31330, // Black Penelope
                31340, // Black Rae
                31400, // Black Boyish
                31620, // Black Desert Flower
                31660 // Black Tighty Bun
        );
        final int answer = sm.askMenu("Hahaha... it takes a lot of style and flair for someone to pay attention to his or her hairsyle in a desert. Someone like you...If you have a #b#t5150053##k or #b#t5151036##k, I'll give your hair a fresh new look.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("Hahaha~ all you need is #b#t5150053##k to change up your hairstyle. Choose the new style, and let me do the rest.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Hahaha, all done! Your new hairstyle is absolutely fabulous. May your stylish hair turn heads even in the desert."); // GPT
                } else {
                    sm.sayNext("I thought I told you, you need the coupon in order for me to work magic on your hair check again.");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("Every once in a while, it doesn't hurt to change up your hair color... it's fun. Allow me, the great Mazra, to dye your hair, so you just bring me #b#t5151036##k, and choose your new hair color.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Hahaha, all done! Your new hair color is absolutely fabulous. May your stylish hair turn heads even in the desert."); // GPT
                } else {
                    sm.sayNext("I thought I told you, you need the coupon in order for me to work magic on your hair check again.");
                }
            }
        }
    }

    @Script("hair_ariant2")
    public static void hair_ariant2(ScriptManager sm) {
        // Shati : Hair Salon Assistant (2100005)
        //   The Burning Road : Ariant (260000000)
        final List<Integer> regHairM = List.of(
                30320, // Black Afro
                30330, // Black Cabana Boy
                30150, // Black Dreadlocks
                30800, // Black Dreamcatcher
                30680, // Black Hobo
                30900, // Black Kravitz Locks
                30170, // Black Line Scratch
                30180, // Black Mane
                30820, // Black Matinee Idol
                30410, // Black Natural
                // Bald Head?
                30460 // Black Tornade Hair
        );
        final List<Integer> regHairF = List.of(
                31400, // Black Boyish
                31090, // Black Bridget
                31190, // Black Celeb
                31520, // Black Curly Stream
                31650, // Black Dashing Damsel
                31620, // Black Desert Flower
                31420, // Black Lana
                31780, // Black Oh So Windy
                34000, // Black Palm Tree Hair
                31330, // Black Penelope
                31340, // Black Rae
                31660 // Black Tighty Bun
        );
        final int answer = sm.askMenu("Hey there! I'm Shati, and I', Mazra's apprentice. If you have #b#t5150052##k or #b#t5151035##k with you, how about allowing me to work on your hair?", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon, your hairstyle will be changed to a random new look. You'll also have access to new hairstyles I worked on that's not available for VIP coupons. Would you like to use #b#t5150052##k for a fabulous new look?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("The reason my hairstyle looks like this is because I've experimented different styles on myself. Good thing I did that. Yours came out awesome!");
                } else {
                    sm.sayNext("I can only change your hairstyle if you bring me the coupon. You didn't forget that, did you?");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair color will change to a random new color. Are you sure you want to use #b#t5151035##k and randomly change your hair color?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("The reason my hairstyle looks like this is because I've experimented different styles on myself. Good thing I did that. Yours came out awesome!");
                } else {
                    sm.sayNext("I can only change your hairstyle if you bring me the coupon. You didn't forget that, did you?");
                }
            }
        }
    }

    @Script("hair_edel1")
    public static void hair_edel1(ScriptManager sm) {
        // Fabio : Hair Stylist (2150003)
        //   Edelstein : Edelstein Hair Salon (310000003)
        //   Dry Road : Road to the Mine 1 (310040000)
        final List<Integer> vipHairM = List.of(
                30350, // Black Astro
                30480, // Black Babby Cut
                33190, // Black Battle Mage Hair
                30760, // Black Bowling Ball
                30330, // Black Cabana Boy
                30560, // Black Grand Lionman
                30040, // Black Rockstar
                30730, // Black Roving Rockstar
                30370, // Black Shaggy Dragon
                30470, // Black Slick Dean
                30460 // Black Tornade Hair
        );
        final List<Integer> vipHairF = List.of(
                31310, // Black Carla
                31490, // Black Cecelia Twist
                31260, // Black Daisy Do
                31130, // Black Jolie
                31160, // Black Lori
                31510, // Black Minnie
                31230, // Black Rose
                31320, // Black Roxy
                31560, // Black Sunflower Power
                34190, // Black Wild Hunter Hair
                31530 // Black Zessica
        );
        final int answer = sm.askMenu("Beauty is something that you must pursue your entire life. I can give you a new hairstyle if you have a #bHair Style Coupon#k or a #bHair Color Coupon#k!", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Change hairstyle (REG coupon)",
                2, "Dye your hair (VIP coupon)",
                3, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("All you need is a #b#t5150053##k and I can change the look of your hair. Please choose the hairstyle you would like. A new hairstyle can make all the difference!", hairOptions); // GPT
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("You might fall in love with your new hairstyle! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!"); // GPT
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new hairstyle. Would you like to use #b#t5150052##k to change your hairstyle?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("You might fall in love with your new hairstyle! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!"); // GPT
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 2) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("Finding the right color to enhance your hairstyle is very important. If you have a #b#t5151036##k, please choose the color and I'll take care of the rest.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("You might fall in love with your new hair color! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!"); // GPT
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        } else if (answer == 3) {
            if (sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Would you like to use #b#t5151035##k to dye your hair?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("You might fall in love with your new hair color! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!"); // GPT
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_henesys1")
    public static void hair_henesys1(ScriptManager sm) {
        // Natalie : Hair Salon Owner (1012103)
        //   Henesys : Henesys Hair Salon (100000104)
        final List<Integer> vipHairM = List.of(
                33040, // White Aran Cut
                30060, // Black Catalyst
                30210, // Black Shaggy Wax
                30140, // Black Topknot
                30200, // Black Wind
                33170, // Black Gaga Hair
                33100 // Black The Coco
        );
        final List<Integer> vipHairF = List.of(
                31150, // Black Angelica
                34050, // White Aran Hair
                31300, // Black Chantelle
                31700, // Black Crazy Medusa
                31350, // Black Fourtail Braids
                31740, // Black Frizzle Dizzle
                34110 // Black Full Bangs
        );
        final int answer = sm.askMenu("I'm the head of this hair salon Natalie. If you have a #b#t5150053##k or a #b#t5151036##k allow me to take care of your hairdo. Please choose the one you want.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? With #b#t5150053##k I'll change it for you. Choose the one to your liking~", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to change your hairstyle again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can totally dye your hair and make it look so good. Why don't you change it up a bit? With #b#t5151036##k I'll change it for you. Choose the one to your liking~", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to dye your hair again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_henesys2")
    public static void hair_henesys2(ScriptManager sm) {
        // Brittany : Hair Salon Assistant (1012104)
        //   Henesys : Henesys Hair Salon (100000104)
        final List<Integer> regHairM = List.of(
                33040, // White Aran Cut
                30060, // Black Catalyst
                33150, // Black Evan Hair (M);
                33170, // Black Gaga Hair
                30210, // Black Shaggy Wax
                33100, // Black The Coco
                30610, // Black The Mo Rawk
                30140, // Black Topknot
                30200 // Black Wind
        );
        final List<Integer> regHairF = List.of(
                31150, // Black Angelica
                34050, // White Aran Hair
                31300, // Black Chantelle
                31700, // Black Crazy Medusa
                31990, // Black Evan Hair (F);
                31350, // Black Fourtail Braids
                31740, // Black Frizzle Dizzle
                34110, // Black Full Bangs
                31080, // Black Rainbow
                31070 // Black Stella
        );
        final int answer = sm.askMenu("I'm Brittany the assistant. If you have #b#t5150052##k or #b#t5151035##k by any chance, then how about letting me change your hairdo?", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new hairstyle. Would you like to use #b#t5150052##k to change your hairstyle?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Would you like to use #b#t5151035##k to dye your hair?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_kerning1")
    public static void hair_kerning1(ScriptManager sm) {
        // Don Giovanni : Hair Salon Owner (1052100)
        //   Kerning City : Kerning City Hair Salon (103000005)
        final List<Integer> vipHairM = List.of(
                30130, // Black Antagonist
                33040, // White Aran Cut
                30850, // Black Cornrow
                30780, // Black Dragon Tail
                30040, // Black Rockstar
                30920, // Black Short Top Tail
                30860 // Black Male Runway Hair
        );
        final List<Integer> vipHairF = List.of(
                34050, // White Aran Hair
                31090, // Black Bridget
                31880, // Black Gardener
                31140, // Black Pei Pei
                31330, // Black Penelope
                31760, // Black Shaggy Dog
                31440 // Black Ravishing Raven
        );
        final int answer = sm.askMenu("Hello! I'm Don Giovanni, head of the beauty salon! If you have either #b#t5150053##k or #b#t5151036##k, why don't you let me take care of the rest? Decide what you want to do with your hair....", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can change your hairstyle to something totally new. Aren't you sick of your hairdo? I'll give you a haircut with #b#t5150053##k. Choose the hairstyle of your liking.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Ok, check out your new haircut. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want another haircut. I'll take care of the rest!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can change the color of your hair to something totally new. Aren't you sick of your hair-color? I'll dye your hair if you have #b#t5151036##k. Choose the color of your liking.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Ok, check out your new hair color. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want do dye your hair again. I'll take care of the rest!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_kerning2")
    public static void hair_kerning2(ScriptManager sm) {
        // Andre : Hair Salon Assistant (1052101)
        //   Kerning City : Kerning City Hair Salon (103000005)
        final List<Integer> regHairM = List.of(
                30130, // Black Antagonist
                33040, // White Aran Cut
                30850, // Black Cornrow
                30780, // Black Dragon Tail
                33130, // Black Dual Blade Hair
                30520, // Black Hontas
                30770, // Black Lucky Charms
                30040, // Black Rockstar
                30920 // Black Short Top Tail
        );
        final List<Integer> regHairF = List.of(
                31060, // Black Annie
                34050, // White Aran Hair
                31520, // Black Curly Stream
                31880, // Black Gardener
                31140, // Black Pei Pei
                31330, // Black Penelope
                31440, // Black Ravishing Raven
                31760, // Black Shaggy Dog
                31750 // Black Super Diva
        );
        final int answer = sm.askMenu("I'm Andres, Don's assistant. Everyone calls me Andre, though. If you have #b#t5150052##k or #b#t5151035##k please let me change your hairdo...", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150052##k and really change your hairstyle?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Ok, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest, but it still looks pretty good! Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Are you going to use #b#t5151035##k and really change your hair color?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Ok, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest, but it still looks pretty good! Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_ludi1")
    public static void hair_ludi1(ScriptManager sm) {
        // Miyu : Owner (2041007)
        //   Ludibrium : Ludibrium Hair Salon (220000004)
        final List<Integer> vipHairM = List.of(
                30250, // Black Afro
                30190, // Bowl Cut
                30660, // Black Fuzz
                30870, // Black Hector Hair
                30990, // Black Tentacle Hair
                30160, // Black Trip Scratch
                30640 // Black Urban Dragon
        );
        final List<Integer> vipHairF = List.of(
                31810, // Black Apple Hair
                31550, // Black Candy Heart
                31830, // Black Eye-skimming Bang
                31840, // Black Female Runway Hair
                31680, // Black Lovely Ladyhawk
                31290, // Black Naomi
                31270, // Black Pigtails
                31870 // Black Ayu
        );
        final int answer = sm.askMenu("Welcome, welcome, welcome to the Ludibrium Hair-Salon! Do you, by any chance, have #b#t5150053##k or #b#t5151036##k? If so, how about letting me take care of your hair? Please choose what you want to do with it.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150053##k, I'll take care of the rest for you.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to change your hairstyle again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5151036##k, I'll take care of the rest for you.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to dye your hair again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_ludi2")
    public static void hair_ludi2(ScriptManager sm) {
        // Mini : Assistant (2041009)
        //   Ludibrium : Ludibrium Hair Salon (220000004)
        final List<Integer> regHairM = List.of(
                30250, // Black Afro
                30190, // Bowl Cut
                30620, // Black Bubba GoaTee
                30660, // Black Fuzz
                30870, // Black Hector Hair
                30220, // Black Groovy Do
                30840, // Black Julian Hair
                30940, // Black Hip Hop Cut
                30650, // Black Rise N Shine
                30540, // Black Robot
                30990, // Black Tentacle Hair
                30610, // Black The Mo Rawk
                30640 // Black Urban Dragon
        );
        final List<Integer> regHairF = List.of(
                31810, // Black Apple Hair
                31870, // Black Ayu
                34120, // Black Bohemian Hair
                31550, // Black Candy Heart
                31830, // Black Eye-skimming Bang
                31840, // Black Female Runway Hair
                31540, // Black Jean
                31680, // Black Lovely Ladyhawk
                31290, // Black Naomi
                31270, // Black Pigtails
                31170, // Black Rastafari
                31640, // Black Sonara Wave
                31600 // Black Tall Bun
        );
        final int answer = sm.askMenu("Hi, I'm the assistant here. Don't worry, I'm plenty good enough for this. If you have #b#t5150052##k or #b#t5151035##k by any chance, then allow me to take care of the rest, alright?", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hairstyle will be changed into a random new look. Are you sure you want to use #b#t5150052##k and change it?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair color will be changed into a random new look. Are you sure you want to use #b#t5150052##k and change it?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_mureung1")
    public static void hair_mureung1(ScriptManager sm) {
        // Grandpa Luo : Lead Hair Stylist (2090100)
        //   Mu Lung : Mu Lung Hair Salon (250000003)
        final List<Integer> vipHairM = List.of(
                30750, // Black Buddha Fire
                30420, // Black Cozy Amber
                30150, // Black Dreadlocks
                30810, // Black Gruff & Tough
                30240, // Black Monkey
                30710, // Black Puffy Fro
                30370, // Black Shaggy Dragon
                30640 // Black Urban Dragon
        );
        final List<Integer> vipHairF = List.of(
                31300, // Black Chantelle
                31180, // Black Cutey Doll
                31910, // Black Housewife
                31460, // Black Lady Mariko
                31160, // Black Lori
                31470, // Black Ming Ming
                31140, // Black Pei Pei
                31660 // Black Tighty Bun
        );
        final int answer = sm.askMenu("Welcome to the Mu Lung hair shop. If you have #b#t5150053##k or a #b#t5151036##k allow me to take care of your hairdo. Please choose the one you want.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150053##k, I'll take care of the rest for you.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to change your hairstyle again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5151036##k, I'll take care of the rest for you.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to dye your hair again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_mureung2")
    public static void hair_mureung2(ScriptManager sm) {
        // Lilishu : Assistant Hair Stylist (2090101)
        //   Mu Lung : Mu Lung Hair Salon (250000003)
        final List<Integer> regHairM = List.of(
        );
        final List<Integer> regHairF = List.of(
        );
        final int answer = sm.askMenu("I'm a hair assistant in this shop. If you have #b#t5150052##k or #b#t5151035##k by any chance, then how about letting me change your hairdo?", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hairstyle will be changed into a random new look. Are you sure you want to use #b#t5150052##k and change it?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair color will be changed into a random new look. Are you sure you want to use #b#t5150052##k and change it?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_orbis1")
    public static void hair_orbis1(ScriptManager sm) {
        // Mino the Owner : Lead Hair Stylist (2010001)
        //   Orbis Park : Orbis Hair Salon (200000202)
        final List<Integer> vipHairM = List.of(
                33240, // Black Clean-Cut Short Hair
                30230, // Black Foil Perm
                30490, // Black Messy Spike
                30260, // Black Metrosexual
                30280, // Black Mohecan Shaggy Do
                33050, // Black Spiky Shag
                30340 // Black Tristan
        );
        final List<Integer> vipHairF = List.of(
                34060, // Black Bow Hair
                31220, // Black Caspia
                31110, // Black Monica
                31790, // Black Princessa
                31230, // Black Rose
                31630, // Black The Honeybun
                34260 // Black Top Tied Hair
        );
        final int answer = sm.askMenu("Hello I'm Mino the Owner. If you have either #b#t5150053##k or #b#t5151036##k, then please let me take care of your hair. Choose what you want to do with it.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150053##k, I'll take care of the rest for you.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to change your hairstyle again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5151036##k, I'll take care of the rest for you.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to dye your hair again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_orbis2")
    public static void hair_orbis2(ScriptManager sm) {
        // Rinz the Assistant : Assistant Hair Stylist (2012007)
        //   Orbis Park : Orbis Hair Salon (200000202)
        final List<Integer> regHairM = List.of(
                30530, // Black Baldie
                33240, // Black Clean-Cut Short Hair
                30230, // Black Foil Perm
                30490, // Black Messy Spike
                30260, // Black Metrosexual
                30280, // Black Mohecan Shaggy Do
                30630, // Black Neon Cactus
                30740, // Black Receding Hair
                33050, // Black Spiky Shag
                30340, // Black Tristan
                33290 // Black Updo
        );
        final List<Integer> regHairF = List.of(
                34060, // Black Bow Hair
                31220, // Black Caspia
                31650, // Black Dashing Damsel
                33160, // Black Lilin Hair
                31110, // Black Monica
                31710, // Black Princess Warrior
                31790, // Black Princessa
                31230, // Black Rose
                31890, // Black Short Twin Tails
                31630, // Black The Honeybun
                34260 // Black Top Tied Hair
        );
        final int answer = sm.askMenu("I'm Rinz, the assistant. Do you have #b#t5150052##k or #b#t5151035##k with you? If so, what do you think about letting me take care of your hairdo? What do you want to do with your hair?", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that you've never seen before. Do you want to use #b#t5150052##k and change your hair?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair color will change to a random new color. Are you sure you want to use #b#t5151035##k and randomly change your hair color?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_sg1")
    public static void hair_sg1(ScriptManager sm) {
        // Eric : Hair Stylist (9270036)
        //   Singapore : CBD (540000000)
        final List<Integer> vipHairM = List.of(
                30310, // Black Acorn
                30270, // Black w/ Bald Spot
                30110, // Black Fireball
                30840, // Black Julian Hair
                30290, // Black Old Man 'Do
                30670, // Black Preppy Spike
                30020, // Black Rebel
                30000, // Black Toben
                30120 // Black Vincent
        );
        final List<Integer> vipHairF = List.of(
                31810, // Black Apple Hair
                31930, // Black Bowl Cut
                31050, // Black Connie
                31240, // Black Disheveled
                31280, // Black Ellie
                31670, // Black Grandma ma'
                31120, // Black Miru
                31110, // Black Monica
                31010 // Black Veronica
        );
        final int answer = sm.askMenu("Welcome, welcome, welcome to the Quick-Hand Hair-Salon! Do you, by any chance, have #b#t5150053##k or #b#t5151036##k? If so, how about letting me take care of your hair? Please choose what you want to do with it.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150053##k, I'll take care of the rest for you.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to change your hairstyle again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5151036##k, I'll take care of the rest for you.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to dye your hair again, because I'll make you look good each time!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_sg2")
    public static void hair_sg2(ScriptManager sm) {
        // Jimmy : Hair Style Assistant (9270037)
        //   Singapore : CBD (540000000)
        final List<Integer> regHairM = List.of(
                30350, // Black Astro
                30110, // Black Fireball
                30840, // Black Julian Hair
                30180, // Black Mane
                30260, // Black Metrosexual
                30290, // Black Old Man 'Do
                30300, // Black Romance
                30470, // Black Slick Dean
                30720 // Black Exotica
        );
        final List<Integer> regHairF = List.of(
                31810, // Black Apple Hair
                31930, // Black Bowl Cut
                31280, // Black Ellie
                31670, // Black Grandma ma'
                31200, // Black Holla' Back Do
                31110, // Black Monica
                31780, // Black Oh So Windy
                31620, // Black Sonara Wave
                31600 // Black Tall Bun
        );
        final int answer = sm.askMenu("Hi, I'm the assistant here. Don't worry, I'm plenty good enough for this. If you have #b#t5150052##k or #b#t5151035##k by any chance, then allow me to take care of the rest, alright?", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hairstyle will be changed into a random new look. Are you sure you want to use #b#t5150052##k and change it?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair color will be changed into a random new look. Are you sure you want to use #b#t5150052##k and change it?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_shouwa1")
    public static void hair_shouwa1(ScriptManager sm) {
        // Tepei : Hair Stylist (9120100)
        //   Zipangu : Hair Salon (801000001)
        final List<Integer> vipHairM = List.of(
                30030, // Black Buzz
                33240, // Black Clean-Cut Short Hair
                30780, // Black Dragon Tail
                30810, // Black Gruff & Tough
                30820, // Black Matinee Idol
                30260, // Black Metrosexual
                30280, // Black Mohecan Shaggy Do
                30710, // Black Puffy Fro
                30920, // Black Short Top Tail
                30340 // Black Tristan
        );
        final List<Integer> vipHairF = List.of(
                31550, // Black Candy Heart
                31850, // Black Dambi
                31350, // Black Fourtail Braids
                31460, // Black Lady Mariko
                31100, // Black Mary
                31030, // Black Polly
                31790, // Black Princessa
                31000, // Black Sammy
                31770, // Black Short Shaggy Hair
                34260 // Black Top Tied Hair
        );
        final int answer = sm.askMenu("Welcome, welcome, welcome to the Showa Hair-Salon! Do you, by any chance, have #b#t5150053##k or #b#t5151036##k? If so, how about letting me take care of your hair? Please choose what you want to do with it.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150053##k, I'll take care of the rest for you.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Ok, check out your new haircut. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want another haircut. I'll take care of the rest!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5151036##k, I'll take care of the rest for you.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Ok, check out your new hair color. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want do dye your hair again. I'll take care of the rest!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_shouwa2")
    public static void hair_shouwa2(ScriptManager sm) {
        // Midori : Assistant Hair Stylist (9120101)
        //   Zipangu : Hair Salon (801000001)
        final List<Integer> regHairM = List.of(
        );
        final List<Integer> regHairF = List.of(
        );
        final int answer = sm.askMenu("Hi, I'm the assistant here. If you have #b#t5150052##k or #b#t5151035##k, please allow me to change your hairdo.", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new hairstyle. Would you like to use #b#t5150052##k to change your hairstyle?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Now, here's the mirror. What do you think of your new haircut? Doesn't it look nice for a job done by an assistant? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon, your hair color will be changed into a random new look. Are you sure you want to use #b#t5151035##k and change it?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Now, here's the mirror. What do you think of your new hair color? Doesn't it look nice for a job done by an assistant? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_wedding1")
    public static void hair_wedding1(ScriptManager sm) {
        // Julius Styleman : Heavenly Hair-Bringer (9201015)
        //   Amoria : Amoria Hair Salon (680000002)
        final List<Integer> vipHairM = List.of(
                30450, // Black Casanova
                30570, // Black Eternal Elegance
                30660, // Black Fuzz
                30910, // Black Jun Pyo Hair
                30050, // Black Metro
                30410, // Black Natural
                30510, // Black Rockie
                30300, // Black Romance
                30580, // Black Saturday Special
                30590 // Black Super Suave
                // Windy Hair?
        );
        final List<Integer> vipHairF = List.of(
                31150, // Black Angelica
                31590, // Black Ballroom Classic
                31310, // Black Carla
                31220, // Black Caspia
                31260, // Black Daisy Do
                31630, // Black The Honeybun
                31580, // Black Victorian Wrap
                31610, // Black Darling Diva
                31490, // Black Cecelia Twist
                31480, // Black Classy Sass
                31420 // Black Lana
        );
        final int answer = sm.askMenu("Welcome! My name's Julius Styleman. If you have a #b#t5150053##k or a #b#t5151036##k allow me to take care of your hairdo. Please choose the one you want.", Map.of(
                0, "Change hairstyle (VIP coupon)",
                1, "Dye your hair (VIP coupon)"
        ));
        if (answer == 0) {
            final List<Integer> hairOptions = getHairOptions(sm.getUser(), vipHairM, vipHairF);
            final int hairAnswer = sm.askAvatar("I can change your hairstyle to something totally new. Aren't you sick of your hairdo? I'll give you a haircut with #b#t5150053##k. Choose the hairstyle of your liking.", hairOptions);
            if (hairAnswer >= 0 && hairAnswer < hairOptions.size()) {
                if (sm.removeItem(HAIR_STYLE_COUPON_VIP, 1)) {
                    sm.setAvatar(hairOptions.get(hairAnswer));
                    sm.sayNext("Ok, check out your new haircut. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want another haircut. I'll take care of the rest!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            final List<Integer> colorOptions = getColorOptions(sm.getUser());
            final int colorAnswer = sm.askAvatar("I can change the color of your hair to something totally new. Aren't you sick of your hair-color? I'll dye your hair if you have #b#t5151036##k. Choose the color of your liking.", colorOptions);
            if (colorAnswer >= 0 && colorAnswer < colorOptions.size()) {
                if (sm.removeItem(HAIR_COLOR_COUPON_VIP, 1)) {
                    sm.setAvatar(colorOptions.get(colorAnswer));
                    sm.sayNext("Ok, check out your new hair color. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want do dye your hair again. I'll take care of the rest!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_wedding2")
    public static void hair_wedding2(ScriptManager sm) {
        // Salon Seamus : Streaky Stylist (9201016)
        //   Amoria : Amoria Hair Salon (680000002)
        final List<Integer> regHairM = List.of(
                30450, // Black Casanova
                30570, // Black Eternal Elegance
                30660, // Black Fuzz
                30910, // Black Jun Pyo Hair
                30050, // Black Metro
                30410, // Black Natural
                30510, // Black Rockie
                30300, // Black Romance
                30580, // Black Saturday Special
                30590 // Black Super Suave
                // Windy Hair?
        );
        final List<Integer> regHairF = List.of(
                31150, // Black Angelica
                31590, // Black Ballroom Classic
                31310, // Black Carla
                31220, // Black Caspia
                31490, // Black Cecelia Twist
                31480, // Black Classy Sass
                31260, // Black Daisy Do
                31020, // Black Francesca
                31570, // Black Maiden's Weave
                31630, // Black The Honeybun
                31580 // Black Victorian Wrap
        );
        final int answer = sm.askMenu("How's it going? I've got some new hair-do's to try out if you're game enough... what do you say? If you have a #b#t5150052##k or #b#t5151035##k, please let me change your hairdo...", Map.of(
                0, "Change hairstyle (REG coupon)",
                1, "Dye your hair (REG coupon)"
        ));
        if (answer == 0) {
            if (sm.askYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150052##k and really change your hairstyle?")) {
                if (sm.removeItem(HAIR_STYLE_COUPON_REG, 1)) {
                    final List<Integer> hairOptions = getHairOptions(sm.getUser(), regHairM, regHairF);
                    final int hair = Util.getRandomFromCollection(hairOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Are you going to use #b#t5151035##k and really change your hair color?")) {
                if (sm.removeItem(HAIR_COLOR_COUPON_REG, 1)) {
                    final List<Integer> colorOptions = getColorOptions(sm.getUser());
                    final int hair = Util.getRandomFromCollection(colorOptions).orElseThrow();
                    sm.setAvatar(hair);
                    sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!");
                } else {
                    sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
        }
    }

    @Script("hair_wedding3")
    public static void hair_wedding3(ScriptManager sm) {
        // Claudia (9201039)
        //   Amoria : Amoria Hair Salon (680000002)
        final List<Integer> expHairM = List.of(
                30270, // Black w/ Bald Spot
                30240, // Black Monkey
                30020, // Black Rebel
                30000, // Black Toben
                30132, // Orange Antagonist
                30192, // Orange Bowl Cut
                30032, // Orange Buzz
                30112, // Orange Fireball
                30162 // Orange Trip Scratch
        );
        final List<Integer> expHairF = List.of(
                31150, // Black Angelica
                31250, // Black Bowlcut
                31310, // Black Carla
                31050, // Black Connie
                31030, // Black Polly
                31070, // Black Stella
                31091, // Red Bridget
                31001 // Red Sammy
        );
        if (sm.hasQuestCompleted(8860) && !sm.hasItem(CLAUDIAS_COUPON_EXP)) {
            sm.sayNext("I've already done your hair once as a trade-for-services, sport. You'll have to snag an EXP Hair coupon from the Cash Shop if you want to change it again!");
            return;
        }
        if (sm.askYesNo("Ready for an awesome hairdo? I think you are! Just say the word, and we'll get started!")) {
            if (sm.removeItem(CLAUDIAS_COUPON_EXP, 1)) {
                final int hair = Util.getRandomFromCollection(sm.getGender() == 0 ? expHairM : expHairF).orElseThrow();
                sm.setAvatar(hair);
                sm.sayNext("Here we go!");
                sm.sayBoth("Not bad, if I do say so myself! I knew those books I studied would come in handy...");
            } else {
                sm.sayNext("Hmmm...are you sure you have our designated free coupon? Sorry but no haircut without it.");
            }
        } else {
            sm.sayNext("Ok, I'll give you a minute.");
        }
    }

    @Script("NLC_HairVip")
    public static void NLC_HairVip(ScriptManager sm) {
        // Mani : Lead Hair Stylist (9201064)
        //   New Leaf City : NLC Mall (600000001)
        sm.sayNext("TODO"); // TODO
    }

    @Script("NLC_HairExp")
    public static void NLC_HairExp(ScriptManager sm) {
        // Ari : Hair Salon Assistant (9201063)
        //   New Leaf City : NLC Mall (600000001)
        sm.sayNext("TODO"); // TODO
    }
}
