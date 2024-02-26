package kinoko.server.command;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.MapInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.ServerConfig;
import kinoko.server.script.ScriptDispatcher;
import kinoko.util.Triple;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.life.mob.Mob;
import kinoko.world.field.life.mob.MobAppearType;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.Item;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

import java.util.*;

public final class AdminCommands {
    @Command("test")
    public static void test(User user, String[] args) {
        user.getField().getReactorPool().forEach(reactor -> {
            System.out.println(reactor);
        });
        user.dispose();
    }

    @Command("dispose")
    public static void dispose(User user, String[] args) {
        ScriptDispatcher.removeScriptManager(user);
        user.dispose();
        user.write(WvsContext.message(Message.system("You have been disposed.")));
    }

    @Command("info")
    public static void info(User user, String[] args) {
        final Field field = user.getField();
        user.write(WvsContext.message(Message.system("Field ID : %d", field.getFieldId())));
        // Compute foothold below
        final Optional<Foothold> footholdBelowResult = field.getFootholdBelow(user.getX(), user.getY());
        final String footholdBelow = footholdBelowResult.map(foothold -> String.valueOf(foothold.getFootholdId())).orElse("unk");
        user.write(WvsContext.message(Message.system("  x : %d, y : %d, fh : %d (%s)", user.getX(), user.getY(), user.getFoothold(), footholdBelow)));
        // Compute nearest portal
        double nearestDistance = Double.MAX_VALUE;
        PortalInfo nearestPortal = null;
        for (PortalInfo pi : field.getMapInfo().getPortalInfos()) {
            final double distance = Util.distance(user.getX(), user.getY(), pi.getX(), pi.getY());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPortal = pi;
            }
        }
        if (nearestPortal != null && nearestDistance < 100) {
            user.write(WvsContext.message(Message.system("Portal name : %s (%d)", nearestPortal.getPortalName(), nearestPortal.getPortalId())));
            user.write(WvsContext.message(Message.system("  x : %d, y : %d, script : %s",
                    nearestPortal.getX(), nearestPortal.getY(), nearestPortal.getScript())));
        }
    }

    @Command({ "find", "lookup" })
    public static void find(User user, String[] args) {
        if (args.length < 3) {
            user.write(WvsContext.message(Message.system("Syntax : %sfind <item/map/mob/npc/skill> <id or query>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final String type = args[1];
        final String query = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        final boolean isNumber = Util.isInteger(query);
        if (type.equalsIgnoreCase("item")) {
            int itemId = -1;
            if (isNumber) {
                itemId = Integer.parseInt(query);
            } else {
                final List<Map.Entry<Integer, String>> searchResult = StringProvider.getItemNames().entrySet().stream()
                        .filter((entry) -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .toList();
                if (!searchResult.isEmpty()) {
                    if (searchResult.size() == 1) {
                        itemId = searchResult.get(0).getKey();
                    } else {
                        user.write(WvsContext.message(Message.system("Results for item name : \"%s\"", query)));
                        for (var entry : searchResult) {
                            user.write(WvsContext.message(Message.system("  %d : %s", entry.getKey(), entry.getValue())));
                        }
                        return;
                    }
                }
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty()) {
                user.write(WvsContext.message(Message.system("Could not find item with %s : %s", isNumber ? "ID" : "name", query)));
                return;
            }
            final ItemInfo ii = itemInfoResult.get();
            user.write(WvsContext.message(Message.system("%s (%d)", StringProvider.getItemName(itemId), itemId)));
            if (!ii.getItemInfos().isEmpty()) {
                user.write(WvsContext.message(Message.system("  info")));
                for (var entry : ii.getItemInfos().entrySet()) {
                    user.write(WvsContext.message(Message.system("    %s : %s", entry.getKey().name(), entry.getValue().toString())));
                }
            }
            if (!ii.getItemSpecs().isEmpty()) {
                user.write(WvsContext.message(Message.system("  spec")));
                for (var entry : ii.getItemSpecs().entrySet()) {
                    user.write(WvsContext.message(Message.system("    %s : %s", entry.getKey().name(), entry.getValue().toString())));
                }
            }
        } else if (type.equalsIgnoreCase("map")) {
            int mapId = -1;
            if (isNumber) {
                mapId = Integer.parseInt(query);
            } else {
                final List<Map.Entry<Integer, String>> searchResult = StringProvider.getMapNames().entrySet().stream()
                        .filter((entry) -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .toList();
                if (!searchResult.isEmpty()) {
                    if (searchResult.size() == 1) {
                        mapId = searchResult.get(0).getKey();
                    } else {
                        user.write(WvsContext.message(Message.system("Results for map name : \"%s\"", query)));
                        for (var entry : searchResult) {
                            user.write(WvsContext.message(Message.system("  %d : %s", entry.getKey(), entry.getValue())));
                        }
                        return;
                    }
                }
            }
            final Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapId);
            if (mapInfoResult.isEmpty()) {
                user.write(WvsContext.message(Message.system("Could not find map with %s : %s", isNumber ? "ID" : "name", query)));
                return;
            }
            final MapInfo mapInfo = mapInfoResult.get();
            user.write(WvsContext.message(Message.system("%s (%d)", StringProvider.getMapName(mapId), mapId)));
            user.write(WvsContext.message(Message.system("  type : %s", mapInfo.getFieldType().name())));
            user.write(WvsContext.message(Message.system("  returnMap : %d", mapInfo.getReturnMap())));
            user.write(WvsContext.message(Message.system("  onFirstUserEnter : %s", mapInfo.getOnFirstUserEnter())));
            user.write(WvsContext.message(Message.system("  onUserEnter : %s", mapInfo.getOnUserEnter())));
        } else if (type.equalsIgnoreCase("mob")) {
            int mobId = -1;
            if (isNumber) {
                mobId = Integer.parseInt(query);
            } else {
                final List<Map.Entry<Integer, String>> searchResult = StringProvider.getMobNames().entrySet().stream()
                        .filter((entry) -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .toList();
                if (!searchResult.isEmpty()) {
                    if (searchResult.size() == 1) {
                        mobId = searchResult.get(0).getKey();
                    } else {
                        user.write(WvsContext.message(Message.system("Results for mob name : \"%s\"", query)));
                        for (var entry : searchResult) {
                            user.write(WvsContext.message(Message.system("  %d : %s", entry.getKey(), entry.getValue())));
                        }
                        return;
                    }
                }
            }
            final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(mobId);
            if (mobTemplateResult.isEmpty()) {
                user.write(WvsContext.message(Message.system("Could not find mob with %s : %s", isNumber ? "ID" : "name", query)));
                return;
            }
            final MobTemplate mobTemplate = mobTemplateResult.get();
            user.write(WvsContext.message(Message.system("%s (%d)", StringProvider.getMobName(mobId), mobId)));
            user.write(WvsContext.message(Message.system("  level : %d", mobTemplate.getLevel())));
        } else if (type.equalsIgnoreCase("npc")) {
            int npcId = -1;
            if (isNumber) {
                npcId = Integer.parseInt(query);
            } else {
                final List<Map.Entry<Integer, String>> searchResult = StringProvider.getNpcNames().entrySet().stream()
                        .filter((entry) -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .toList();
                if (!searchResult.isEmpty()) {
                    if (searchResult.size() == 1) {
                        npcId = searchResult.get(0).getKey();
                    } else {
                        user.write(WvsContext.message(Message.system("Results for npc name : \"%s\"", query)));
                        for (var entry : searchResult) {
                            user.write(WvsContext.message(Message.system("  %d : %s", entry.getKey(), entry.getValue())));
                        }
                        return;
                    }
                }
            }
            final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(npcId);
            if (npcTemplateResult.isEmpty()) {
                user.write(WvsContext.message(Message.system("Could not find npc with %s : %s", isNumber ? "ID" : "name", query)));
                return;
            }
            final NpcTemplate npcTemplate = npcTemplateResult.get();
            user.write(WvsContext.message(Message.system("%s (%d)", StringProvider.getNpcName(npcId), npcId)));
            user.write(WvsContext.message(Message.system("  script : %s", npcTemplate.getScript())));
        } else if (type.equalsIgnoreCase("skill")) {
            int skillId = -1;
            if (isNumber) {
                skillId = Integer.parseInt(query);
            } else {
                final List<Map.Entry<Integer, Triple<String, String, String>>> searchResult = StringProvider.getSkillStrings().entrySet().stream()
                        .filter((entry) -> entry.getValue().getLeft().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .toList();
                if (!searchResult.isEmpty()) {
                    if (searchResult.size() == 1) {
                        skillId = searchResult.get(0).getKey();
                    } else {
                        user.write(WvsContext.message(Message.system("Results for skill name : \"%s\"", query)));
                        for (var entry : searchResult) {
                            user.write(WvsContext.message(Message.system("  %d : %s", entry.getKey(), entry.getValue().getLeft())));
                        }
                        return;
                    }
                }
            }
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                user.write(WvsContext.message(Message.system("Could not find skill with %s : %s", isNumber ? "ID" : "name", query)));
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            user.write(WvsContext.message(Message.system("%s (%d)", StringProvider.getSkillName(skillId), skillId)));
        } else {
            user.write(WvsContext.message(Message.system("Syntax : %sfind <item/map/mob/npc/skill> <id or query>", ServerConfig.COMMAND_PREFIX)));
        }
    }

    @Command("npc")
    public static void npc(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %snpc <npc template ID>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int templateId = Integer.parseInt(args[1]);
        final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(templateId);
        if (npcTemplateResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve npc ID : %d", templateId)));
            return;
        }
        final String scriptName = npcTemplateResult.get().getScript();
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
        final Field targetField = fieldResult.get();
        final Optional<PortalInfo> portalResult = targetField.getPortalById(0);
        if (portalResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve portal for field ID : %d", fieldId)));
            return;
        }
        user.warp(targetField, portalResult.get(), false, false);
    }

    @Command("mob")
    public static void mob(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %smob <mob template ID to spawn>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int mobId = Integer.parseInt(args[1]);
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(mobId);
        if (mobTemplateResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve mob ID : %d", mobId)));
            return;
        }
        final Field field = user.getField();
        final Optional<Foothold> footholdResult = field.getFootholdBelow(user.getX(), user.getY());
        final Mob mob = new Mob(
                mobTemplateResult.get(),
                user.getX(),
                user.getY(),
                footholdResult.map(Foothold::getFootholdId).orElse(user.getFoothold()),
                0,
                false
        );
        mob.setAppearType(MobAppearType.REGEN);
        field.getMobPool().addMob(mob);
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
        final Item item = ii.createItem(user.getNextItemSn(), Math.min(quantity, ii.getSlotMax()));

        // Add item
        final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(item);
        if (addItemResult.isPresent()) {
            user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(item)));
        } else {
            user.write(WvsContext.message(Message.system("Failed to add item ID %d (%d) to inventory", itemId, quantity)));
        }
    }

    @Command("meso")
    public static void meso(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %smeso <mesos to add>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int money = Integer.parseInt(args[1]);
        final InventoryManager im = user.getInventoryManager();
        if (im.addMoney(money)) {
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney()));
            user.write(WvsContext.message(Message.incMoney(money)));
        } else {
            user.write(WvsContext.message(Message.system("Failed to add %d mesos", money)));
        }
    }
}
