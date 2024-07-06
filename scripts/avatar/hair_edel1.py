# Fabio : Hair Stylist (2150003)
#   Edelstein : Edelstein Hair Salon (310000003)
#   Dry Road : Road to the Mine 1 (310040000)

VIP_HAIR_M = [
    30350, # Black Astro
    30480, # Black Babby Cut
    33190, # Black Battle Mage Hair
    30760, # Black Bowling Ball
    30330, # Black Cabana Boy
    30560, # Black Grand Lionman
    30040, # Black Rockstar
    30730, # Black Roving Rockstar
    30370, # Black Shaggy Dragon
    30470, # Black Slick Dean
    30460, # Black Tornade Hair
]
VIP_HAIR_F = [
    31310, # Black Carla
    31490, # Black Cecelia Twist
    31260, # Black Daisy Do
    31130, # Black Jolie
    31160, # Black Lori
    31510, # Black Minnie
    31230, # Black Rose
    31320, # Black Roxy
    31560, # Black Sunflower Power
    34190, # Black Wild Hunter Hair
    31530, # Black Zessica
]

HAIR_STYLE_COUPON_VIP = 5150053
HAIR_STYLE_COUPON_REG = 5150052

HAIR_COLOR_COUPON_VIP = 5151036
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("Beauty is something that you must pursue your entire life. I can give you a new hairstyle if you have a #bHair Style Coupon#k or a #bHair Color Coupon#k!\r\n" + \
        "#L0##bChange hairstyle (VIP coupon)#k#l\r\n" + \
        "#L1##bChange hairstyle (REG coupon)#k#l\r\n" + \
        "#L2##bDye your hair (VIP coupon)#k#l\r\n"
        "#L3##bDye your hair (REG coupon)#k#l"
)
if answer == 0:
    # HAIR_STYLE_COUPON_VIP
    color = sm.getHair() % 10
    choices = [ hair + color for hair in (VIP_HAIR_M if sm.getGender() == 0 else VIP_HAIR_F) ]
    answer = sm.askAvatar("All you need is a #b#t5150053##k and I can change the look of your hair. Please choose the hair style you would like. A new hair style can make all the difference!", choices) # GPT
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_STYLE_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("You might fall in love with your new hair style! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!") # GPT
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 1:
    # HAIR_STYLE_COUPON_REG
    if sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new hairstyle. Would you like to use #b#t5150052##k to change your hairstyle?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = VIP_HAIR_M if sm.getGender() == 0 else VIP_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("You might fall in love with your new hair style! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!") # GPT
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
elif answer == 2:
    # HAIR_COLOR_COUPON_VIP
    hair = sm.getHair()
    hair = hair - (hair % 10)
    choices = [ hair + i for i in range(8) ]
    answer = sm.askAvatar("Finding the right color to enhance your hair style is very important. If you have a #b#t5151036##k, please choose the color and I'll take care of the rest.", choices)
    if answer >= 0 and answer < len(choices):
        if sm.removeItem(HAIR_COLOR_COUPON_VIP, 1):
            sm.changeAvatar(choices[answer])
            sm.sayNext("You might fall in love with your new hair color! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!") # GPT
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
elif answer == 3:
    # HAIR_COLOR_COUPON_REG
    if sm.askYesNo("If you use the REG coupon your hair will change to a RANDOM new color. Would you like to use #b#t5151035##k to dye your hair?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("You might fall in love with your new hair color! Take a look, it suits you perfectly. Remember, you’re always welcome to come back anytime for another stylish transformation!") # GPT
        else:
            sm.sayNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
