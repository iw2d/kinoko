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
import kinoko.provider.mob.MobSkillType;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.provider.skill.SkillStringInfo;
import kinoko.server.ServerConfig;
import kinoko.server.cashshop.CashShop;
import kinoko.server.cashshop.Commodity;
import kinoko.server.script.ScriptDispatcher;
import kinoko.util.Rect;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.Item;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.Account;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.Stat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.*;

public final class AdminCommands {
    @Command("test")
    public static void test(User user, String[] args) {
        System.out.println(user.getCharacterStat().getPortal());
        user.dispose();
    }

    @Command("dispose")
    public static void dispose(User user, String[] args) {
        ScriptDispatcher.removeScriptManager(user);
        user.closeDialog();
        user.dispose();
        user.write(WvsContext.message(Message.system("You have been disposed.")));
    }

    @Command("info")
    public static void info(User user, String[] args) {
        final Field field = user.getField();
        user.write(WvsContext.message(Message.system("HP : %d / %d, MP : %d / %d", user.getHp(), user.getMaxHp(), user.getMp(), user.getMaxMp())));
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
        // Compute nearest mob
        final Optional<Mob> nearestMobResult = user.getNearestObject(field.getMobPool().getInsideRect(user.getRelativeRect(new Rect(-100, -100, 100, 100))));
        if (nearestMobResult.isPresent()) {
            final Mob nearestMob = nearestMobResult.get();
            user.write(WvsContext.message(Message.system("%s", nearestMob.toString())));
            user.write(WvsContext.message(Message.system("  Controller : %s", nearestMob.getController().getCharacterName())));
        }
    }

    @Command({ "find", "lookup" })
    public static void find(User user, String[] args) {
        if (args.length < 3) {
            user.write(WvsContext.message(Message.system("Syntax : %sfind <item/map/mob/npc/skill/commodity> <id or query>", ServerConfig.COMMAND_PREFIX)));
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
            user.write(WvsContext.message(Message.system("Item : %s (%d)", StringProvider.getItemName(itemId), itemId)));
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
            user.write(WvsContext.message(Message.system("Map : %s (%d)", StringProvider.getMapName(mapId), mapId)));
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
            user.write(WvsContext.message(Message.system("Mob : %s (%d)", StringProvider.getMobName(mobId), mobId)));
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
            user.write(WvsContext.message(Message.system("Npc : %s (%d)", StringProvider.getNpcName(npcId), npcId)));
            user.write(WvsContext.message(Message.system("  script : %s", npcTemplate.getScript())));
        } else if (type.equalsIgnoreCase("skill")) {
            int skillId = -1;
            if (isNumber) {
                skillId = Integer.parseInt(query);
            } else {
                final List<Map.Entry<Integer, SkillStringInfo>> searchResult = StringProvider.getSkillStrings().entrySet().stream()
                        .filter((entry) -> entry.getValue().getName().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .toList();
                if (!searchResult.isEmpty()) {
                    if (searchResult.size() == 1) {
                        skillId = searchResult.get(0).getKey();
                    } else {
                        user.write(WvsContext.message(Message.system("Results for skill name : \"%s\"", query)));
                        for (var entry : searchResult) {
                            user.write(WvsContext.message(Message.system("  %d : %s", entry.getKey(), entry.getValue().getName())));
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
            user.write(WvsContext.message(Message.system("Skill : %s (%d)", StringProvider.getSkillName(skillId), skillId)));
        } else if (type.equalsIgnoreCase("commodity")) {
            if (!isNumber) {
                user.write(WvsContext.message(Message.system("Can only lookup commodity by ID")));
                return;
            }
            final int commodityId = Integer.parseInt(query);
            final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
            if (commodityResult.isEmpty()) {
                user.write(WvsContext.message(Message.system("Could not find commodity with ID : %d", commodityId)));
                return;
            }
            final Commodity commodity = commodityResult.get();
            user.write(WvsContext.message(Message.system("Commodity : %d", commodityId)));
            user.write(WvsContext.message(Message.system("  itemId : %d (%s)", commodity.getItemId(), StringProvider.getItemName(commodity.getItemId()))));
            user.write(WvsContext.message(Message.system("  count : %d", commodity.getCount())));
            user.write(WvsContext.message(Message.system("  price : %d", commodity.getPrice())));
            user.write(WvsContext.message(Message.system("  period : %d", commodity.getPeriod())));
            user.write(WvsContext.message(Message.system("  gender : %d", commodity.getGender())));
        } else {
            user.write(WvsContext.message(Message.system("Syntax : %sfind <item/map/mob/npc/skill/commodity> <id or query>", ServerConfig.COMMAND_PREFIX)));
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

    @Command({ "mob", "spawn" })
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
                null,
                user.getX(),
                user.getY(),
                footholdResult.map(Foothold::getFootholdId).orElse(user.getFoothold())
        );
        field.getMobPool().addMob(mob);
        mob.setAppearType(MobAppearType.NORMAL);
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
        try (var locked = user.acquire()) {
            final InventoryManager im = locked.get().getInventoryManager();
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
            if (addItemResult.isPresent()) {
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(UserLocal.effect(Effect.gainItem(item)));
            } else {
                user.write(WvsContext.message(Message.system("Failed to add item ID %d (%d) to inventory", itemId, quantity)));
            }
        }
    }

    @Command({ "meso", "money" })
    public static void meso(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %smeso <new mesos>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int money = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            final InventoryManager im = locked.get().getInventoryManager();
            im.setMoney(money);
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
        }
    }

    @Command("nx")
    public static void nx(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %snx <value>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int nx = Integer.parseInt(args[1]);
        try (var lockedAccount = user.getAccount().acquire()) {
            final Account account = lockedAccount.get();
            account.setNxPrepaid(nx);
            user.write(WvsContext.message(Message.system("Set NX prepaid to %d", nx)));
        }
    }

    @Command("hp")
    public static void hp(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %shp <new hp>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int newHp = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            user.setHp(newHp);
        }
    }

    @Command("mp")
    public static void mp(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %smp <new mp>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int newMp = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            user.setMp(newMp);
        }
    }

    @Command("stat")
    public static void stat(User user, String[] args) {
        if (args.length != 3 || !Util.isInteger(args[2])) {
            user.write(WvsContext.message(Message.system("Syntax : %sstat hp/mp/str/dex/int/luk/ap/sp <new value>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final String stat = args[1].toLowerCase();
        final int value = Integer.parseInt(args[2]);
        try (var locked = user.acquire()) {
            final CharacterStat cs = locked.get().getCharacterStat();
            final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
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
                        statMap.put(Stat.SP, cs.getSp().getNonExtendSp());
                    }
                }
                default -> {
                    user.write(WvsContext.message(Message.system("Syntax : %sstat hp/mp/str/dex/int/luk/ap/sp <new value>", ServerConfig.COMMAND_PREFIX)));
                    return;
                }
            }
            user.validateStat();
            user.write(WvsContext.statChanged(statMap, true));
            user.write(WvsContext.message(Message.system("Set %s to %d", stat, value)));
        }
    }

    @Command("level")
    public static void level(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %slevel <new level>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int level = Integer.parseInt(args[1]);
        if (level < 1 || level > GameConstants.LEVEL_MAX) {
            user.write(WvsContext.message(Message.system("Could not change level to : {}", level)));
            return;
        }
        try (var locked = user.acquire()) {
            final CharacterStat cs = user.getCharacterStat();
            cs.setLevel((short) level);
            user.validateStat();
            user.write(WvsContext.statChanged(Stat.LEVEL, (byte) cs.getLevel(), true));
        }
    }

    @Command("job")
    public static void job(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %sjob <job id>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int jobId = Integer.parseInt(args[1]);
        if (Job.getById(jobId) == null) {
            user.write(WvsContext.message(Message.system("Could not change to unknown job : {}", jobId)));
            return;
        }
        try (var locked = user.acquire()) {
            user.setJob(jobId);
        }
    }

    @Command("skill")
    public static void skill(User user, String[] args) {
        if (args.length != 3 || !Util.isInteger(args[1]) || !Util.isInteger(args[2])) {
            user.write(WvsContext.message(Message.system("Syntax : %sskill <skill id> <skill level>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int skillId = Integer.parseInt(args[1]);
        final int slv = Integer.parseInt(args[2]);
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not find skill : {}", skillId)));
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        final SkillRecord skillRecord = si.createRecord();
        skillRecord.setSkillLevel(Math.min(slv, si.getMaxLevel()));
        skillRecord.setMasterLevel(si.getMaxLevel());
        try (var locked = user.acquire()) {
            final SkillManager sm = user.getSkillManager();
            sm.addSkill(skillRecord);
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecord, true));
        }
    }

    @Command("startquest")
    public static void startQuest(User user, String[] args) {
        if (args.length != 2 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %sstartquest <quest id>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int questId = Integer.parseInt(args[1]);
        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not find quest : %d", questId)));
            return;
        }
        try (var locked = user.acquire()) {
            final QuestRecord qr = user.getQuestManager().forceStartQuest(questId);
            user.write(WvsContext.message(Message.questRecord(qr)));
            user.validateStat();
        }
    }

    @Command("questex")
    public static void questex(User user, String[] args) {
        if (args.length != 3 || !Util.isInteger(args[1])) {
            user.write(WvsContext.message(Message.system("Syntax : %squestex <quest id> <qr value>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int questId = Integer.parseInt(args[1]);
        final String infoValue = args[2];
        try (var locked = user.acquire()) {
            final QuestRecord qr = user.getQuestManager().setQuestInfoEx(questId, infoValue);
            user.write(WvsContext.message(Message.questRecord(qr)));
            user.validateStat();
        }
    }

    @Command("killmobs")
    public static void killMobs(User user, String[] args) {
        user.getField().getMobPool().forEach((mob) -> {
            if (mob.getHp() > 0) {
                mob.damage(user, mob.getMaxHp());
            }
        });
    }

    @Command("mobskill")
    public static void mobskill(User user, String[] args) {
        if (args.length != 3 || !Util.isInteger(args[1]) || !Util.isInteger(args[2])) {
            user.write(WvsContext.message(Message.system("Syntax : %smobskill <skill id> <level>", ServerConfig.COMMAND_PREFIX)));
            return;
        }
        final int skillId = Integer.parseInt(args[1]);
        final int slv = Integer.parseInt(args[2]);
        final MobSkillType skillType = MobSkillType.getByValue(skillId);
        if (skillType == null) {
            user.write(WvsContext.message(Message.system("Could not resolve mob skill %d", skillId)));
            return;
        }
        final CharacterTemporaryStat cts = skillType.getCharacterTemporaryStat();
        if (cts == null) {
            user.write(WvsContext.message(Message.system("Could not resolve mob skill {} does not apply a CTS", skillType)));
            return;
        }
        // Apply mob skill
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getMobSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            user.write(WvsContext.message(Message.system("Could not resolve mob skill info %d", skillId)));
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        try (var locked = user.acquire()) {
            locked.get().setTemporaryStat(cts, TemporaryStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv)));
        }
    }

    @Command("max")
    public static void max(User user, String[] args) {
        try (var locked = user.acquire()) {
            // Set stats
            final CharacterStat cs = user.getCharacterStat();
            cs.setLevel((short) 200);
            cs.setBaseStr((short) 10000);
            cs.setBaseDex((short) 10000);
            cs.setBaseInt((short) 10000);
            cs.setBaseLuk((short) 10000);
            cs.setMaxHp(50000);
            cs.setMaxMp(50000);
            cs.setExp(0);
            user.validateStat();
            user.write(WvsContext.statChanged(Map.of(
                    Stat.LEVEL, (byte) cs.getLevel(),
                    Stat.STR, cs.getBaseStr(),
                    Stat.DEX, cs.getBaseDex(),
                    Stat.INT, cs.getBaseInt(),
                    Stat.LUK, cs.getBaseLuk(),
                    Stat.MAX_HP, cs.getMaxHp(),
                    Stat.MAX_MP, cs.getMaxMp(),
                    Stat.EXP, cs.getExp()
            ), true));

            // Reset skills
            final SkillManager sm = user.getSkillManager();
            final Set<SkillRecord> removedRecords = new HashSet<>();
            for (SkillRecord skillRecord : sm.getSkillRecords()) {
                skillRecord.setSkillLevel(0);
                skillRecord.setMasterLevel(0);
                removedRecords.add(skillRecord);
            }
            user.write(WvsContext.changeSkillRecordResult(removedRecords, true));


            // Add skills
            final Set<SkillRecord> skillRecords = new HashSet<>();
            for (int skillRoot : JobConstants.getSkillRootFromJob(user.getJob())) {
                final Job job = Job.getById(skillRoot);
                for (SkillInfo si : SkillProvider.getSkillsForJob(job)) {
                    final SkillRecord skillRecord = si.createRecord();
                    skillRecord.setSkillLevel(si.getMaxLevel());
                    skillRecord.setMasterLevel(si.getMaxLevel());
                    sm.addSkill(skillRecord);
                    skillRecords.add(skillRecord);
                }
            }
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecords, true));

            // Heal
            user.setHp(user.getMaxHp());
            user.setMp(user.getMaxMp());
        }
    }
}
