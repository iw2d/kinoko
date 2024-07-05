# Henesys : Bowman Instructional School (100000201)
#   rank00 (-323, 181)
# Ellinia : Magic Library (101000003)
#   rank00 (-2, 183)
# Perion : Warriors' Sanctuary (102000003)
#   rank00 (55, -29)
# Kerning City : Thieves' Hideout (103000003)
#   rank00 (291, 181)
# Nautilus : Navigation Room (120000101)
#   rank00 (-296, 149)
# Empress' Road : Ereve (130000000)
#   west00 (-1644, 86)
# Empress' Road : Crossroads of Ereve (130000200)
#   east00 (3303, 87)
# Snow Island : Dangerous Forest (140010100)
#   in01 (-2763, -375)

FIELDS = {
    100000201 : 100000204, # Bowman Instructional School -> Hall of Bowmen
    101000003 : 101000004, # Magic Library -> Hall of Magicians
    102000003 : 102000004, # Warriors' Sanctuary -> Hall of Warriors
    103000003 : 103000008, # Thieves' Hideout -> Hall of Thieves
    120000101 : 120000105  # Navigation Room -> Training Room
}


fieldId = sm.getFieldId()

if fieldId in FIELDS:
    sm.playPortalSE()
    sm.warp(FIELDS[fieldId], "out00")
else:
    sm.dispose()
