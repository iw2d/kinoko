# Rinz the Assistant : Assistant Hair Stylist (2012007)
#   Orbis Park : Orbis Hair Salon (200000202)

REG_HAIR_M = [
    30530, # Black Baldie
    33240, # Black Clean-Cut Short Hair
    30230, # Black Foil Perm
    30490, # Black Messy Spike
    30260, # Black Metrosexual
    30280, # Black Mohecan Shaggy Do
    30630, # Black Neon Cactus
    30740, # Black Receding Hair
    33050, # Black Spiky Shag
    30340, # Black Tristan
    33290, # Black Updo
]
REG_HAIR_F = [
    34060, # Black Bow Hair
    31220, # Black Caspia
    31650, # Black Dashing Damsel
    33160, # Black Lilin Hair
    31110, # Black Monica
    31710, # Black Princess Warrior
    31790, # Black Princessa
    31230, # Black Rose
    31890, # Black Short Twin Tails
    31630, # Black The Honeybun
    34260, # Black Top Tied Hair
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("I'm Rinz, the assistant. Do you have #b#t5150052##k or #b#t5151035##k with you? If so, what do you think about letting me take care of your hairdo? What do you want to do with your hair?\r\n" + \
    "#L0##bHaircut (REG coupon)#k#l\r\n" + \
    "#L1##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that you've never seen before. Do you want to use #b#t5150052##k and change your hair?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? If you ever feel like changing it up again later, please drop by.")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("If you use the REG coupon your hair color will change to a random new color. Are you sure you want to use #b#t5151035##k and randomly change your hair color?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? If you ever feel like changing it up again later, please drop by.")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
