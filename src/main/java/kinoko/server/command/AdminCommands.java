package kinoko.server.command;

import kinoko.packet.world.Message;
import kinoko.packet.world.WvsContext;
import kinoko.provider.MobProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.mob.MobInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.server.ServerConfig;
import kinoko.server.script.ScriptDispatcher;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.life.mob.Mob;
import kinoko.world.life.mob.MobAppearType;
import kinoko.world.user.User;

import java.util.Optional;

public final class AdminCommands {
    @Command("dispose")
    public static void dispose(User user, String[] args) {
        ScriptDispatcher.removeScriptManager(user);
        user.dispose();
        user.write(WvsContext.message(Message.system("You have been disposed.")));
    }

    @Command("info")
    public static void info(User user, String[] args) {
        user.write(WvsContext.message(Message.system("Field ID : %d", user.getField().getFieldId())));
        user.write(WvsContext.message(Message.system("x : %d, y : %d, fh : %d", user.getX(), user.getY(), user.getFoothold())));
    }

    @Command("npc")
    public static void npc(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %snpc <npc template ID>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int templateId = Integer.parseInt(args[1]);
        final Optional<NpcInfo> npcInfoResult = NpcProvider.getNpcInfo(templateId);
        if (npcInfoResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve npc ID : %d", templateId)));
            return;
        }
        final String scriptName = npcInfoResult.get().getScript();
        if (scriptName == null || scriptName.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not find script for npc ID : %d", templateId)));
            return;
        }
        user.write(WvsContext.message(Message.system("Starting script for npc ID : %d, script : %s", templateId, scriptName)));
        ScriptDispatcher.startNpcScript(user, templateId, scriptName);
    }

    @Command("map")
    public static void map(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %smap <field ID to warp to>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int fieldId = Integer.parseInt(args[1]);
        final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve field ID : %d", fieldId)));
            return;
        }
        user.write(WvsContext.message(Message.system("Warping to field ID : %d", fieldId)));
        user.warp(fieldResult.get(), 0, false, false);
    }

    @Command("mob")
    public static void mob(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %smob <mob template ID to spawn>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int mobId = Integer.parseInt(args[1]);
        final Optional<MobInfo> mobInfoResult = MobProvider.getMobInfo(mobId);
        if (mobInfoResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve mob ID : %d", mobId)));
            return;
        }
        final Mob mob = new Mob(
                user.getX(),
                user.getY(),
                user.getFoothold(),
                mobInfoResult.get(),
                MobAppearType.NORMAL
        );
        mob.setField(user.getField());
        user.getField().getLifePool().addLife(mob);
    }

    @Command("meso")
    public static void meso(User user, String[] args) {
        user.addMoney(1_000_000);
        user.write(WvsContext.message(Message.system("Money : %d", user.getMoney())));
    }
}
