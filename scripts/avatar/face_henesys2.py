# Dr. Feeble : Doctor w/o License (1052005)
#   Henesys : Henesys Plastic Surgery (100000103)

REG_FACE_M = [
    20000, # Motivated Look
    20001, # Perplexed Stare
    20002, # Leisure Look
    20003, # Dramatic Face
    20004, # Rebel's Fire
    20005, # Alert Face
    20006, # Babyface Pout
    20007, # Sad Innocence
    20008, # Worrisome Glare
    20012, # Curious Dog
    20014, # Look of Wonder
]
REG_FACE_F = [
    21000, # Motivated Look
    21001, # Fearful Stare
    21002, # Leisure Look
    21003, # Strong Stare
    21004, # Angel Glow
    21005, # Babyface Pout
    21006, # Pucker Up Face
    21007, # Dollface Look
    21008, # Hopeless Gaze
    21012, # Soul's Window
    21014, # Curious Look
]

FACE_COUPON_REG = 5152056

if sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?"):
    if sm.removeItem(FACE_COUPON_REG, 1):
        choices = REG_FACE_M if sm.getGender() == 0 else REG_FACE_F
        face = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getFace() % 1000) - (sm.getFace() % 100)
        sm.changeAvatar(face)
        sm.sayNext("Enjoy your new and improved face!")
    else:
        sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
