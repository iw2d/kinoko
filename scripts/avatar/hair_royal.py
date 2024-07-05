# Big Headward : Prince (1012117)
#   Henesys : Henesys Hair Salon (100000104)

ROYAL_HAIR_M = [
    30010, # Zeta
    30070, # All Back
    30080, # Military Buzzcut
    30090, # Mohawk
    30100, # Blue Fantasy
    30690, # Black Metro Man
    30760, # Black Bowling Ball
    33000, # Black Prince Cut
]
ROYAL_HAIR_F = [
    31130, # Black Jolie
    31530, # Black Zessica
    31820, # Black Grace
    31920, # Black CL Hair
    31940, # Black Spunky Do
    34000, # Black Palm Tree Hair
    34030, # Black Designer Hair
]
SUMMER_HAIR_M = [
    33190, # Black Battle Mage Hair
    33210, # Black Heavy Metal Hair
    33220, # Black Sun Bleached
    33240, # Black Clean-Cut Short Hair
    33250, # Black Bed Head Hair
    33290, # Black Updo
]
SUMMER_HAIR_F = [
    34160, # White Lilin Hair
    34180, # Black Wave Ponytail
    34190, # Black Wild Hunter Hair
    34210, # Black Lively Waved Hair
    34220, # Black Messy Pigtails
    34260, # Black Top Tied Hair
    34270, # Black Hime Hair
]

ROYAL_HAIR_COUPON = 5150040
SUMMER_ROYAL_HAIR_COUPON = 5150050

answer = sm.askMenu("Hi. I'm Big Head Kingdom's #bBig Headward#k. If you have a #bSummer Royal Hair Coupon#k or #bRoyal Hair Coupon#k, why not let me take care of your hair?\r\n" + \
        "#L0##bChange Hairstyle (Summer Royal Hair Coupon)#k#l\r\n" + \
        "#L1##bChange HairStyle (Royal Hair Coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("When you use the Summer Royal Hair Coupon, you get a new, random hairdo. Are you sure you want to use #bSummer Royal Hair Coupon#k and change your hair?"):
        if sm.removeItem(SUMMER_ROYAL_HAIR_COUPON, 1):
            choices = SUMMER_HAIR_M if sm.getGender() == 0 else SUMMER_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("How do you like it? It's the latest style, known as #b#t{}##k. Oh my, you seriously look elegant and beautiful. Ha ha ha! Well, of course! I styled it after all! Come back whenever you need me. Heh heh.".format(hair))
        else:
            sm.sayNext("Hmm... it looks like you don't have our designated coupon... I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("When you use the Royal Hair Coupon, you get a new, random hairdo. Are you sure you want to use #bRoyal Hair Coupon#k and change your hair?"):
        if sm.removeItem(ROYAL_HAIR_COUPON, 1):
            choices = ROYAL_HAIR_M if sm.getGender() == 0 else ROYAL_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("How do you like it? It's the latest style, known as #b#t{}##k. Oh my, you seriously look elegant and beautiful. Ha ha ha! Well, of course! I styled it after all! Come back whenever you need me. Heh heh.".format(hair))
        else:
            sm.sayNext("Hmm... it looks like you don't have our designated coupon... I'm afraid I can't give you a haircut without it. I'm sorry...")
