# Isa the Station Guide : Platform Usher (2012006)
#   Orbis : Orbis Station Entrance (200000100)

CHOICES = [
    ("Platform to Board a Ship to Victoria Island", 200000111),
    ("Platform to Board a Ship to Ludibrium", 200000121),
    ("Platform to Board a Ship to Leafre", 200000131),
    ("Platform to Ride a Crane to Mu Lung", 200000141),
    ("Platform to Ride a Genie to Ariant", 200000151),
    ("Platform to Board a Ship to Ereve", 200000161),
    ("Platform to Board a Ship to Edelstein", 200000170)
]

answer = sm.askMenu("There are many Platforms at the Orbis Station. You must find the correct Platform for your destination. Which Platform would you like to go to?\r\n" + \
        "\r\n".join("#L{}##b{}#k#l".format(i, t[0]) for i, t in enumerate(CHOICES))
)

if answer >= 0 and answer < len(CHOICES):
    if sm.askYesNo("Even if you took the wrong passage you can get back here using the portal, so no worries. Will you move to the #b{}#k?".format(CHOICES[answer][0])):
        sm.warp(CHOICES[answer][1], "west00")
