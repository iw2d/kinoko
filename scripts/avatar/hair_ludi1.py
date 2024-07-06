# Miyu : Owner (2041007)
#   Ludibrium : Ludibrium Hair Salon (220000004)

VIP_HAIR_M = [
    30250, # Black Afro
    30190, # Bowl Cut
    30660, # Black Fuzz
    30870, # Black Hector Hair
    30990, # Black Tentacle Hair
    30160, # Black Trip Scratch
    30640, # Black Urban Dragon
]
VIP_HAIR_F = [
    31810, # Black Apple Hair
    31550, # Black Candy Heart
    31830, # Black Eye-skimming Bang
    31840, # Black Female Runway Hair
    31680, # Black Lovely Ladyhawk
    31290, # Black Naomi
    31270, # Black Pigtails
    31870, # Black Ayu
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_COLOR_COUPON_VIP = 5151036

answer = sm.askMenu("Welcome, welcome, welcome to the Ludibrium Hair-Salon! Do you, by any chance, have #b#t5150053##k or #b#t5151036##k? If so, how about letting me take care of your hair? Please choose what you want to do with it.\r\n" + \
        "#L0##bChange hair style (VIP coupon)#k#l\r\n" + \
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
