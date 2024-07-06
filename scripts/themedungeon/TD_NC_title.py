# Tera Forest   : Tera Forest Time Gate (240070000)
# Neo City : <Year 2021> Average Town Entrance (240070100)
# Neo City : <Year 2099> Midnight Harbor Entrance (240070200)
# Neo City : <Year 2215> Bombed City Center Retail District (240070300)
# Neo City : <Year 2216> Ruined City Intersection (240070400)
# Neo City : <Year 2230> Dangerous Tower Lobby (240070500)
# Neo City : <Year 2503> Air Battleship Bow (240070600)

fieldId = sm.getFieldId()

if fieldId == 240070000:
    sm.screenEffect("temaD/enter/teraForest")
elif fieldId == 240070100:
    sm.screenEffect("temaD/enter/neoCity1")
elif fieldId == 240070200:
    sm.screenEffect("temaD/enter/neoCity2")
elif fieldId == 240070300:
    sm.screenEffect("temaD/enter/neoCity3")
elif fieldId == 240070400:
    sm.screenEffect("temaD/enter/neoCity4")
elif fieldId == 240070500:
    sm.screenEffect("temaD/enter/neoCity5")
elif fieldId == 240070600:
    sm.screenEffect("temaD/enter/neoCity6")
