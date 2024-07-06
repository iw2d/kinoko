# Andre : Hair Salon Assistant (1052101)
#   Kerning City : Kerning City Hair Salon (103000005)

REG_HAIR_M = [
    30130, # Black Antagonist
    33040, # White Aran Cut
    30850, # Black Cornrow
    30780, # Black Dragon Tail
    33130, # Black Dual Blade Hair
    30520, # Black Hontas
    30770, # Black Lucky Charms
    30040, # Black Rockstar
    30920, # Black Short Top Tail
]
REG_HAIR_F = [
    31060, # Black Annie
    34050, # White Aran Hair
    31520, # Black Curly Stream
    31880, # Black Gardener
    31140, # Black Pei Pei
    31330, # Black Penelope
    31440, # Black Ravishing Raven
    31760, # Black Shaggy Dog
    31750, # Black Super Diva
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("I'm Andres, Don's assistant. Everyone calls me Andre, though. If you have #b#t5150052##k or #b#t5151035##k please let me change your hairdo...\r\n" + \
    "#L0##bHaircut (REG coupon)#k#l\r\n" + \
    "#L1##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150052##k and really change your hairstyle?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("Ok, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest, but it still looks pretty good! Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Are you going to use #b#t5151035##k and really change your hair color?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("Ok, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest, but it still looks pretty good! Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
