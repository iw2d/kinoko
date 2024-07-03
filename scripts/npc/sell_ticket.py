# ContiMove - station attendant

STATION_TO_ORBIS = 104020110
ORBIS_STATION_ENTRANCE = 200000100
LUDIBRIUM_TICKETING_PLACE = 220000100
LEAFRE_STATION_ENTRANCE = 240000100
ARIANT_STATION_PLATFORM = 260000100


if sm.getFieldId() == STATION_TO_ORBIS:
    sm.sayNext("Pleased to meet you. I'm Joel, the station attendant. Want to leave Victoria Island and go to another area? At our station, we have an #bairship#k bound for #bOrbis Station#k, on the continent of Ossyria, leaving #bevery 15 minutes on the hour#k.")
    sm.sayBoth("If you are thinking of going to Orbis, please go talk to #bCherry#k on the right.")
    sm.sayBoth("Well, the truth is, we charged for these flights until very recently, but the alchemists of Magatia made a crucial discovery on the fuel that dramatically cuts down the amount of Mana used for the flight, so these flight are now free. Don't worry, we still get paid. Now we just get paid through the government.")
elif sm.getFieldId() == ORBIS_STATION_ENTRANCE:
    answer = sm.askMenu("I can guide you to the right ship to reach your destination. Where are you headed?\r\n" + \
            "#L0##bVictoria Island#k#l\r\n" + \
            "#L1##bLudibrium Castle#k#l\r\n" + \
            "#L2##bLeafre#k#l\r\n" + \
            "#L3##bMu Lung#k#l\r\n" + \
            "#L4##bAriant#k#l\r\n" + \
            "#L5##bEreve#k#l\r\n" + \
            "#L6##bEdelstein#k#l"
    )
    if answer == 0:
        sm.sayNext("You're headed to Victoria Island? Oh, it's a beautiful island with a variety of villages. The ship to Victoria Island #bleaves every 15 minutes on the hour#k.")
        sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Airship to Victoria. If anyone can show you the way, it's Isa.")
    elif answer == 1:
        sm.sayNext("You're headed to Ludibrium Castle at Ludus Lake? It's such a fun village made of toys. The ship to Ludibrium #bleaves every 10 minutes on the hour#k.")
        sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Airship to Ludibrium. If anyone can show you the way, it's Isa.")
    elif answer == 2:
        sm.sayNext("You're headed to Leafre in Minar Forest? I love that quaint little village of Halflingers. The ship to Leafre #bleaves every 10 minutes on the hour#k.")
        sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Airship to Leafre. If anyone can show you the way, it's Isa.")
    elif answer == 3:
        sm.sayNext("Are you heading towards Mu Lung in the Mu Lung temple? I'm sorry, but there's no ship that flies from Orbis to Mu Lung. There is another way to get there, though. There's a #bCrane that runs a cab service for 1 that's always available#k, so you'll get there as soon as you wish.")
        sm.sayBoth("Unlike the other ships that fly for free, however, this cab requires a set fee. This personalized flight to Mu Lung will cost you #b1,500 mesos#k, so please have to fee ready before riding the Crane.")
        sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Crane to Mu Lung. If anyone can show you the way, it's Isa.")
    elif answer == 4:
        sm.sayNext("You're headed to Ariant in the Nihal Desert? The people living there have a passion as hot as the desert. The ship to Ariant #bleaves every 10 minutes on the hour#k.")
        sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Genie to Ariant. If anyone can show you the way, it's Isa.")
    elif answer == 5:
        sm.sayNext("Are you heading towards Ereve? It's a beautiful island blessed with the presence of the Shinsoo the Holy Beast and Empress Cygnus. #bThe boat is for 1 person and it's always readily available#k so you can travel to Ereve fast.")
        sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the ship to Ereve. If anyone can show you the way, it's Isa.")
    elif answer == 6:
        sm.sayNext("Are you going to Edelstein? The brave people who live there constantly fight the influence of dangerous monsters. #b1-person Airship to Edelstein is always on standby#k, so you can use it at any time.")
        sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the ship to Edelstein. If anyone can show you the way, it's Isa.")
elif sm.getFieldId() == LUDIBRIUM_TICKETING_PLACE:
    sm.sayNext("Pleased to meet you. I'm Mel, the station attendant. Are you ready to leave Ludibrium and go to another area? At our station, we have an #bairship#k bound for #bOrbis Station#k, on the continent of Ossyria, leaving #bevery 10 minutes on the hour#k.")
    sm.sayBoth("If you are planning on heading to Orbis, please use the portal on the right and head to the station, then talk to #bTian#k.")
    sm.sayBoth("Sigh... Free flights to everywhere... I don't understand what got the alchemists of Magatia to come up with something like this. This is making our job that much harder, because there are so many more passengers now. Sigh...")
elif sm.getFieldId() == LEAFRE_STATION_ENTRANCE:
    sm.sayNext("Pleased to meet you. I'm Mu, the station attendant. Would you like to leave Leafre and go to another area? At our station, we have an #bairship#k bound for #bOrbis Station#k, on the continent of Ossyria, leaving #bevery 10 minutes on the hour#k.")
    sm.sayBoth("If you're going to Orbis, use the portal on the right and head to the station, then talk to #bTommie#k. Ah, don't be surprised when you see him. We keep being mistaken for being twins, but Tommie's actually my third oldest brother.")
    sm.sayBoth("Oh, and this is just between you and me... at the highest point of the station, you'll find a strange old man named Corba. Apparently, he possesses the mystic power to transform people into flying dragons. It has been said that once transformed into a flying dragon, you can fly to the mysterious floating island. Surely adventurers of level 100 and up will be intrigued...")
elif sm.getFieldId() == ARIANT_STATION_PLATFORM:
    sm.sayNext("Hey. I'm the station attendant, Syras. You wanna leave Ariant and go to another area? Here at our station we have a #bgenie#k that's headed to #bOrbis Station#k, on the continent of Ossyria, #bleaving every 10 minutes on the hour#k.")
    sm.sayBoth("If you're going to Orbis, talk to that old man on the right, #bAsesson#k. He has a hard time hearing, so you may want to yell at him to get his attention.")
    sm.sayBoth("Oh, and in case you're not aware of this, somewhere in the desert, a mysterious-looking man called Karcasa sends people to Victoria Island for a fee. I hope you understand that it's against the law to fly these innocent people to other towns without permit!!")
    sm.sayPrev("The Camel Cab, however, is permitted by the king so you can use that. Well, that cab will only take you up to Magatia, but it's still legal.")