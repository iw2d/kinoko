# Midori : Assistant Hair Stylist (9120101)
#   Zipangu : Hair Salon (801000001)

REG_HAIR_M = [
    33240, # Black Clean-Cut Short Hair
    30780, # Black Dragon Tail
    30800, # Black Dreamcatcher
    30810, # Black Gruff & Tough
    30790, # Black Lion Hair
    30820, # Black Matinee Idol
    30260, # Black Metrosexual
    30280, # Black Mohecan Shaggy Do
    30710, # Black Puffy Fro
    30920, # Black Short Top Tail
    30360, # Black Spiky Tail
    30340, # Black Tristan
]
REG_HAIR_F = [
    31550, # Black Candy Heart
    31850, # Black Dambi
    31350, # Black Fourtail Braids
    31540, # Black Jean
    31800, # Black Onna's Honor
    31410, # Black Paula
    31710, # Black Princess Warrior
    31790, # Black Princessa
    31770, # Black Short Shaggy Hair
    31720, # Black Streaky Siren
    34260, # Black Top Tied Hair
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("Hi, I'm the assistant here. If you have #b#t5150052##k or #b#t5151035##k, please allow me to change your hairdo.\r\n" + \
    "#L0##bChange hair-style (REG coupon)#k#l\r\n" + \
    "#L1##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new hairstyle. Would you like to use #b#t5150052##k to change your hairstyle?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("Now, here's the mirror. What do you think of your new haircut? Doesn't it look nice for a job done by an assistant? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    if sm.askYesNo("If you use the regular coupon, your hair color will be changed into a random new look. Are you sure you want to use #b#t5151035##k and change it?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("Now, here's the mirror. What do you think of your new hair color? Doesn't it look nice for a job done by an assistant? Come back later when you need to change it up again!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
