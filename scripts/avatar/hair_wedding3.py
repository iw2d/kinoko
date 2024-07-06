# Claudia (9201039)
#   Amoria : Amoria Hair Salon (680000002)

EXP_HAIR_M = [
    30270, # Black w/ Bald Spot
    30240, # Black Monkey
    30020, # Black Rebel
    30000, # Black Toben
    30132, # Orange Antagonist
    30192, # Orange Bowl Cut
    30032, # Orange Buzz
    30112, # Orange Fireball
    30162, # Orange Trip Scratch
]
EXP_HAIR_F = [
    31150, # Black Angelica
    31250, # Black Bowlcut
    31310, # Black Carla
    31050, # Black Connie
    31030, # Black Polly
    31070, # Black Stella
    31091, # Red Bridget
    31001, # Red Sammy
]

AMORIA_BEAUTY_OR_BEAST = 8860
CLAUDIAS_COUPON_EXP = 4031528

if sm.hasQuestCompleted(AMORIA_BEAUTY_OR_BEAST)  and not sm.hasItem(CLAUDIAS_COUPON_EXP, 1):
    sm.sayNext("I've already done your hair once as a trade-for-services, sport. You'll have to snag an EXP Hair coupon from the Cash Shop if you want to change it again!")
else:
    if sm.askYesNo("Ready for an awesome hairdo? I think you are! Just say the word, and we'll get started!"):
        if sm.removeItem(CLAUDIAS_COUPON_EXP, 1):
            choices = EXP_HAIR_M if sm.getGender() == 0 else EXP_HAIR_F
            hair = choices[sm.getRandom(0, len(choices) - 1)]
            sm.changeAvatar(hair)
            sm.sayNext("Here we go!")
            sm.sayBoth("Not bad, if I do say so myself! I knew those books I studied would come in handy...")
        else:
            sm.sayNext("Hmmm...are you sure you have our designated free coupon? Sorry but no haircut without it.")
    else:
        sm.sayNext("Ok, I'll give you a minute.")
