# Natalie : Hair Salon Owner (1012103)
#   Henesys : Henesys Hair Salon (100000104)

VIP_HAIR_M = [
    33040, # White Aran Cut
    30060, # Black Catalyst
    30210, # Black Shaggy Wax
    30140, # Black Topknot
    30200, # Black Wind
    33170, # Black Gaga Hair
    33100, # Black The Coco
]
VIP_HAIR_F = [
    31150, # Black Angelica
    34050, # White Aran Hair
    31300, # Black Chantelle
    31700, # Black Crazy Medusa
    31350, # Black Fourtail Braids
    31740, # Black Frizzle Dizzle
    34110, # Black Full Bangs
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_COLOR_COUPON_VIP = 5151036

answer = sm.askMenu("I'm the head of this hair salon Natalie. If you have a #b#t5150053##k or a #b#t5151036##k allow me to take care of your hairdo. Please choose the one you want.\r\n" + \
        "#L0##bHaircut (VIP coupon)#k#l\r\n" + \
        "#L1##bDye your hair (VIP coupon)#k#l"
)
if answer == 0:
    color = sm.getHair() % 10
    choices = [ hair + color for hair in (VIP_HAIR_M if sm.getGender() == 0 else VIP_HAIR_F) ]
    answer = sm.askAvatar("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? With #b#t5150053##k I'll change it for you. Choose the one to your liking~", choices)
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
    answer = sm.askAvatar("I can totally dye your hair and make it look so good. Why don't you change it up a bit? With #b#t5151036##k I'll change it for you. Choose the one to your liking~", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_COLOR_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("Check it out!! What do you think? Even I think this one is a work of art! AHAHAHA. Please let me know when you want to dye your hair again, because I'll make you look good each time!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
