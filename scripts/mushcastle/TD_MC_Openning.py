# null (106020001)

QR_Opening = 2311

if sm.getQRValue(QR_Opening) != "1":
    sm.setQRValue(QR_Opening, "1")

sm.setDirectionMode(True, 0)
sm.reservedEffect("Effect/Direction2.img/open/back0")
sm.reservedEffect("Effect/Direction2.img/open/back1")
sm.reservedEffect("Effect/Direction2.img/open/light")
sm.reservedEffect("Effect/Direction2.img/open/pepeKing")
sm.reservedEffect("Effect/Direction2.img/open/line")
sm.reservedEffect("Effect/Direction2.img/open/violeta0")
sm.reservedEffect("Effect/Direction2.img/open/violeta1")
sm.reservedEffect("Effect/Direction2.img/open/frame")
sm.reservedEffect("Effect/Direction2.img/open/chat")
sm.reservedEffect("Effect/Direction2.img/open/out")
sm.setDirectionMode(False, 13000)
