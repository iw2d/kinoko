# Saeko : Assistant (9120103)
#   Zipangu : Plastic Surgery (801000002)

VIP_FACE_M = [
    20020, # Fierce Edge
    20000, # Motivated Look
    20002, # Leisure Look
    20004, # Rebel's Fire
    20005, # Alert Face
    20012, # Curious Dog
]
VIP_FACE_F = [
    21021, # Compassion Look
    21000, # Motivated Look
    21002, # Leisure Look
    21003, # Strong Stare
    21006, # Pucker Up Face
    21008, # Hopeless Gaze
]

FACE_COUPON_REG = 5152056

if sm.askYesNo("If you use the regular coupon, you may end up with a random new look for your face...do you still want to do it using #b#t5152056##k?"):
    if sm.removeItem(FACE_COUPON_REG, 1):
        choices = VIP_FACE_M if sm.getGender() == 0 else VIP_FACE_F
        face = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getFace() % 1000) - (sm.getFace() % 100)
        sm.changeAvatar(face)
        sm.sayNext("The surgery's complete. Don't you like it? I think it came out great!")
    else:
        sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
