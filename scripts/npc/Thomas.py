# Thomas Swift : Amoria Ambassador (9201022)

HENESYS = 100000000
AMORIA = 680000000

if sm.getFieldId() == HENESYS:
    if sm.askYesNo("I can take you to Amoria Village. Are you ready to go?"):
        sm.sayNext("I hope you had a great time! See you around!")
        sm.warp(AMORIA)
    else:
        sm.sayNext("Ok, feel free to hang around until you're ready to go!")
elif sm.getFieldId() == AMORIA:
    if sm.askYesNo("I can take you back to your original location. Are you ready to go?"):
        sm.sayNext("I hope you had a great time! See you around!")
        sm.warp(HENESYS)
    else:
        sm.sayNext("Ok, feel free to hang around until you're ready to go!")
