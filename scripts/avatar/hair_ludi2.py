# Mini : Assistant (2041009)
#   Ludibrium : Ludibrium Hair Salon (220000004)

REG_HAIR_M = [
    30250, # Black Afro
    30190, # Bowl Cut
    30620, # Black Bubba GoaTee
    30660, # Black Fuzz
    30870, # Black Hector Hair
    30220, # Black Groovy Do
    30840, # Black Julian Hair
    30940, # Black Hip Hop Cut
    30650, # Black Rise N Shine
    30540, # Black Robot
    30990, # Black Tentacle Hair
    30610, # Black The Mo Rawk
    30640, # Black Urban Dragon
]
REG_HAIR_F = [
    31810, # Black Apple Hair
    31870, # Black Ayu
    34120, # Black Bohemian Hair
    31550, # Black Candy Heart
    31830, # Black Eye-skimming Bang
    31840, # Black Female Runway Hair
    31540, # Black Jean
    31680, # Black Lovely Ladyhawk
    31290, # Black Naomi
    31270, # Black Pigtails
    31170, # Black Rastafari
    31640, # Black Sonara Wave
    31600, # Black Tall Bun
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("Hi, I'm the assistant here. Don't worry, I'm plenty good enough for this. If you have #b#t5150052##k or #b#t5151035##k by any chance, then allow me to take care of the rest, alright?\r\n" + \
    "#L0##bChange hair-style (REG coupon)#k#l\r\n" + \
    "#L1##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use the regular coupon, your hair-style will be changed into a random new look. Are you sure you want to use #b#t5150052##k and change it?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? If you ever feel like changing it up again later, please drop by.")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("If you use the regular coupon, your hair color will be changed into a random new look. Are you sure you want to use #b#t5151035##k and change it?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? If you ever feel like changing it up again later, please drop by.")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
