# Roger's Apple

APPLE = 2010000
GREEN_APPLE = 2010009

if sm.getHp() < sm.getMaxHp():
    sm.sayNext("Hey, your HP is not fully recovered yet. Did you take all the Roger's Apple that I gave you? Are you sure?")
    sm.dispose()

sm.sayNext("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.")

sm.sayBoth("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!")

sm.sayBoth("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care of yourself and Good luck my friend!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i" + str(APPLE) + "# 3 Apple\r\n#i" + str(GREEN_APPLE) + "# 3 Green apple\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10 exp")

if not sm.addItems([(APPLE, 3), (GREEN_APPLE, 3)]):
    sm.sayNext("Please check if your inventory is full or not.")
    sm.dispose()

sm.addExp(10)
sm.forceCompleteQuest(questId)