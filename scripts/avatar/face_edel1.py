# Botoxie : Plastic Surgeon (2150005)
#   Black Wing Territory : Edelstein (310000000)

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
    20016, # Ghostface Stare
    20020, # Fierce Edge
    20017, # Demure Poise
    20013, # Insomniac Daze
    20022, # Child's Play
    20025, # Edge of Emotion
    20027, # Pensive Look
    20028, # Sarcastic Face
    20029, # Shade of Cool
    20031, # Fearful Glance
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
    21016, # Beauty Stare
    21020, # Gentle Glow
    21017, # Demure Poise Eyes
    21013, # Wide-eyed Girl
    21021, # Compassion Look
    21023, # Innocent Look
    # Lazy Look?
    21026, # Tender Love
    21027, # Glamorous Edge
    21029, # Kitty Cat
]

FACE_COUPON_VIP = 5152057

color = (sm.getFace() % 1000) - (sm.getFace() % 100)
choices = [ face + color for face in (VIP_FACE_M if sm.getGender() == 0 else VIP_FACE_F) ]
answer = sm.askAvatar("You can change your face to have a completely new look. Feel free to try it out! All you need is a #b#t5152057##k to receive your makeover. Ready for a stunning transformation?", choices) # GPT
if answer >= 0 and answer < len(choices):
    if sm.removeItem(FACE_COUPON_VIP, 1):
        sm.changeAvatar(choices[answer])
        sm.sayNext("Ok, the surgery's over. See for it yourself.. What do you think? Quite fantastic, if I should say so myself. Please come again when you want another look, okay?")
    else:
        sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
