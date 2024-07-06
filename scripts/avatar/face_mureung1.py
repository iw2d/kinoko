# Pata : Plastic Surgeon (2090103)
#   Mu Lung : Mu Lung (250000000)

VIP_FACE_M = [
    20010, # Anger's Blaze
    20000, # Motivated Look
    20002, # Leisure Look
    20004, # Rebel's Fire
    20005, # Alert Face
    20006, # Babyface Pout
    20012, # Curious Dog
    20009, # Smart Aleck
    20022, # Child's Play
    20028, # Sarcastic Face
]
VIP_FACE_F = [
    21011, # Hypnotized Look
    21000, # Motivated Look
    21002, # Leisure Look
    21003, # Strong Stare
    21005, # Babyface Pout
    21006, # Pucker Up Face
    21008, # Hopeless Gaze
    21012, # Soul's Window
    21009, # Look of Death
    21023, # Innocent Look
    21026, # Tender Love
]

FACE_COUPON_VIP = 5152057

color = (sm.getFace() % 1000) - (sm.getFace() % 100)
choices = [ face + color for face in (VIP_FACE_M if sm.getGender() == 0 else VIP_FACE_F) ]
answer = sm.askAvatar("With our specialized machine, you can see the results of your potential treatment in advance. What kind of face would you like to have? ", choices)
if answer >= 0 and answer < len(choices):
    if sm.removeItem(FACE_COUPON_VIP, 1):
        sm.changeAvatar(choices[answer])
        sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?")
    else:
        sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
