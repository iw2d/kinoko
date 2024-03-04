package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.skill.PassiveSkillData;

public final class DiceInfo implements Encodable {
    public static final int SIZE = 22;
    private final int[] diceInfo = new int[SIZE];

    public void updatePassiveSkillData(PassiveSkillData psd) {
        // CUserLocal::UpdatePassiveSkillData
        psd.mhpR += diceInfo[0];
        psd.mmpR += diceInfo[1];
        psd.cr += diceInfo[2];
        psd.cdMin += diceInfo[3]; // 4 not used
        psd.evaR += diceInfo[5];
        psd.ar += diceInfo[6];
        psd.er += diceInfo[7];
        psd.pddR += diceInfo[8];
        psd.mddR += diceInfo[9];
        psd.pdR += diceInfo[10];
        psd.mdR += diceInfo[11];
        psd.dipR += diceInfo[12];
        psd.pdamR += diceInfo[13];
        psd.mdamR += diceInfo[14];
        psd.padR += diceInfo[15];
        psd.madR += diceInfo[16];
        psd.expR += diceInfo[17];
        psd.impR += diceInfo[18];
        psd.asrR += diceInfo[19];
        psd.terR += diceInfo[20];
        psd.mesoR += diceInfo[21];
    }

    @Override
    public void encode(OutPacket outPacket) {
        // aDiceInfo
        for (int i = 0; i < SIZE; i++) {
            outPacket.encodeInt(diceInfo[i]);
        }
    }
}
