# Nurse Pretty : Traveling Plastic Surgeon (9201148)
#   Henesys : Henesys Plastic Surgery (100000103)

ROYAL_FACE_M = [
    20020, # Fierce Edge
    20021, # Overjoyed Smile
    20023, # Hypnotized Look
    20024, # Intense Stare
    20026, # Shuteye
    20028, # Sarcastic Face
    20036, # Male Aran Face
    20037, # Male Evan Face
]
ROYAL_FACE_F = [
    21020, # Gentle Glow
    21021, # Compassion Look
    21022, # Glitzy Face
    21023, # Innocent Look
    21024, # Leisure Look
    21025, # Shuteye
    21026, # Tender Love
    21034, # Female Aran Face
    21035, # Female Evan Face
]

ROYAL_FACE_COUPON = 5152053

answer = sm.askMenu("Hello, my name is #p9201148# and I'm a plastic surgery specialist. You can undergo my special plastic surgery if you have a #bRoyal Face Coupon#k.\r\n" + \
        "#L0##bI'd love some special plastic surgery.#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use a #bRoyal Face Coupon#k, I'll perform a special Royal plastic surgery on you, but no one knows what the results of the plastic surgery will be. It all depends on my mood. Hehehe. Shall we begin?"):
        if sm.removeItem(ROYAL_FACE_COUPON, 1):
            choices = ROYAL_FACE_M if sm.getGender() == 0 else ROYAL_FACE_F
            face = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getFace() % 1000) - (sm.getFace() % 100)
            sm.changeAvatar(face)
            sm.sayNext("#b#t{}##k Do you like it? I think it looks fabulous! Come back and see me again soon!".format(face))
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't perform plastic surgery for you without it. I'm sorry...")
