# Dimensional Mirror : Multi-Functional Portal (9010022)
#   Henesys : Henesys (100000000)
#   Ellinia : Ellinia (101000000)
#   Perion : Perion (102000000)
#   Kerning City : Kerning City (103000000)
#   Sleepywood : Sleepywood (105000000)
#   Dungeon : Sleepywood (105040300)
#   Nautilus : Nautilus Harbor (120000000)
#   Orbis : Orbis Park (200000200)
#   El Nath : El Nath (211000000)
#   Ludibrium : Ludibrium (220000000)
#   Omega Sector : Omega Sector (221000000)
#   Korean Folk Town : Korean Folk Town (222000000)
#   Aquarium : Aquarium (230000000)
#   Leafre : Leafre (240000000)
#   Mu Lung : Mu Lung (250000000)
#   Herb Town : Herb Town (251000000)
#   The Burning Road : Ariant (260000000)
#   Sunset Road : Magatia (261000000)
#   Black Wing Territory : Edelstein (310000000)
#   Singapore : CBD (540000000)
#   New Leaf City : NLC Town Center (600000000)
#   Zipangu : Mushroom Shrine (800000000)

QR_UnityPortal = 7050

AVAILABLE_FIELDS = [
    100000000, # Henesys
    101000000, # Ellinia
    102000000, # Perion
    103000000, # Kerning City
    105000000, # Sleepywood
    120000000, # Nautilus Harbor
    200000200, # Orbis Park
    211000000, # El Nath
    220000000, # Ludibrium
    221000000, # Omega Sector
    222000000, # Korean Folk Town
    230000000, # Aquarium
    240000000, # Leafre
    250000000, # Mu Lung
    251000000, # Herb Town
    260000000, # Ariant
    261000000, # Magatia
    310000000, # Edelstein
    540000000, # CBD
    600000000, # NLC Town Center
    800000000, # Mushroom Shrine
]

choices = []
choices.append("#0#Ariant Coliseum")
choices.append("#1#Mu Lung Dojo")
choices.append("#2#Monster Carnival 1")
choices.append("#3#Monster Carnival 2")
choices.append("#4#Sea of Fog")
choices.append("#5#Nett's Pyramid")
choices.append("#6#Dusty Platform")
# choices.append("#7#Happyville")
choices.append("#8#Golden Temple")
choices.append("#9#Moon Bunny")
choices.append("#10#First Time Together")
choices.append("#11#Dimensional Crack")
choices.append("#12#Forest of Poison Haze")
choices.append("#13#Remnants of the Goddess")
choices.append("#14#Lord Pirate")
choices.append("#15#Romeo and Juliet")
choices.append("#16#Resurrection of the Hoblin King")
choices.append("#17#Dragon's Nest")
# choices.append("#98#") # Astaroth?
# choices.append("#99#") # Leafre?

fieldId = sm.getFieldId()

if fieldId in AVAILABLE_FIELDS:
    answer = sm.askSlideMenu(0, "".join(choices))
    if answer == 0:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(980010000, "out00") # Ariant Coliseum : Battle Arena Lobby
    elif answer == 1:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(925020000, "out00") # Mu Lung Dojo : Mu Lung Dojo Entrance
    elif answer == 2:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(980000000, "out00") # Monster Carnival : Spiegelmann's Office
    elif answer == 3:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(980030000, "out00") # The 2nd Monster Carnival : Spiegelmann's Office
    elif answer == 4:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(923020000, "sp") # Sea of Fog : Shipwrecked Ghost Ship
    elif answer == 5:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(926010000, "out00") # Hidden Street : Pyramid Dunes
    elif answer == 6:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(910320000, "out00") # Hidden Street : Abandoned Subway Station
#     elif answer == 7:
#         sm.setQRValue(QR_UnityPortal, str(fieldId))
#         sm.warp(209000000, "st00") # Hidden Street : Happyville
    elif answer == 8:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(950100000, "out00") # Golden Temple : Golden Temple
    elif answer == 9:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(910010500, "out00") # Hidden Street : Moon Bunny Lobby
    elif answer == 10:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(910340700, "out00") # Hidden Street : First Time Together Lobby
    elif answer == 11:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(221023300, "out00") # Ludibrium : Eos Tower 101st Floor
    elif answer == 12:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(300030100, "west00") # Elin Forest : Deep Fairy Forest
    elif answer == 13:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(200080101, "out00") # Orbis : The Unknown Tower
    elif answer == 14:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(251010404, "out00") # Herb Town : Over the Pirate Ship
    elif answer == 15:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(261000021, "out00") # Magatia : Alcadno - Hidden Room
    elif answer == 16:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(211000002, "out00") # El Nath : Shammos's Solitary Room
    elif answer == 17:
        sm.setQRValue(QR_UnityPortal, str(fieldId))
        sm.warp(240080000, "left00") # Leafre : Crimson Sky Dock
