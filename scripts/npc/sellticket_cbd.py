# Shalon : Ticketing Usher (9270038)

BEFORE_DEPARTURE_TO_KERNING_CITY = 540010001
TICKET_TO_KERNING_CITY = 4031732


answer = sm.askMenu("Hello there~ I am Shalon from Singapore Airport. How can I help you?\r\n" + \
        "#L0##bI would like to buy a plane ticket to Kerning City#k#l\r\n" + \
        "#L1##bLet me go in to the departure point#k#l"
)
if answer == 0:
    if sm.askYesNo("The ticket will cost you 5,000 mesos. Will you purchase the ticket?"):
        if sm.canAddItem(TICKET_TO_KERNING_CITY, 1):
            if sm.addMoney(-5000):
                sm.addItem(TICKET_TO_KERNING_CITY, 1)
                sm.sayOk("Thank you for choosing Wizet Airline! Enjoy your flight!")
            else:
                sm.sayOk("You don't have enough mesos.")
        else:
            sm.sayOk("Please check if your inventory is full or not.")
elif answer == 1:
    if sm.askYesNo("Would you like to go in now? You will lose your ticket once you go in~ Thank you for choosing Wizet Airline."):
        if sm.hasItem(TICKET_TO_KERNING_CITY):
            if sm.getEventState("CM_AIRPORT") == "AIRPORT_BOARDING":
                sm.removeItem(TICKET_TO_KERNING_CITY, 1)
                sm.warp(BEFORE_DEPARTURE_TO_KERNING_CITY, "sp")
            else:
                sm.sayOk("Sorry, the plane has already taken off. Please wait a few minutes.")
        else:
            sm.sayOk("You need a #b#t{}##k to get on the plane!".format(TICKET_TO_KERNING_CITY))