# Julius Styleman : Heavenly Hair-Bringer (9201015)
#   Amoria : Amoria Hair Salon (680000002)

VIP_HAIR_M = [
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
VIP_HAIR_F = [
    31150, # Black Angelica
    31590, # Black Ballroom Classic
    31310, # Black Carla
    31220, # Black Caspia
    31260, # Black Daisy Do
    31630, # Black The Honeybun
    31580, # Black Victorian Wrap
    31610, # Black Darling Diva
    31490, # Black Cecelia Twist
    31480, # Black Classy Sass
    31420, # Black Lana
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_COLOR_COUPON_VIP = 5151036

answer = sm.askMenu("Welcome! My name's Julius Styleman. If you have a #b#t5150053##k or a #b#t5151036##k allow me to take care of your hairdo. Please choose the one you want.\r\n" + \
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
