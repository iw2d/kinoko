package kinoko.server.command;

import kinoko.packet.user.UserLocalPacket;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.provider.ItemProvider;
import kinoko.provider.MobProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.Foothold;
import kinoko.provider.mob.MobInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.server.ServerConfig;
import kinoko.server.script.ScriptDispatcher;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.item.Item;
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
        user.write(WvsContext.message(Message.system("x : %d, y : %d, fh : %d (%d)", user.getX(), user.getY(), user.getFoothold(),
                user.getField().getFootholdBelow(user.getX(), user.getY()).get().getFootholdId())));
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
        final Optional<Foothold> footholdResult = user.getField().getFootholdBelow(user.getX(), user.getY());
        final Mob mob = new Mob(
                user.getX(),
                user.getY(),
                footholdResult.map(Foothold::getFootholdId).orElse(user.getFoothold()),
                mobInfoResult.get(),
                MobAppearType.NORMAL
        );
        mob.setField(user.getField());
        user.getField().getLifePool().addLife(mob);
    }

    @Command("item")
    public static void item(User user, String[] args) {
        if (args.length < 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %sitem <item ID> [item quantity]", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int itemId = Integer.parseInt(args[1]);
        final int quantity;
        if (args.length == 2) {
            quantity = 1;
        } else if (args.length == 3 && Util.isInteger(args[2])) {
            quantity = Integer.parseInt(args[2]);
        } else {
            user.write(WvsContext.message(Message.system("Syntax : %sitem <item ID> [item quantity]", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve item ID : %d", itemId)));
            return;
        }
        final ItemInfo ii = itemInfoResult.get();
        final Item item = Item.createByInfo(user.getCharacterData().getNextItemSn(), ii, Math.min(quantity, ii.getSlotMax()));
        if (user.addItem(item)) {
            user.write(UserLocalPacket.userEffect(Effect.gainItem(item.getItemId(), item.getQuantity())));
        } else {
            user.write(WvsContext.message(Message.system("Failed to add item {} to inventory", itemId)));
        }
    }

    @Command("meso")
    public static void meso(User user, String[] args) {
        user.addMoney(1_000_000);
        user.write(WvsContext.message(Message.system("Money : %d", user.getMoney())));
    }
}
