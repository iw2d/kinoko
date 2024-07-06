# Salon Seamus : Streaky Stylist (9201016)
#   Amoria : Amoria Hair Salon (680000002)

REG_HAIR_M = [
    30450, # Black Casanova
    30570, # Black Eternal Elegance
    30660, # Black Fuzz
    30910, # Black Jun Pyo Hair
    30050, # Black Metro
    30410, # Black Natural
    30510, # Black Rockie
    30300, # Black Romance
    30580, # Black Saturday Special
    30590, # Black Super Suave
    # Windy Hair?
]
REG_HAIR_F = [
    31150, # Black Angelica
    31590, # Black Ballroom Classic
    31310, # Black Carla
    31220, # Black Caspia
    31490, # Black Cecelia Twist
    31480, # Black Classy Sass
    31260, # Black Daisy Do
    31020, # Black Francesca
    31570, # Black Maiden's Weave
    31630, # Black The Honeybun
    31580, # Black Victorian Wrap
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("How's it going? I've got some new hair-do's to try out if you're game enough... what do you say? If you have a #b#t5150052##k or #b#t5151035##k, please let me change your hairdo...\r\n" + \
    "#L0##bHaircut (REG coupon)#k#l\r\n" + \
    "#L1##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150052##k and really change your hairstyle?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("Hey, here's the mirror. What do you think of your new haircut? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Are you going to use #b#t5151035##k and really change your hair color?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("Hey, here's the mirror. What do you think of your new hair color? I know it wasn't the smoothest of all, but didn't it come out pretty nice? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
