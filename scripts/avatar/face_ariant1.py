# Vard : Plastic Surgery Director (2100008)
#   The Burning Road : Ariant (260000000)

VIP_FACE_M = [
    20013, # Insomniac Daze
    20000, # Motivated Look
    20002, # Leisure Look
    20004, # Rebel's Fire
    20005, # Alert Face
    20012, # Curious Dog
]
VIP_FACE_F = [
    21009, # Look of Death
    21000, # Motivated Look
    21002, # Leisure Look
    21003, # Strong Stare
    21006, # Pucker Up Face
    21012, # Soul's Window
]

FACE_COUPON_VIP = 5152057

color = (sm.getFace() % 1000) - (sm.getFace() % 100)
choices = [ face + color for face in (VIP_FACE_M if sm.getGender() == 0 else VIP_FACE_F) ]
sm.askAvatar("Your face may be covered to combat the heat here in the desert, but truly beautiful faces seem to glow regardless. If you have #b#t5152057##k, I can assist you in uncovering your radiant potential. What do you say, shall we begin?", choices) # GPT
if answer >= 0 and answer < len(choices):
    if sm.removeItem(FACE_COUPON_VIP, 1):
        sm.changeAvatar(choices[answer])
        sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?")
    else:
        sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
