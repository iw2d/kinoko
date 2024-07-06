# Lilishu : Assistant Hair Stylist (2090101)
#   Mu Lung : Mu Lung Hair Salon (250000003)

REG_HAIR_M = [
    30830, # Black Alex
    30750, # Black Buddha Fire
    30420, # Black Cozy Amber
    30150, # Black Dreadlocks
    30720, # Black Exotica
    30810, # Black Gruff & Tough
    30550, # Black Kongfu Braids
    30240, # Black Monkey
    30710, # Black Puffy Fro
    30700, # Black Rising Rocker
    30370, # Black Shaggy Dragon
    30600, # Black The Curl
    30640, # Black Urban Dragon
]
REG_HAIR_F = [
    31300, # Black Chantelle
    31180, # Black Cutey Doll
    31690, # Black Demolishing Diva
    31910, # Black Housewife
    31460, # Black Lady Mariko
    31160, # Black Lori
    31470, # Black Ming Ming
    31800, # Black Onna's Honor
    31140, # Black Pei Pei
    31210, # Black Perfect Stranger
    31940, # Black Spunky Do
    31660, # Black Tighty Bun
    31890, # Black Short Twin Tails
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("I'm a hair assistant in this shop. If you have #b#t5150052##k or #b#t5151035##k by any chance, then how about letting me change your hairdo?\r\n" + \
    "#L0##bHaircut (REG coupon)#k#l\r\n" + \
    "#L1##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use a regular coupon your hair style will change RANDOMLY. Do you still want to use #b#t5150052##k and change it up?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("If you use a regular coupon your hair color will change RANDOMLY. Do you still want to use #b#t5151035##k and change it up?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
