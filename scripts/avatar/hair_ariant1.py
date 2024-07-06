# Mazra : Hair Salon Director (2100006)
#   The Burning Road : Ariant (260000000)

VIP_HAIR_M = [
    30320, # Black Afro
    30330, # Black Cabana Boy
    30150, # Black Dreadlocks
    30900, # Black Kravitz Locks
    30170, # Black Line Scratch
    30180, # Black Mane
    30820, # Black Matinee Idol
    30410, # Black Natural
    30460, # Black Tornade Hair
]
VIP_HAIR_F = [
    31090, # Black Bridget
    31190, # Black Celeb
    31040, # Black Edgy
    31420, # Black Lana
    31330, # Black Penelope
    31340, # Black Rae
    31400, # Black Boyish
    31620, # Black Desert Flower
    31660, # Black Tighty Bun
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_COLOR_COUPON_VIP = 5151036

answer = sm.askMenu("Hahaha... it takes a lot of style and flair for someone to pay attention to his or her hairsyle in a desert. Someone like you...If you have a #b#t5150053##k or #b#t5151036##k, I'll give your hair a fresh new look.\r\n" + \
        "#L0##bChange hairstyle (VIP coupon)#k#l\r\n" + \
        "#L1##bDye your hair (VIP coupon)#k#l"
)
if answer == 0:
    color = sm.getHair() % 10
    choices = [ hair + color for hair in (VIP_HAIR_M if sm.getGender() == 0 else VIP_HAIR_F) ]
    answer = sm.askAvatar("Hahaha~ all you need is #b#t5150053##k to change up your hairstyle. Choose the new style, and let me do the rest.", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_STYLE_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("Hahaha, all done! Your new hairstyle is absolutely fabulous. May your stylish hair turn heads even in the desert.") # GPT
        else:
            sm.sayNext("I thought I told you, you need the coupon in order for me to work magic on your hair check again.")
elif answer == 1:
    hair = sm.getHair()
    hair = hair - (hair % 10)
    choices = [ hair + i for i in range(8) ]
    answer = sm.askAvatar("Every once in a while, it doesn't hurt to change up your hair color... it's fun. Allow me, the great Mazra, to dye your hair, so you just bring me #b#t5151036##k, and choose your new hair color.", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_COLOR_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("Hahaha, all done! Your new hair color is absolutely fabulous. May your stylish hair turn heads even in the desert.") # GPT
        else:
            sm.sayNext("I thought I told you, you need the coupon in order for me to work magic on your hair check again.")
