package kinoko.world.character;

import kinoko.server.OutPacket;
import kinoko.world.Encodable;

public final class CharacterStat implements Encodable {
    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(1); // dwCharacterID
        outPacket.encodeString("admin", 13); // sCharacterName
        outPacket.encodeByte(0); // nGender
        outPacket.encodeByte(0); // nSkin
        outPacket.encodeInt(0); // nFace
        outPacket.encodeInt(0); // nHair

        // aliPetLockerSN
        for (int i = 0; i < 3; i++) {
            outPacket.encodeLong(0);
        }

        outPacket.encodeByte(1); // nLevel
        outPacket.encodeShort(0); // nJob
        outPacket.encodeShort(0); // nSTR
        outPacket.encodeShort(0); // nDEX
        outPacket.encodeShort(0); // nINT
        outPacket.encodeShort(0); // nLUK
        outPacket.encodeInt(50); // nHP
        outPacket.encodeInt(50); // nMHP
        outPacket.encodeInt(5); // nMP
        outPacket.encodeInt(5); // nMMP
        outPacket.encodeShort(0); // nAP

        int job = 0;
        if (job / 1000 == 3 || job / 100 == 22 || job == 2001) {
            for (int i = 0; i < 10; i++) {
                outPacket.encodeByte(i); // nJobLevel
                outPacket.encodeByte(0); // nSP
            }
        } else {
            outPacket.encodeShort(0); // nSP
        }

        outPacket.encodeInt(0); // nEXP
        outPacket.encodeShort(0); // nEXP
        outPacket.encodeInt(0); // nTempEXP
        outPacket.encodeInt(100000000); // dwPosMap
        outPacket.encodeByte(0); // nPortal
        outPacket.encodeInt(0); // nPlayTime
        outPacket.encodeShort(0); // nSubJob
    }
}
