# Brittany : Hair Salon Assistant (1012104)
#   Henesys : Henesys Hair Salon (100000104)

REG_HAIR_M = [
    33040, # White Aran Cut
    30060, # Black Catalyst
    33150, # Black Evan Hair (M)
    33170, # Black Gaga Hair
    30210, # Black Shaggy Wax
    33100, # Black The Coco
    30610, # Black The Mo Rawk
    30140, # Black Topknot
    30200, # Black Wind
]
REG_HAIR_F = [
    31150, # Black Angelica
    34050, # White Aran Hair
    31300, # Black Chantelle
    31700, # Black Crazy Medusa
    31990, # Black Evan Hair (F)
    31350, # Black Fourtail Braids
    31740, # Black Frizzle Dizzle
    34110, # Black Full Bangs
    31080, # Black Rainbow
    31070, # Black Stella
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("I'm Brittany the assistant. If you have #b#t5150052##k or #b#t5151035##k by any chance, then how about letting me change your hairdo?\r\n" + \
    "#L0##bHaircut (REG coupon)#k#l\r\n" + \
    "#L1##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new hairstyle. Would you like to use #b#t5150052##k to change your hairstyle?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Would you like to use #b#t5151035##k to dye your hair?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
