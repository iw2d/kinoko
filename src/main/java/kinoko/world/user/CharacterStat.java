package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import lombok.Data;

@Data
public final class CharacterStat implements Encodable {
    private CharacterData characterData;
    private byte gender;
    private byte skin;
    private int face;
    private int hair;
    private byte level;
    private short job;
    private short subJob;
    private short baseStr;
    private short baseDex;
    private short baseInt;
    private short baseLuk;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private short ap;
    private ExtendSP sp;
    private int exp;
    private short pop;
    private int posMap;
    private byte portal;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getCharacterData().getId()); // dwCharacterID
        outPacket.encodeString(getCharacterData().getName(), 13); // sCharacterName
        outPacket.encodeByte(getGender()); // nGender
        outPacket.encodeByte(getSkin()); // nSkin
        outPacket.encodeInt(getFace()); // nFace
        outPacket.encodeInt(getHair()); // nHair

        // aliPetLockerSN
        for (int i = 0; i < 3; i++) {
            outPacket.encodeLong(0);
        }

        outPacket.encodeByte(getLevel()); // nLevel
        outPacket.encodeShort(getJob()); // nJob
        outPacket.encodeShort(getBaseStr()); // nSTR
        outPacket.encodeShort(getBaseDex()); // nDEX
        outPacket.encodeShort(getBaseInt()); // nINT
        outPacket.encodeShort(getBaseLuk()); // nLUK
        outPacket.encodeInt(getHp()); // nHP
        outPacket.encodeInt(getMaxHp()); // nMHP
        outPacket.encodeInt(getMp()); // nMP
        outPacket.encodeInt(getMaxMp()); // nMMP
        outPacket.encodeShort(getAp()); // nAP
        getSp().encode(outPacket);

        outPacket.encodeInt(getExp()); // nEXP
        outPacket.encodeShort(getPop()); // nPOP
        outPacket.encodeInt(0); // nTempEXP
        outPacket.encodeInt(getPosMap()); // dwPosMap
        outPacket.encodeByte(getPortal()); // nPortal
        outPacket.encodeInt(0); // nPlayTime
        outPacket.encodeShort(getSubJob()); // nSubJob
    }
}
