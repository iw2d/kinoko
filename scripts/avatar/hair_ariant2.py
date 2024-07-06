# Shati : Hair Salon Assistant (2100005)
#   The Burning Road : Ariant (260000000)

REG_HAIR_M = [
    30320, # Black Afro
    30330, # Black Cabana Boy
    30150, # Black Dreadlocks
    30800, # Black Dreamcatcher
    30680, # Black Hobo
    30900, # Black Kravitz Locks
    30170, # Black Line Scratch
    30180, # Black Mane
    30820, # Black Matinee Idol
    30410, # Black Natural
    # Bald Head?
    30460, # Black Tornade Hair
]
REG_HAIR_F = [
    31400, # Black Boyish
    31090, # Black Bridget
    31190, # Black Celeb
    31520, # Black Curly Stream
    31650, # Black Dashing Damsel
    31620, # Black Desert Flower
    31420, # Black Lana
    31780, # Black Oh So Windy
    34000, # Black Palm Tree Hair
    31330, # Black Penelope
    31340, # Black Rae
    31660, # Black Tighty Bun
]

HAIR_STYLE_COUPON_REG = 5150052
HAIR_COLOR_COUPON_REG = 5151035

answer = sm.askMenu("Hey there! I'm Shati, and I', Mazra's apprentice. If you have #b#t5150052##k or #b#t5151035##k with you, how about allowing me to work on your hair?\r\n" + \
    "#L0##bChange Hairstyle (REG coupon)#k#l\r\n" + \
    "#L1##bDye Hair (REG coupon)#k#l"
)
if answer == 0:
    if sm.askYesNo("If you use the REG coupon, your hairstyle will be changed to a random new look. You'll also have access to new hairstyles I worked on that's not available for VIP coupons. Would you like to use #b#t5150052##k for a fabulous new look?"):
        if sm.removeItem(HAIR_STYLE_COUPON_REG, 1):
            choices = REG_HAIR_M if sm.getGender() == 0 else REG_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)] + (sm.getHair() % 10)
            sm.changeAvatar(hair)
            sm.sayNext("The reason my hairstyle looks like this is because I've experimented different styles on myself. Good thing I did that. Yours came out awesome!")
        else:
            sm.sayNext("I can only change your hairstyle if you bring me the coupon. You didn't forget that, did you?")
elif answer == 1:
    if sm.askYesNo("If you use the REG coupon your hair color will change to a random new color. Are you sure you want to use #b#t5151035##k and randomly change your hair color?"):
        if sm.removeItem(HAIR_COLOR_COUPON_REG, 1):
            hair = sm.getHair() - (sm.getHair() % 10)
            choices = [ hair + i for i in range(8) ]
            sm.changeAvatar(choices[sm.getRandom(0, len(choices) - 1)])
            sm.sayNext("The reason my hairstyle looks like this is because I've experimented different styles on myself. Good thing I did that. Yours came out awesome!")
        else:
            sm.sayNext("I can only change your hairstyle if you bring me the coupon. You didn't forget that, did you?")
