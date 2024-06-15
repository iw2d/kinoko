package kinoko.world.user.stat;

import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class DiceInfo implements Encodable {
    public static final DiceInfo DEFAULT = new DiceInfo();
    private final int[] infoArray = new int[22];

    public int[] getInfoArray() {
        return infoArray;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // aDiceInfo
        for (int i = 0; i < 22; i++) {
            outPacket.encodeInt(infoArray[i]);
        }
    }

    public static DiceInfo decode(InPacket inPacket) {
        final DiceInfo diceInfo = new DiceInfo();
        for (int i = 0; i < 22; i++) {
            diceInfo.infoArray[i] = inPacket.decodeInt();
        }
        return diceInfo;
    }

    public static DiceInfo from(int roll, SkillInfo si, int slv) {
        final DiceInfo diceInfo = new DiceInfo();
        switch (roll) {
            case 2 -> {
                // weapon defense
                diceInfo.infoArray[8] = si.getValue(SkillStat.pddR, slv);
            }
            case 3 -> {
                // max hp/mp
                diceInfo.infoArray[0] = si.getValue(SkillStat.mhpR, slv);
                diceInfo.infoArray[1] = si.getValue(SkillStat.mmpR, slv);
            }
            case 4 -> {
                // critical rate
                diceInfo.infoArray[2] = si.getValue(SkillStat.cr, slv);
            }
            case 5 -> {
                // damage
                diceInfo.infoArray[12] = si.getValue(SkillStat.damR, slv);
            }
            case 6 -> {
                // exp
                diceInfo.infoArray[17] = si.getValue(SkillStat.expR, slv);
            }
        }
        return diceInfo;
    }
}
