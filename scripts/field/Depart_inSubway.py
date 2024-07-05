# Kerning City Subway : Kerning Square Subway (103020010)
# Kerning City Subway : Kerning Square Subway (103020011)
# Kerning City Subway : Kerning Square Subway (103020012)

KERNING_SQUARE_SUBWAY_1 = 103020010
KERNING_SQUARE_SUBWAY_2 = 103020011

if sm.getFieldId() == KERNING_SQUARE_SUBWAY_1:
    sm.scriptProgressMessage("The next stop is at Kerning Square Station. The exit is to your left.")
elif sm.getFieldId() == KERNING_SQUARE_SUBWAY_2:
    sm.scriptProgressMessage("The next stop is at Kerning Subway Station. The exit is to your left.")
