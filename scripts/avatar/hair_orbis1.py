# Mino the Owner : Lead Hair Stylist (2010001)
#   Orbis Park : Orbis Hair Salon (200000202)

VIP_HAIR_M = [
    33240, # Black Clean-Cut Short Hair
    30230, # Black Foil Perm
    30490, # Black Messy Spike
    30260, # Black Metrosexual
    30280, # Black Mohecan Shaggy Do
    33050, # Black Spiky Shag
    30340, # Black Tristan
]
VIP_HAIR_F = [
    34060, # Black Bow Hair
    31220, # Black Caspia
    31110, # Black Monica
    31790, # Black Princessa
    31230, # Black Rose
    31630, # Black The Honeybun
    34260, # Black Top Tied Hair
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_COLOR_COUPON_VIP = 5151036

answer = sm.askMenu("Hello I'm Mino the Owner. If you have either #b#t5150053##k or #b#t5151036##k, then please let me take care of your hair. Choose what you want to do with it.\r\n" + \
        "#L0##bHaircut (VIP coupon)#k#l\r\n" + \
        "#L1##bDye your hair (VIP coupon)#k#l"
)
if answer == 0:
    color = sm.getHair() % 10
    choices = [ hair + color for hair in (VIP_HAIR_M if sm.getGender() == 0 else VIP_HAIR_F) ]
    answer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5150053##k, I'll take care of the rest for you.", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_STYLE_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to change your hairstyle again, because I'll make you look good each time!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    hair = sm.getHair()
    hair = hair - (hair % 10)
    choices = [ hair + i for i in range(8) ]
    answer = sm.askAvatar("I can completely change the look of your hair. Aren't you ready for a change? With #b#t5151036##k, I'll take care of the rest for you.", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_COLOR_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to dye your hair again, because I'll make you look good each time!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
