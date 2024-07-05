# Shanks (22000)
#   Maple Road : Southperry (2000000)

LUCAS_S_RECOMMENDATION_LETTER = 4031801
GO_LITH = 2010000

if not sm.askYesNo("Take this ship and you'll head off to a bigger continent. For #e150 mesos#n, I'll take you to #bVictoria Island#k. The thing is, once you leave this place, you can't ever come back. What do you think? Do you want to go to Victoria Island?"):
    sm.sayOk("Hmm... I guess you still have things to do here?")
    sm.dispose()

if sm.getLevel() < 7:
    sm.sayOk("Let's see... I don't think you are strong enough. You'll have to be at least Level 7 to go to Victoria Island.")
    sm.dispose()

if sm.hasItem(LUCAS_S_RECOMMENDATION_LETTER):
    sm.sayNext("Okay, now give me 150 mesos... Hey, what's that? Is that the recommendation letter from Lucas, the chief of Amherst? Hey, you should have told me you had this. I, Shanks, recognize greatness when I see one, and since you have been recommended by Lucas, I see that you have a great, great potential as an adventurer. No way would I charge you for this trip!")
    sm.sayBoth("Since you have the recommendation letter, I won't charge you for this. Alright, buckle up, because we're going to head to Victoria Island right now, and it might get a bit turbulent!!")
    if sm.removeItem(LUCAS_S_RECOMMENDATION_LETTER, 1):
        sm.warp(GO_LITH)
else:
    sm.sayNext("Bored of this place? Here... Give me #e150 mesos#n first...")
    if sm.addMoney(-150):
        sm.sayNext("Awesome! #e150#n mesos accepted! Alright, off to Victoria Island!")
        sm.warp(GO_LITH)
    else:
        sm.sayOk("What? You're telling me you wanted to go without any money? You're one weirdo...")
