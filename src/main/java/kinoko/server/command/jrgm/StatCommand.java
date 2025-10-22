package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.ServerConfig;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.job.JobConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;

import java.util.EnumMap;
import java.util.Map;

/**
 * JrGM command to set individual stats: HP, MP, STR, DEX, INT, LUK, AP, SP.
 */
public final class StatCommand {

    @Command("stat")
    @Arguments({ "hp/mp/str/dex/int/luk/ap/sp", "new value" })
    public static void stat(User user, String[] args) {
        try {
            String stat = args[1].toLowerCase();
            int value = Integer.parseInt(args[2]);

            CharacterStat cs = user.getCharacterStat();
            Map<Stat, Object> statMap = new EnumMap<>(Stat.class);

            switch (stat) {
                case "hp" -> {
                    cs.setMaxHp(value);
                    statMap.put(Stat.HP, cs.getMaxHp());
                }
                case "mp" -> {
                    cs.setMaxMp(value);
                    statMap.put(Stat.MP, cs.getMaxMp());
                }
                case "str" -> {
                    cs.setBaseStr((short) value);
                    statMap.put(Stat.STR, cs.getBaseStr());
                }
                case "dex" -> {
                    cs.setBaseDex((short) value);
                    statMap.put(Stat.DEX, cs.getBaseDex());
                }
                case "int" -> {
                    cs.setBaseInt((short) value);
                    statMap.put(Stat.INT, cs.getBaseInt());
                }
                case "luk" -> {
                    cs.setBaseLuk((short) value);
                    statMap.put(Stat.LUK, cs.getBaseLuk());
                }
                case "ap" -> {
                    cs.setAp((short) value);
                    statMap.put(Stat.AP, cs.getAp());
                }
                case "sp" -> {
                    if (JobConstants.isExtendSpJob(cs.getJob())) {
                        cs.getSp().setSp(JobConstants.getJobLevel(cs.getJob()), value);
                        statMap.put(Stat.SP, cs.getSp());
                    } else {
                        cs.getSp().setNonExtendSp(value);
                        statMap.put(Stat.SP, (short) cs.getSp().getNonExtendSp());
                    }
                }
                default -> {
                    user.write(MessagePacket.system(
                            "Syntax: %sstat hp/mp/str/dex/int/luk/ap/sp <new value>",
                            ServerConfig.PLAYER_COMMAND_PREFIX));
                    return;
                }
            }

            user.validateStat();
            user.write(WvsContext.statChanged(statMap, true));
            user.write(MessagePacket.system("Set %s to %d", stat, value));

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system(
                    "Syntax: %sstat hp/mp/str/dex/int/luk/ap/sp <new value>",
                    ServerConfig.PLAYER_COMMAND_PREFIX));
        }
    }
}
