# Chief Tatamo (2081000)
#   Leafre : Leafre (240000000)

QR_Likeness = 7810

MAGIC_SEED = 4031346

def buyMagicSeed(count, price):
    if count == 0:
        sm.sayOk("I can't sell you 0.")
    else:
        total = count * price
        if sm.askYesNo("Buying #b{} Magic Seed(s)#k will cost you #b{:,} mesos#k. Are you sure you want to make the purchase?".format(count, total)):
            if sm.canAddItem(MAGIC_SEED, count) and sm.addMoney(-total):
                sm.addItem(MAGIC_SEED, count)
                sm.sayOk("See you again~")
            else:
                sm.sayOk("Please check and see if you have enough mesos to make the purchase. Also, I suggest you check the etc. inventory and see if you have enough space available to make the purchase.")
        else:
            sm.sayOk("Please think carefully. Once you have made your decision, let me know.")

def donateItem(itemId, inc):
    likeness = sm.getQRValue(QR_Likeness)
    if not likeness:
        sm.setQRValue(QR_Likeness, "000000")
        likeness = "000000"
    likeness = int(likeness)
    maxCount = sm.getItemCount(itemId)
    if maxCount > 0:
        count = sm.askNumber("How many #b#t{}##k's would you like to donate?\r\n#b< Owned : {} >#k".format(itemId, maxCount), 0, 0, maxCount)
        if count == 0:
            sm.sayOk("Think about it, and then let me know your decision.")
        elif count > 0 and sm.removeItem(itemId, count):
            likeness = likeness + (count * inc)
            if likeness > 800000:
                likeness = 800000
            sm.setQRValue(QR_Likeness, "{:06}".format(likeness))
            sm.sayOk("Thank you very much.")
        else:
            sm.sayOk("Please check and see if you have enough of the item.")
    else:
        sm.sayOk("I don't think you have the item.")


answer = sm.askMenu("...Can I help you?\r\n#b#L0# Buy the Magic Seed#l\r\n#L1# Do something for Leafre#l\r\n")
if answer == 0:
    # Buy the Magic Seed
    likeness = sm.getQRValue(QR_Likeness)
    if not likeness:
        sm.setQRValue(QR_Likeness, "000000")
        likeness = "000000"
    likeness = int(likeness)
    if likeness < 5000:
        if sm.askMenu("You don't seem to be from our town. How can I help you?\r\n#b#L0# I would like some Magic Seed.#l") == 0:
            count = sm.askNumber("#bMagic Seed#k is a precious item; I cannot give it to you just like that. How about doing me a little favor? Then I'll give it to you. I'll sell the #bMagic Seed#k to you for #b30,000 mesos#k each.\r\nAre you willing to make the purchase? How many would you like, then?", 0, 0, 100)
            buyMagicSeed(count, 30000)
    elif likeness >= 5000 and likeness < 24000:
        if sm.askMenu("Haven't we met before? No wonder you looked familiar. Hahaha...\r\nHow can I help you this time?\r\n#b#L0# I would like some Magic Seed.#l") == 0:
            sm.sayNext("Ahh~ now I remember. If I'm mistaken, I gave you some #bMagic Seed#k before. How was it? I'm guessing you are more than satisfied with your previous purchase based on the look on your face.")
            count = sm.askNumber("#bMagic Seed#k is a precious item; I cannot give it to you just like that. How about doing me a little favor? Then I'll give it to you. I'll sell the #bMagic Seed#k to you for #b27,000 mesos#k each.\r\nAre you willing to make the purchase? How many would you like, then?", 0, 0, 100)
            buyMagicSeed(count, 27000)
    elif likeness >= 24000 and likeness < 50000:
        if sm.askMenu("It's a beautiful day again today. Days like this should be spent out in the park on a picnic with your family. I have to admit, when I first met you, I had my reservations, what with you not being from this town and all ... but now, I feel more than comfortable doing business with you.\r\nHow can I help you this time?\r\n#b#L0# I would like some Magic Seed.#l") == 0:
            count = sm.askNumber("#bMagic Seed#k is a rare, precious item indeed, but now that we have been acquainted for quite some time, I'll give you a special discount. How about #b24,000 mesos#k for a #bMagic Seed#k? It's cheaper than flying over here through the ship! How many would you like?", 0, 0, 100)
            buyMagicSeed(count, 24000)
    elif likeness >= 50000 and likeness < 200000:
        if sm.askMenu("Hmmm ... It seems like Birk is crying out loud much louder than usual today. When Birk cries, it signals the fact that the egg of the baby dragon is ready to be hatched any minute now. Now that you have become part of the family here, I would like for you to personally witness the birth of the baby dragon when that time comes. \r\nDo you need something from me today?\r\n#b#L0# I would like some Magic Seed.#l") == 0:
            count = sm.askNumber("You must have run out of the #bMagic Seed#k. We have grown very close to one another, and it doesn't sound too good for me to ask you for something in return, but please understand that the #bMagic Seed#k is very rare and hard to come by. How about #b18,000 mesos#k for #b1 Magic Seed#k? How many would you like to get?", 0, 0, 100)
            buyMagicSeed(count, 18000)
    elif likeness >= 200000 and likeness < 800000:
        sm.sayNext("Ohh hoh. I had a feeling that you'd be coming here right about now ...\r\nanyway, a while ago, a huge war erupted at the Dragon Shrine, where the dragons reside. Did you hear anything about it?")
        if sm.askMenu("The sky shook, and the ground trembled as this incredibly loud thud covered every part of the forest. The baby dragons are now shivering in fear, wondering what may happen next. I wonder what actually happened... anyway, you're here for the seed, right?\r\n#b#L0# I would like some Magic Seed.#l") == 0:
            count = sm.askNumber("I knew it. I can now tell just by looking at your eyes. I know that you will always be there for us here. We both understand that the #bMagic Seed#k is a precious item, but for you, I'll sell it to you for #b12,000 mesos#k. How many would you like?", 0, 0, 100)
            buyMagicSeed(count, 12000)
    elif likeness >= 800000:
        if sm.askMenu("Aren't you here for the Magic Seed? A lot of time has passed since we first met, and now I feel a sense of calmness and relief whenever I talk to you. People in this town love you, and I think the same way about you. You're a true friend.\r\n#b#L0# Thank you so much for such kind words. I'd love to get some Magic Seeds right now.#l") == 0:
            count = sm.askNumber("You know I always have them ready. Just give me #b8,000 mesos#k per seed. We've been friends for a while, anyway. How many would you like?", 0, 0, 100)
            buyMagicSeed(count, 8000)
elif answer == 1:
    # Do something for Leafre
    likeness = sm.getQRValue(QR_Likeness)
    if not likeness:
        sm.setQRValue(QR_Likeness, "000000")
        likeness = "000000"
    likeness = int(likeness)
    if likeness < 5000:
        sm.sayNext("It is the chief's duty to make the town more hospitable for people to live in, and carrying out the duty will require lots of items. If you have collected items around Leafre, are you interested in donating them?")
    elif likeness >= 5000 and likeness < 24000:
        sm.sayNext("You're the person that graciously donated some great items to us before. I cannot tell you how helpful that really was. By the way, if you have collected items around Leafre, are you interested in donating them to us once more?")
    elif likeness >= 24000 and likeness < 50000:
        sm.sayNext("You came to see me again today. Thanks to your immense help, the quality of life in this town has been significantly upgraded. People in this town are very thankful of your contributions. By the way, if you have collected items around Leafre, are you interested in donating them to us once more?")
    elif likeness >= 50000 and likeness < 200000:
        sm.sayNext("Hey, there! Your tremendous contribution to this town has resulted in our town thriving like no other. The town is doing really well as it is, but I'd appreciate it if you can help us out again. If you have collected items around Leafre, are you interested in donating them to us once more?")
    elif likeness >= 200000 and likeness < 800000:
        sm.sayNext("It's you, the number 1 supporter of Leafre! Good things always seem to happen when you're in town. By the way, if you have collected items around Leafre, are you interested in donating them to us once more?")
    elif likeness >= 800000:
        sm.sayNext("Aren't you #b" + sm.getCharacterName() + "#k? It's great to see you again! Thanks to your incredible work, our town is doing so well that I really don't have much to do these days. Everyone in this town seems to look up to you, and I mean that. I thoroughly appreciate your great help, but ... can you help us out once more? If you have collected items around Leafre, then would you be again interested in donating the items to us?")

    answer = sm.askMenu("Which item would you like to donate?\r\n#b#L0# #t4000226##l\r\n#L1# #t4000229##l\r\n#L2# #t4000236##l\r\n#L3# #t4000237##l\r\n#L4# #t4000260##l\r\n#L5# #t4000261##l\r\n#L6# #t4000231##l\r\n#L7# #t4000238##l\r\n#L8# #t4000239##l\r\n#L9# #t4000241##l\r\n#L10# #t4000242##l\r\n#L11# #t4000234##l\r\n#L12# #t4000232##l\r\n#L13# #t4000233##l\r\n#L14# #t4000235#\r\n#L15# #t4000243##l")
    if answer == 0:
        donateItem(4000226, 2) # Rash's Furball
    elif answer == 1:
        donateItem(4000229, 4) # Dark Rash's Furball
    elif answer == 2:
        donateItem(4000236, 3) # Beetle's Horn
    elif answer == 3:
        donateItem(4000237, 6) # Dual Beetle's Horn
    elif answer == 4:
        donateItem(4000260, 3) # Hov's Shorts
    elif answer == 5:
        donateItem(4000261, 6) # Pin Hov's Charm
    elif answer == 6:
        donateItem(4000231, 7) # Hankie's Pan Flute
    elif answer == 7:
        donateItem(4000238, 9) # Harp's Tail Feather
    elif answer == 8:
        donateItem(4000239, 12) # Blood Harp's Crown
    elif answer == 9:
        donateItem(4000241, 15) # Birk's Chewed Grass
    elif answer == 10:
        donateItem(4000242, 20) # Dual Birk's Tiny Tail
    elif answer == 11:
        donateItem(4000234, 20) # Kentaurus's Skull
    elif answer == 12:
        donateItem(4000232, 20) # Kentaurus's Flame
    elif answer == 13:
        donateItem(4000233, 20) # Kentaurus's Marrow
    elif answer == 14:
        donateItem(4000235, 100) # Manon's Tail
    elif answer == 15:
        donateItem(4000243, 100) # Griffey Horn
