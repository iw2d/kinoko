# Franz the Owner : Plastic Surgeon (2010002)
#   Orbis Park : Orbis Plastic Surgery (200000201)

VIP_FACE_M = [
    20000, # Motivated Look
    20001, # Perplexed Stare
    20002, # Leisure Look
    20003, # Dramatic Face
    20004, # Rebel's Fire
    20005, # Alert Face
    20006, # Babyface Pout
    20008, # Worrisome Glare
    20012, # Curious Dog
    20014, # Look of Wonder
    20022, # Child's Play
    20028, # Sarcastic Face
]
VIP_FACE_F = [
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
    21023, # Innocent Look
    21026, # Tender Love
]

FACE_COUPON_VIP = 5152057

color = (sm.getFace() % 1000) - (sm.getFace() % 100)
choices = [ face + color for face in (VIP_FACE_M if sm.getGender() == 0 else VIP_FACE_F) ]
answer = sm.askAvatar("Welcome, welcome! Not happy with your look? Neither am I. But for #b#t5152057##k, I can transform your face and get you the look you've always wanted.", choices) # GPT
if answer >= 0 and answer < len(choices):
    if sm.removeItem(FACE_COUPON_VIP, 1):
        sm.changeAvatar(choices[answer])
        sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?")
    else:
        sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
