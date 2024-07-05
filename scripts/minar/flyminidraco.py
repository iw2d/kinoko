# Corba : Retired Dragon Trainer (2082003)
#   Leafre : Station (240000110)

WAY_TO_TEMPLE_OF_TIME = 200090500
MINI_DRACO_TRANSFORMATION = 2210016

answer = sm.askMenu("If you had wings, I'm sure you could go there.  But, that alone won't be enough.  If you want to fly though the wind that's sharper than a blade, you'll need tough scales as well.  I'm the only Halfling left that knows the way back... If you want to go there, I can transform you.  No matter what you are, for this moment, you will become a #bDragon#k...\r\n" + \
        "#L0##bI want to become a dragon.#k#l"
)

if answer == 0:
    sm.setConsumeItemEffect(MINI_DRACO_TRANSFORMATION)
    sm.warp(WAY_TO_TEMPLE_OF_TIME, "sp")
