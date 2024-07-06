# Tepei : Hair Stylist (9120100)
#   Zipangu : Hair Salon (801000001)

VIP_HAIR_M = [
    30030, # Black Buzz
    33240, # Black Clean-Cut Short Hair
    30780, # Black Dragon Tail
    30810, # Black Gruff & Tough
    30820, # Black Matinee Idol
    30260, # Black Metrosexual
    30280, # Black Mohecan Shaggy Do
    30710, # Black Puffy Fro
    30920, # Black Short Top Tail
    30340, # Black Tristan
]
VIP_HAIR_F = [
    31550, # Black Candy Heart
    31850, # Black Dambi
    31350, # Black Fourtail Braids
    31460, # Black Lady Mariko
    31100, # Black Mary
    31030, # Black Polly
    31790, # Black Princessa
    31000, # Black Sammy
    31770, # Black Short Shaggy Hair
    34260, # Black Top Tied Hair
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_COLOR_COUPON_VIP = 5151036

answer = sm.askMenu("Welcome, welcome, welcome to the Showa Hair-Salon! Do you, by any chance, have #b#t5150053##k or #b#t5151036##k? If so, how about letting me take care of your hair? Please choose what you want to do with it.\r\n" + \
        "#L0##bChange hair style (VIP coupon)#k#l\r\n" + \
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

