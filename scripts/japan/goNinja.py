# Palanquin (9110107)
#   Zipangu : Mushroom Shrine (800000000)
#   Zipangu : Outside Ninja Castle (800040000)

MUSHROOM_SHRINE = 800000000
OUTSIDE_NINJA_CASTLE = 800040000

if sm.getFieldId() == MUSHROOM_SHRINE:
    sm.sayNext("We are... the palanquin... bearers! Need to... get to... Ninja Castle? Talk to us! Talk to us!")
    if sm.askYesNo("Huh, what? You want to go to Ninja Castle?"):
        sm.sayNext("Got it! We are... the palanquin.... bearers! We'll get you there faster than you can blink. And since we're in such a jolly mood, you don't even have to pay us!")
        sm.warp(OUTSIDE_NINJA_CASTLE, "sp")
elif sm.getFieldId() == OUTSIDE_NINJA_CASTLE:
    sm.sayNext("We are... the palanquin... bearers! Need to... get to... Ninja Castle? Talk to us! Talk to us!")
    if sm.askYesNo("Huh, what? You want to go to Mushroom Shrine?"):
        sm.sayNext("Got it! We are... the palanquin.... bearers! We'll get you there faster than you can blink. And since we're in such a jolly mood, you don't even have to pay us!")
        sm.warp(MUSHROOM_SHRINE, "sp")
