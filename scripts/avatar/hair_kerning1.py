# Don Giovanni : Hair Salon Owner (1052100)
#   Kerning City : Kerning City Hair Salon (103000005)

VIP_HAIR_M = [
    30130, # Black Antagonist
    33040, # White Aran Cut
    30850, # Black Cornrow
    30780, # Black Dragon Tail
    30040, # Black Rockstar
    30920, # Black Short Top Tail
    30860, # Black Male Runway Hair
]
VIP_HAIR_F = [
    34050, # White Aran Hair
    31090, # Black Bridget
    31880, # Black Gardener
    31140, # Black Pei Pei
    31330, # Black Penelope
    31760, # Black Shaggy Dog
    31440, # Black Ravishing Raven
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_COLOR_COUPON_VIP = 5151036

answer = sm.askMenu("Hello! I'm Don Giovanni, head of the beauty salon! If you have either #b#t5150053##k or #b#t5151036##k, why don't you let me take care of the rest? Decide what you want to do with your hair....\r\n" + \
        "#L0##bChange hairstyle (VIP coupon)#k#l\r\n" + \
        "#L1##bDye your hair (VIP coupon)#k#l"
)
if answer == 0:
    color = sm.getHair() % 10
    choices = [ hair + color for hair in (VIP_HAIR_M if sm.getGender() == 0 else VIP_HAIR_F) ]
    answer = sm.askAvatar("I can change your hairstyle to something totally new. Aren't you sick of your hairdo? I'll give you a haircut with #b#t5150053##k. Choose the hairstyle of your liking.", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_STYLE_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("Ok, check out your new haircut. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want another haircut. I'll take care of the rest!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    hair = sm.getHair()
    hair = hair - (hair % 10)
    choices = [ hair + i for i in range(8) ]
    answer = sm.askAvatar("I can change the color of your hair to something totally new. Aren't you sick of your hair-color? I'll dye your hair if you have #b#t5151036##k. Choose the color of your liking.", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_COLOR_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("Ok, check out your new hair color. What do you think? Even I admit this one is a masterpiece! AHAHAHAHA. Let me know when you want do dye your hair again. I'll take care of the rest!")
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
