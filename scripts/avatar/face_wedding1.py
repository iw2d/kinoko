# Dr. 90212 : Makeover Magician (9201018)
#   Amoria : Amoria Plastic Surgery  (680000003)

VIP_FACE_M = [
    20018, # Champion Focus
    20019, # Irritable Face
    20000, # Motivated Look
    20001, # Perplexed Stare
    20003, # Dramatic Face
    20004, # Rebel's Fire
    20005, # Alert Face
    20006, # Babyface Pout
    20008, # Worrisome Glare
]
VIP_FACE_F = [
    21018, # Athena's Grace
    21019, # Hera's Radiance
    21001, # Fearful Stare
    21002, # Leisure Look
    21003, # Strong Stare
    21004, # Angel Glow
    21005, # Babyface Pout
    21006, # Pucker Up Face
    21007, # Dollface Look
    21012, # Soul's Window
]

FACE_COUPON_VIP = 5152057

color = (sm.getFace() % 1000) - (sm.getFace() % 100)
choices = [ face + color for face in (VIP_FACE_M if sm.getGender() == 0 else VIP_FACE_F) ]
answer = sm.askAvatar("Ready to look like a million mesos? For #b#t5152057##k I can guarantee you'll look like a new person!.", choices)
if answer >= 0 and answer < len(choices):
    if sm.removeItem(FACE_COUPON_VIP, 1):
        sm.changeAvatar(choices[answer])
        sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?")
    else:
        sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
