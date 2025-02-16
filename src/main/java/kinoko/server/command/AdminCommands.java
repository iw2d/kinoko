package kinoko.server.command;

import kinoko.packet.user.DragonPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.MapInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.map.ReactorInfo;
import kinoko.provider.mob.MobSkillType;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.provider.skill.SkillStringInfo;
import kinoko.script.common.ScriptDispatcher;
import kinoko.server.ServerConfig;
import kinoko.server.cashshop.CashShop;
import kinoko.server.cashshop.Commodity;
import kinoko.util.BitFlag;
import kinoko.util.Rect;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobLeaveType;
import kinoko.world.field.npc.Npc;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.item.*;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.job.explorer.Beginner;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.legend.Aran;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.Account;
import kinoko.world.user.Dragon;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.*;

import java.lang.reflect.Method;
import java.util.*;

public final class AdminCommands {
    @Command("test")
    public static void test(User user, String[] args) {
        user.getConnectedServer().submitUserQueryRequestAll((queryResult) -> {
            user.write(MessagePacket.system("Users in world: %d", queryResult.size()));
            user.write(MessagePacket.system("Users in field : %d", user.getField().getUserPool().getCount()));
            user.write(MessagePacket.system("Party ID : %d (%d)", user.getPartyId(), user.getCharacterData().getPartyId()));
            user.setConsumeItemEffect(ItemProvider.getItemInfo(2022181).orElseThrow());
            user.dispose();
        });
    }

    @Command("dispose")
    public static void dispose(User user, String[] args) {
        try (var locked = user.acquire()) {
            user.closeDialog();
            user.dispose();
        }
        user.write(MessagePacket.system("You have been disposed."));
    }

    @Command("info")
    public static void info(User user, String[] args) {
        // User stats
        final Field field = user.getField();
        user.write(MessagePacket.system("HP : %d / %d, MP : %d / %d", user.getHp(), user.getMaxHp(), user.getMp(), user.getMaxMp()));
        user.write(MessagePacket.system("STR : %d", user.getBasicStat().getStr()));
        user.write(MessagePacket.system("DEX : %d", user.getBasicStat().getDex()));
        user.write(MessagePacket.system("INT : %d", user.getBasicStat().getInt()));
        user.write(MessagePacket.system("LUK : %d", user.getBasicStat().getLuk()));
        user.write(MessagePacket.system("AP  : %d", user.getCharacterStat().getAp()));
        user.write(MessagePacket.system("SP  : %s", user.getCharacterStat().getSp().getMap()));
        user.write(MessagePacket.system("Damage : %d ~ %d", (int) CalcDamage.calcDamageMin(user), (int) CalcDamage.calcDamageMax(user)));
        user.write(MessagePacket.system("Field ID : %d (%s)", field.getFieldId(), field.getFieldType()));
        // Compute foothold below
        final Optional<Foothold> footholdBelowResult = field.getFootholdBelow(user.getX(), user.getY());
        final String footholdBelow = footholdBelowResult.map(foothold -> String.valueOf(foothold.getSn())).orElse("unk");
        user.write(MessagePacket.system("  x : %d, y : %d, fh : %d (%s)", user.getX(), user.getY(), user.getFoothold(), footholdBelow));
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
        if (nearestPortal != null && nearestDistance < 200) {
            user.write(MessagePacket.system("Portal name : %s (%d)", nearestPortal.getPortalName(), nearestPortal.getPortalId()));
            user.write(MessagePacket.system("  x : %d, y : %d, script : %s",
                    nearestPortal.getX(), nearestPortal.getY(), nearestPortal.getScript()));
        }
        // Compute nearest mob
        final Rect detectRect = Rect.of(-400, -400, 400, 400);
        final Optional<Mob> nearestMobResult = user.getNearestObject(field.getMobPool().getInsideRect(user.getRelativeRect(detectRect)));
        if (nearestMobResult.isPresent()) {
            final Mob mob = nearestMobResult.get();
            user.write(MessagePacket.system(mob.toString()));
            user.write(MessagePacket.system("  Controller : %s", mob.getController().getCharacterName()));
        }
        // Compute nearest npc
        final Optional<Npc> nearestNpcResult = user.getNearestObject(field.getNpcPool().getInsideRect(user.getRelativeRect(detectRect)));
        if (nearestNpcResult.isPresent()) {
            final Npc npc = nearestNpcResult.get();
            user.write(MessagePacket.system(npc.toString()));
        }
        // Compute nearest reactor
        final Optional<Reactor> nearestReactorResult = user.getNearestObject(field.getReactorPool().getInsideRect(user.getRelativeRect(detectRect)));
        if (nearestReactorResult.isPresent()) {
            final Reactor reactor = nearestReactorResult.get();
            user.write(MessagePacket.system(reactor.toString()));
        }
    }

    @Command({ "find", "lookup" })
    @Arguments({ "item/map/mob/npc/skill/quest/commodity", "id or query" })
    public static void find(User user, String[] args) {
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
                        user.write(MessagePacket.system("Results for item name : \"%s\"", query));
                        for (var entry : searchResult) {
                            user.write(MessagePacket.system("  %d : %s", entry.getKey(), entry.getValue()));
                        }
                        return;
                    }
                }
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find item with %s : %s", isNumber ? "ID" : "name", query));
                return;
            }
            final ItemInfo ii = itemInfoResult.get();
            user.write(MessagePacket.system("Item : %s (%d)", StringProvider.getItemName(itemId), itemId));
            if (!ii.getItemInfos().isEmpty()) {
                user.write(MessagePacket.system("  info"));
                for (var entry : ii.getItemInfos().entrySet()) {
                    user.write(MessagePacket.system("    %s : %s", entry.getKey().name(), entry.getValue().toString()));
                }
            }
            if (!ii.getItemSpecs().isEmpty()) {
                user.write(MessagePacket.system("  spec"));
                for (var entry : ii.getItemSpecs().entrySet()) {
                    user.write(MessagePacket.system("    %s : %s", entry.getKey().name(), entry.getValue().toString()));
                }
            }
        } else if (type.equalsIgnoreCase("map")) {
            final int mapId;
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
                        mapId = -1;
                        user.write(MessagePacket.system("Results for map name : \"%s\"", query));
                        for (var entry : searchResult) {
                            user.write(MessagePacket.system("  %d : %s", entry.getKey(), entry.getValue()));
                        }
                        return;
                    }
                } else {
                    mapId = -1;
                }
            }
            final Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapId);
            if (mapInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find map with %s : %s", isNumber ? "ID" : "name", query));
                return;
            }
            final List<MapInfo> connectedMaps = MapProvider.getMapInfos().stream()
                    .filter((mapInfo) -> mapInfo.getPortalInfos().stream().anyMatch((portalInfo) -> portalInfo.getDestinationFieldId() == mapId))
                    .sorted(Comparator.comparingInt(MapInfo::getMapId))
                    .toList();
            final MapInfo mapInfo = mapInfoResult.get();
            user.write(MessagePacket.system("Map : %s (%d)", StringProvider.getMapName(mapId), mapId));
            user.write(MessagePacket.system("  type : %s", mapInfo.getFieldType().name()));
            user.write(MessagePacket.system("  returnMap : %d", mapInfo.getReturnMap()));
            user.write(MessagePacket.system("  forcedReturn : %d", mapInfo.getForcedReturn()));
            user.write(MessagePacket.system("  onFirstUserEnter : %s", mapInfo.getOnFirstUserEnter()));
            user.write(MessagePacket.system("  onUserEnter : %s", mapInfo.getOnUserEnter()));
            if (!mapInfo.getPortalInfos().isEmpty()) {
                user.write(MessagePacket.system("  portals :"));
                for (PortalInfo portalInfo : mapInfo.getPortalInfos().stream().sorted(Comparator.comparingInt(PortalInfo::getPortalId)).toList()) {
                    user.write(MessagePacket.system("    %s (%d, %d)", portalInfo.getPortalName(), portalInfo.getX(), portalInfo.getY()));
                }
            }
            if (!connectedMaps.isEmpty()) {
                user.write(MessagePacket.system("  connectedMap :"));
                for (MapInfo connectedMapInfo : connectedMaps) {
                    user.write(MessagePacket.system("    %s (%d)", StringProvider.getMapName(connectedMapInfo.getMapId()), connectedMapInfo.getMapId()));
                }
            }
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
                        user.write(MessagePacket.system("Results for mob name : \"%s\"", query));
                        for (var entry : searchResult) {
                            user.write(MessagePacket.system("  %d : %s", entry.getKey(), entry.getValue()));
                        }
                        return;
                    }
                }
            }
            final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(mobId);
            if (mobTemplateResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find mob with %s : %s", isNumber ? "ID" : "name", query));
                return;
            }
            final MobTemplate mobTemplate = mobTemplateResult.get();
            user.write(MessagePacket.system("Mob : %s (%d)", StringProvider.getMobName(mobId), mobId));
            user.write(MessagePacket.system("  level : %d", mobTemplate.getLevel()));
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
                        user.write(MessagePacket.system("Results for npc name : \"%s\"", query));
                        for (var entry : searchResult) {
                            user.write(MessagePacket.system("  %d : %s", entry.getKey(), entry.getValue()));
                        }
                        return;
                    }
                }
            }
            final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(npcId);
            if (npcTemplateResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find npc with %s : %s", isNumber ? "ID" : "name", query));
                return;
            }
            final NpcTemplate npcTemplate = npcTemplateResult.get();
            final List<MapInfo> npcFields = MapProvider.getMapInfos().stream()
                    .filter((mapInfo) -> mapInfo.getLifeInfos().stream().anyMatch((lifeInfo) -> lifeInfo.getTemplateId() == npcTemplate.getId()))
                    .sorted(Comparator.comparingInt(MapInfo::getMapId))
                    .toList();
            user.write(MessagePacket.system("Npc : %s (%d)", StringProvider.getNpcName(npcId), npcId));
            user.write(MessagePacket.system("  script : %s", npcTemplate.getScript()));
            for (MapInfo mapInfo : npcFields) {
                user.write(MessagePacket.system("  field : %s (%d)", StringProvider.getMapName(mapInfo.getMapId()), mapInfo.getMapId()));
            }
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
                        skillId = searchResult.getFirst().getKey();
                    } else {
                        user.write(MessagePacket.system("Results for skill name : \"%s\"", query));
                        for (var entry : searchResult) {
                            user.write(MessagePacket.system("  %d : %s", entry.getKey(), entry.getValue().getName()));
                        }
                        return;
                    }
                }
            }
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find skill with %s : %s", isNumber ? "ID" : "name", query));
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            user.write(MessagePacket.system("Skill : %s (%d)", StringProvider.getSkillName(skillId), skillId));
        } else if (type.equalsIgnoreCase("quest")) {
            int questId = -1;
            if (isNumber) {
                questId = Integer.parseInt(query);
            } else {
                final List<QuestInfo> searchResult = QuestProvider.getQuestInfos().stream()
                        .filter((questInfo) -> questInfo.getQuestName().toLowerCase().contains(query.toLowerCase()) ||
                                questInfo.getQuestParent().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(QuestInfo::getQuestId))
                        .toList();
                if (!searchResult.isEmpty()) {
                    if (searchResult.size() == 1) {
                        questId = searchResult.getFirst().getQuestId();
                    } else {
                        user.write(MessagePacket.system("Results for quest name : \"%s\"", query));
                        for (QuestInfo questInfo : searchResult) {
                            user.write(MessagePacket.system("  %d : %s%s", questInfo.getQuestId(),
                                    questInfo.getQuestParent().isEmpty() ? "" : String.format("%s : ", questInfo.getQuestParent()),
                                    questInfo.getQuestName()
                            ));
                        }
                        return;
                    }
                }
            }
            final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
            if (questInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find quest with %s : %s", isNumber ? "ID" : "name", query));
                return;
            }
            final QuestInfo questInfo = questInfoResult.get();
            user.write(MessagePacket.system("Quest %d : %s%s", questInfo.getQuestId(),
                    questInfo.getQuestParent().isEmpty() ? "" : String.format("%s : ", questInfo.getQuestParent()),
                    questInfo.getQuestName()
            ));
        } else if (type.equalsIgnoreCase("commodity")) {
            if (!isNumber) {
                user.write(MessagePacket.system("Can only lookup commodity by ID"));
                return;
            }
            final int commodityId = Integer.parseInt(query);
            final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
            if (commodityResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find commodity with ID : %d", commodityId));
                return;
            }
            final Commodity commodity = commodityResult.get();
            user.write(MessagePacket.system("Commodity : %d", commodityId));
            user.write(MessagePacket.system("  itemId : %d (%s)", commodity.getItemId(), StringProvider.getItemName(commodity.getItemId())));
            user.write(MessagePacket.system("  count : %d", commodity.getCount()));
            user.write(MessagePacket.system("  price : %d", commodity.getPrice()));
            user.write(MessagePacket.system("  period : %d", commodity.getPeriod()));
            user.write(MessagePacket.system("  gender : %d", commodity.getGender()));
        } else {
            user.write(MessagePacket.system("Unknown type : %s", type));
        }
    }

    @Command("npc")
    @Arguments("npc template ID")
    public static void npc(User user, String[] args) {
        final int templateId = Integer.parseInt(args[1]);
        final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(templateId);
        if (npcTemplateResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve npc ID : %d", templateId));
            return;
        }
        final String scriptName = npcTemplateResult.get().getScript();
        if (scriptName == null || scriptName.isEmpty()) {
            user.write(MessagePacket.system("Could not find script for npc ID : %d", templateId));
            return;
        }
        user.write(MessagePacket.system("Starting script for npc ID : %d, script : %s", templateId, scriptName));
        ScriptDispatcher.startNpcScript(user, user, scriptName, templateId);
    }

    @Command({ "map", "warp" })
    @Arguments("field ID to warp to")
    public static void map(User user, String[] args) {
        final int fieldId = Integer.parseInt(args[1]);
        final String portalName;
        if (args.length > 2) {
            portalName = args[2];
        } else {
            portalName = GameConstants.DEFAULT_PORTAL_NAME;
        }
        final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve field ID : %d", fieldId));
            return;
        }
        final Field targetField = fieldResult.get();
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve portal %s for field ID : %d", portalName, fieldId));
            return;
        }
        try (var locked = user.acquire()) {
            user.warp(targetField, portalResult.get(), false, false);
        }
    }

    @Command("reactor")
    @Arguments("reactor template ID")
    public static void reactor(User user, String[] args) {
        final int templateId = Integer.parseInt(args[1]);
        final Optional<ReactorTemplate> reactorTemplateResult = ReactorProvider.getReactorTemplate(templateId);
        if (reactorTemplateResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve reactor template ID : %d", templateId));
            return;
        }
        final Field field = user.getField();
        final ReactorInfo reactorInfo = new ReactorInfo(templateId, "", user.getX(), user.getY(), false, -1);
        field.getReactorPool().addReactor(Reactor.from(reactorTemplateResult.get(), reactorInfo));
    }

    @Command("hitreactor")
    @Arguments("reactor template ID")
    public static void hitReactor(User user, String[] args) {
        final int templateId = Integer.parseInt(args[1]);
        final Field field = user.getField();
        final Optional<Reactor> reactorResult = field.getReactorPool().getByTemplateId(templateId);
        if (reactorResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve reactor with template ID : %d", templateId));
            return;
        }
        try (var lockedReactor = reactorResult.get().acquire()) {
            final Reactor reactor = lockedReactor.get();
            reactor.setState(reactor.getState() + 1);
            field.getReactorPool().hitReactor(user, reactor, 0);
        }
    }

    @Command({ "mob", "spawn" })
    @Arguments("mob template ID")
    public static void mob(User user, String[] args) {
        final int templateId = Integer.parseInt(args[1]);
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(templateId);
        if (mobTemplateResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve mob template ID : %d", templateId));
            return;
        }
        final int count;
        if (args.length > 2) {
            count = Integer.parseInt(args[2]);
        } else {
            count = 1;
        }
        final Field field = user.getField();
        final Optional<Foothold> footholdResult = field.getFootholdBelow(user.getX(), user.getY());
        for (int i = 0; i < count; i++) {
            final Mob mob = new Mob(
                    mobTemplateResult.get(),
                    null,
                    user.getX(),
                    user.getY(),
                    footholdResult.map(Foothold::getSn).orElse(user.getFoothold())
            );
            field.getMobPool().addMob(mob);
        }
    }

    @Command("togglemob")
    @Arguments("true/false")
    public static void disableMob(User user, String[] args) {
        if (args[1].equalsIgnoreCase("true")) {
            user.getField().setMobSpawn(true);
            user.write(MessagePacket.system("Enabled mob spawns"));
        } else if (args[1].equalsIgnoreCase("false")) {
            user.getField().setMobSpawn(false);
            user.write(MessagePacket.system("Disabled mob spawns"));
        }
    }

    @Command("item")
    @Arguments("item ID")
    public static void item(User user, String[] args) {
        final int itemId = Integer.parseInt(args[1]);
        final int quantity;
        if (args.length > 2) {
            quantity = Integer.parseInt(args[2]);
        } else {
            quantity = 1;
        }
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve item ID : %d", itemId));
            return;
        }
        final ItemInfo ii = itemInfoResult.get();
        final Item item = ii.createItem(user.getNextItemSn(), Math.min(quantity, ii.getSlotMax()), ItemVariationOption.NORMAL);

        // Add item
        try (var locked = user.acquire()) {
            final InventoryManager im = locked.get().getInventoryManager();
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
            if (addItemResult.isPresent()) {
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(UserLocal.effect(Effect.gainItem(item)));
            } else {
                user.write(MessagePacket.system("Failed to add item ID %d (%d) to inventory", itemId, quantity));
            }
        }
    }

    @Command("clearinventory")
    @Arguments("inventory type")
    public static void clearInventory(User user, String[] args) {
        final Optional<InventoryType> inventoryTypeResult = Arrays.stream(InventoryType.values())
                .filter((type) -> type.name().equalsIgnoreCase(args[1]))
                .findFirst();
        if (inventoryTypeResult.isEmpty()) {
            user.write(MessagePacket.system("Please specify a valid inventory type : EQUIP | CONSUME | INSTALL | ETC | CASH"));
            return;
        }
        final InventoryType inventoryType = inventoryTypeResult.get();
        try (var locked = user.acquire()) {
            final List<InventoryOperation> removeOperations = new ArrayList<>();
            final var iter = locked.get().getInventoryManager().getInventoryByType(inventoryType).getItems().entrySet().iterator();
            while (iter.hasNext()) {
                final var tuple = iter.next();
                final int position = tuple.getKey();
                removeOperations.add(InventoryOperation.delItem(inventoryType, position));
                iter.remove();
            }
            user.write(WvsContext.inventoryOperation(removeOperations, true));
            user.write(MessagePacket.system("%s inventory cleared!", inventoryType));
        }
    }

    @Command("clearlocker")
    public static void clearLocker(User user, String[] args) {
        try (var locked = user.acquire()) {
            try (var lockedAccount = user.getAccount().acquire()) {
                user.getAccount().getLocker().getCashItems().clear();
                user.write(MessagePacket.system("Locker inventory cleared!"));
            }
        }
    }

    @Command({ "meso", "money" })
    @Arguments("amount")
    public static void meso(User user, String[] args) {
        final int money = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            final InventoryManager im = locked.get().getInventoryManager();
            im.setMoney(money);
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
        }
    }

    @Command("nx")
    @Arguments("amount")
    public static void nx(User user, String[] args) {
        final int nx = Integer.parseInt(args[1]);
        try (var lockedAccount = user.getAccount().acquire()) {
            final Account account = lockedAccount.get();
            account.setNxPrepaid(nx);
            user.write(MessagePacket.system("Set NX prepaid to %d", nx));
        }
    }

    @Command("hp")
    @Arguments("new hp")
    public static void hp(User user, String[] args) {
        final int newHp = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            user.setHp(newHp);
        }
    }

    @Command("mp")
    @Arguments("new mp")
    public static void mp(User user, String[] args) {
        final int newMp = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            user.setMp(newMp);
        }
    }

    @Command("stat")
    @Arguments({ "hp/mp/str/dex/int/luk/ap/sp", "new value" })
    public static void stat(User user, String[] args) {
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
                        statMap.put(Stat.SP, (short) cs.getSp().getNonExtendSp());
                    }
                }
                default -> {
                    user.write(MessagePacket.system("Syntax : %sstat hp/mp/str/dex/int/luk/ap/sp <new value>", ServerConfig.COMMAND_PREFIX));
                    return;
                }
            }
            user.validateStat();
            user.write(WvsContext.statChanged(statMap, true));
            user.write(MessagePacket.system("Set %s to %d", stat, value));
        }
    }

    @Command("avatar")
    @Arguments("new look")
    public static void avatar(User user, String[] args) {
        final int look = Integer.parseInt(args[1]);
        if (look >= 0 && look <= GameConstants.SKIN_MAX) {
            user.getCharacterStat().setSkin((byte) look);
            user.write(WvsContext.statChanged(Stat.SKIN, user.getCharacterStat().getSkin(), false));
            user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
        } else if (look >= GameConstants.FACE_MIN && look <= GameConstants.FACE_MAX) {
            if (StringProvider.getItemName(look) == null) {
                user.write(MessagePacket.system("Tried to change face with invalid ID : %d", look));
                return;
            }
            user.getCharacterStat().setFace(look);
            user.write(WvsContext.statChanged(Stat.FACE, user.getCharacterStat().getFace(), false));
            user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
        } else if (look >= GameConstants.HAIR_MIN && look <= GameConstants.HAIR_MAX) {
            if (StringProvider.getItemName(look) == null) {
                user.write(MessagePacket.system("Tried to change hair with invalid ID : %d", look));
                return;
            }
            user.getCharacterStat().setHair(look);
            user.write(WvsContext.statChanged(Stat.HAIR, user.getCharacterStat().getHair(), false));
            user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
        } else {
            user.write(MessagePacket.system("Tried to change avatar with invalid ID : %d", look));
        }
    }

    @Command("level")
    @Arguments("new level")
    public static void level(User user, String[] args) {
        final int level = Integer.parseInt(args[1]);
        if (level < 1 || level > GameConstants.LEVEL_MAX) {
            user.write(MessagePacket.system("Could not change level to : %d", level));
            return;
        }
        try (var locked = user.acquire()) {
            final CharacterStat cs = user.getCharacterStat();
            cs.setLevel((short) level);
            user.validateStat();
            user.write(WvsContext.statChanged(Stat.LEVEL, (byte) cs.getLevel(), true));
            user.getConnectedServer().notifyUserUpdate(user);
        }
    }

    @Command("levelup")
    @Arguments("new level")
    public static void levelUp(User user, String[] args) {
        final int level = Integer.parseInt(args[1]);
        if (level <= user.getLevel() || level > GameConstants.LEVEL_MAX) {
            user.write(MessagePacket.system("Could not level up to : %d", level));
            return;
        }
        try (var locked = user.acquire()) {
            while (user.getLevel() < level) {
                user.addExp(GameConstants.getNextLevelExp(user.getLevel()) - user.getCharacterStat().getExp());
            }
        }
    }

    @Command("job")
    @Arguments("job ID")
    public static void job(User user, String[] args) {
        final int jobId = Integer.parseInt(args[1]);
        final Job job = Job.getById(jobId);
        if (job == null) {
            user.write(MessagePacket.system("Could not change to unknown job : %d", jobId));
            return;
        }
        try (var locked = user.acquire()) {
            // Set job
            user.getCharacterStat().setJob(job.getJobId());
            user.write(WvsContext.statChanged(Stat.JOB, job.getJobId(), false));
            user.getField().broadcastPacket(UserRemote.effect(user, Effect.jobChanged()), user);
            // Update skills
            final SkillManager sm = user.getSkillManager();
            final List<SkillRecord> skillRecords = new ArrayList<>();
            for (int skillRoot : JobConstants.getSkillRootFromJob(jobId)) {
                for (SkillInfo si : SkillProvider.getSkillsForJob(Job.getById(skillRoot))) {
                    if (sm.getSkill(si.getSkillId()).isPresent()) {
                        continue;
                    }
                    if (si.isInvisible()) {
                        continue;
                    }
                    final SkillRecord sr = new SkillRecord(si.getSkillId());
                    sr.setSkillLevel(0);
                    sr.setMasterLevel(SkillConstants.isSkillNeedMasterLevel(si.getSkillId()) ? 0 : si.getMaxLevel());
                    sm.addSkill(sr);
                    skillRecords.add(sr);
                }
            }
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecords, true));
            // Additional handling
            if (JobConstants.isDragonJob(jobId)) {
                final Dragon dragon = new Dragon(user.getJob());
                user.setDragon(dragon);
                user.getField().broadcastPacket(DragonPacket.dragonEnterField(user, dragon));
            } else {
                user.setDragon(null);
            }
            if (JobConstants.isWildHunterJob(jobId)) {
                user.write(WvsContext.wildHunterInfo(user.getWildHunterInfo()));
            }
            user.getConnectedServer().notifyUserUpdate(user);
        }
    }

    @Command("skill")
    @Arguments({ "skill ID", "skill level" })
    public static void skill(User user, String[] args) {
        final int skillId = Integer.parseInt(args[1]);
        final int slv = Integer.parseInt(args[2]);
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            user.write(MessagePacket.system("Could not find skill : %d", skillId));
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        final SkillRecord skillRecord = new SkillRecord(si.getSkillId());
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

    @Command("morph")
    @Arguments("morph ID")
    public static void morph(User user, String[] args) {
        final int morphId = Integer.parseInt(args[1]);
        if (SkillProvider.getMorphInfoById(morphId).isEmpty()) {
            user.write(MessagePacket.system("Could not resolve morph info for morph ID : %d", morphId));
            return;
        }
        try (var locked = user.acquire()) {
            final SecondaryStat ss = locked.get().getSecondaryStat();
            final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(Set.of(CharacterTemporaryStat.Morph), CharacterTemporaryStat.FLAG_SIZE);
            ss.getTemporaryStats().put(CharacterTemporaryStat.Morph, TemporaryStatOption.of(morphId, -5300000, 0));
            user.write(WvsContext.temporaryStatSet(ss, flag));
            user.getField().broadcastPacket(UserRemote.temporaryStatSet(user, ss, flag));
        }
    }

    @Command("ride")
    @Arguments("vehicle ID")
    public static void ride(User user, String[] args) {
        final int vehicleId = Integer.parseInt(args[1]);
        if (ItemProvider.getItemInfo(vehicleId).isEmpty()) {
            user.write(MessagePacket.system("Could not resolve item info for vehicle ID : %d", vehicleId));
            return;
        }
        try (var locked = user.acquire()) {
            final SecondaryStat ss = locked.get().getSecondaryStat();
            final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(Set.of(CharacterTemporaryStat.RideVehicle), CharacterTemporaryStat.FLAG_SIZE);
            ss.getTemporaryStats().put(CharacterTemporaryStat.RideVehicle, TwoStateTemporaryStat.ofTwoState(CharacterTemporaryStat.RideVehicle, vehicleId, Beginner.MONSTER_RIDER, 0));
            user.write(WvsContext.temporaryStatSet(ss, flag));
            user.getField().broadcastPacket(UserRemote.temporaryStatSet(user, ss, flag));
        }
    }

    @Command("clearquest")
    @Arguments("quest ID")
    public static void clearQuest(User user, String[] args) {
        final int questId = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(questId);
            if (questRecordResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find quest record : %d", questId));
                return;
            }
            final QuestRecord qr = questRecordResult.get();
            qr.setState(QuestState.NONE);
            user.write(MessagePacket.questRecord(qr));
            user.validateStat();
        }
    }

    @Command("startquest")
    @Arguments("quest ID")
    public static void startQuest(User user, String[] args) {
        final int questId = Integer.parseInt(args[1]);
        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            user.write(MessagePacket.system("Could not find quest : %d", questId));
            return;
        }
        try (var locked = user.acquire()) {
            final QuestRecord qr = user.getQuestManager().forceStartQuest(questId);
            user.write(MessagePacket.questRecord(qr));
            user.validateStat();
        }
    }

    @Command("completequest")
    @Arguments("quest ID")
    public static void completeQuest(User user, String[] args) {
        final int questId = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            final QuestRecord qr = user.getQuestManager().forceCompleteQuest(questId);
            user.write(MessagePacket.questRecord(qr));
            user.validateStat();
        }
    }

    @Command({ "questex", "qr" })
    @Arguments("quest ID")
    public static void questex(User user, String[] args) {
        final int questId = Integer.parseInt(args[1]);
        final String newValue;
        if (args.length > 2) {
            newValue = args[2];
        } else {
            newValue = null;
        }
        try (var locked = user.acquire()) {
            if (newValue == null) {
                final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(questId);
                final String value = questRecordResult.map(QuestRecord::getValue).orElse("");
                user.write(MessagePacket.system("Get QR value for quest ID %d : %s", questId, value));
            } else {
                final QuestRecord qr = user.getQuestManager().setQuestInfoEx(questId, newValue);
                user.write(MessagePacket.questRecord(qr));
                user.validateStat();
                user.write(MessagePacket.system("Set QR value for quest ID %d : %s", questId, newValue));
            }
        }
    }

    @Command("killmobs")
    public static void killMobs(User user, String[] args) {
        user.getField().getMobPool().forEach((mob) -> {
            try (var lockedMob = mob.acquire()) {
                if (mob.getHp() > 0) {
                    mob.damage(user, mob.getMaxHp(), 0, MobLeaveType.ETC);
                }
            }
        });
    }

    @Command("mobskill")
    @Arguments({ "skill ID", "skill level" })
    public static void mobskill(User user, String[] args) {
        final int skillId = Integer.parseInt(args[1]);
        final int slv = Integer.parseInt(args[2]);
        final MobSkillType skillType = MobSkillType.getByValue(skillId);
        if (skillType == null) {
            user.write(MessagePacket.system("Could not resolve mob skill %d", skillId));
            return;
        }
        final CharacterTemporaryStat cts = skillType.getCharacterTemporaryStat();
        if (cts == null) {
            user.write(MessagePacket.system("Could not resolve mob skill %s does not apply a CTS", skillType));
            return;
        }
        // Apply mob skill
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getMobSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            user.write(MessagePacket.system("Could not resolve mob skill info %d", skillId));
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        try (var locked = user.acquire()) {
            locked.get().setTemporaryStat(cts, TemporaryStatOption.ofMobSkill(Math.max(si.getValue(SkillStat.x, slv), 1), skillId, slv, si.getDuration(slv)));
        }
    }

    @Command("combo")
    @Arguments("value")
    public static void combo(User user, String[] args) {
        final int combo = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            user.setTemporaryStat(CharacterTemporaryStat.ComboAbilityBuff, TemporaryStatOption.of(combo, Aran.COMBO_ABILITY, 0));
            user.write(UserLocal.incCombo(combo));
        }
    }

    @Command({ "battleship", "bship" })
    public static void battleship(User user, String[] args) {
        try (var locked = user.acquire()) {
            user.write(MessagePacket.system("Battleship HP : %d", Pirate.getBattleshipDurability(user)));
        }
    }

    @Command("jaguar")
    @Arguments("index")
    public static void jaguar(User user, String[] args) {
        final int index = Integer.parseInt(args[1]);
        try (var locked = user.acquire()) {
            user.getWildHunterInfo().setRidingType(index);
            user.write(WvsContext.wildHunterInfo(user.getWildHunterInfo()));
        }
    }

    @Command("cd")
    public static void cd(User user, String[] args) {
        try (var locked = user.acquire()) {
            final var iter = locked.get().getSkillManager().getSkillCooltimes().keySet().iterator();
            while (iter.hasNext()) {
                final int skillId = iter.next();
                user.write(UserLocal.skillCooltimeSet(skillId, 0));
                iter.remove();
            }
        }
    }

    @Command("max")
    public static void max(User user, String[] args) {
        try (var locked = user.acquire()) {
            // Set stats
            final CharacterStat cs = user.getCharacterStat();
            cs.setLevel((short) 200);
//            cs.setBaseStr((short) 10000);
//            cs.setBaseDex((short) 10000);
//            cs.setBaseInt((short) 10000);
//            cs.setBaseLuk((short) 10000);
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
                    Stat.MHP, cs.getMaxHp(),
                    Stat.MMP, cs.getMaxMp(),
                    Stat.EXP, cs.getExp()
            ), true));

            // Reset skills
            final SkillManager sm = user.getSkillManager();
            final List<SkillRecord> removedRecords = new ArrayList<>();
            for (SkillRecord skillRecord : sm.getSkillRecords()) {
                if (JobConstants.isBeginnerJob(SkillConstants.getSkillRoot(skillRecord.getSkillId()))) {
                    continue;
                }
                skillRecord.setSkillLevel(0);
                skillRecord.setMasterLevel(0);
                removedRecords.add(skillRecord);
                sm.removeSkill(skillRecord.getSkillId());
            }
            user.write(WvsContext.changeSkillRecordResult(removedRecords, true));

            // Add skills
            final List<SkillRecord> skillRecords = new ArrayList<>();
            for (int skillRoot : JobConstants.getSkillRootFromJob(user.getJob())) {
                if (JobConstants.isBeginnerJob(skillRoot)) {
                    continue;
                }
                final Job job = Job.getById(skillRoot);
                for (SkillInfo si : SkillProvider.getSkillsForJob(job)) {
                    final SkillRecord skillRecord = new SkillRecord(si.getSkillId());
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

    @Command("help")
    public static void help(User user, String[] args) {
        if (args.length == 1) {
            for (Class<?> clazz : new Class[]{ AdminCommands.class }) {
                user.write(MessagePacket.system("Admin Commands :"));
                for (Method method : clazz.getDeclaredMethods()) {
                    if (!method.isAnnotationPresent(Command.class)) {
                        continue;
                    }
                    user.write(MessagePacket.system("%s", CommandProcessor.getHelpString(method)));
                }
            }
        } else {
            final String commandName = args[1].toLowerCase();
            final Optional<Method> commandResult = CommandProcessor.getCommand(commandName);
            if (commandResult.isEmpty()) {
                user.write(MessagePacket.system("Unknown command : %s", commandName));
                return;
            }
            final Method method = commandResult.get();
            user.write(MessagePacket.system("Syntax : %s", CommandProcessor.getHelpString(method)));
        }
    }

    @Command("reloaddrops")
    public static void reloadDrops(User user, String[] args) {
        RewardProvider.initialize();
    }

    @Command("reloadshops")
    public static void reloadShops(User user, String[] args) {
        ShopProvider.initialize();
    }

    @Command({ "reloadcashshop", "reloadcs" })
    public static void reloadCashShop(User user, String[] args) {
        CashShop.initialize();
    }
}
