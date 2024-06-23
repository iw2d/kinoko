# Portal to Hall of <job>

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