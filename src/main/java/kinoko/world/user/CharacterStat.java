package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import lombok.Data;

@Data
public final class CharacterStat implements Encodable {
    private final CharacterData characterData;
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
    private ExtendSP extendSP;
    private int exp;
    private short pop;
    private int money;
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
        getExtendSP().encode(outPacket);

        outPacket.encodeInt(getExp()); // nEXP
        outPacket.encodeShort(getPop()); // nPOP
        outPacket.encodeInt(0); // nTempEXP
        outPacket.encodeInt(getPosMap()); // dwPosMap
        outPacket.encodeByte(getPortal()); // nPortal
        outPacket.encodeInt(0); // nPlayTime
        outPacket.encodeShort(getSubJob()); // nSubJob
    }

    public static CharacterStat getDefault(CharacterData cd, String name, int[] selectedAL, byte gender) {
        final CharacterStat cs = new CharacterStat(cd);
        cs.setLevel((byte) 1);
        cs.setGender(gender);
        cs.setSkin((byte) selectedAL[3]);
        cs.setFace(selectedAL[0]);
        cs.setHair(selectedAL[1] + selectedAL[2]);

        cs.setBaseStr((short) 12);
        cs.setBaseDex((short) 5);
        cs.setBaseInt((short) 4);
        cs.setBaseLuk((short) 4);

        cs.setHp(50);
        cs.setMaxHp(50);
        cs.setMp(5);
        cs.setMaxMp(5);
        cs.setAp((short) 0);
        cs.setExtendSP(ExtendSP.getDefault(cs));

        cs.setExp(0);
        cs.setPop((short) 0);
        return cs;
    }
}
