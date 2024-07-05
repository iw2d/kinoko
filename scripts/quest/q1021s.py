# Roger's Apple (1021)

ROGERS_APPLE = 2010007

sm.sayNext("Hey, " + ("Man" if sm.getGender() == 0 else "Miss") + "~ What's up? Haha! I am Roger who teaches you new travellers with lots of information.")

sm.sayBoth("You are asking who made me do this? Ahahahaha! Myself! I wanted to do this and just be kind to you new travellers.")

if not sm.askAccept("So..... Let me just do this for fun! Abaracadabra~!"):
    sm.dispose()

sm.setHp(25)

sm.sayNext("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you  #rRoger's Apple#k. Please take it. You will feel stronger. Open the item window and double click to consume. Hey, It's very simple to open the item window. Just press #bI#k on your keyboard.")

sm.sayBoth("Please take all Roger's Apples that I gave you. You will be able to see the HP bar increasing right away. Please talk to me again when you recover your HP 100%.")

if not sm.hasItem(ROGERS_APPLE) and not sm.addItem(ROGERS_APPLE, 1):
    sm.sayNext("Please check if your inventory is full or not.")
    sm.dispose()

sm.forceStartQuest(questId)
sm.avatarOriented("UI/tutorial.img/28")
