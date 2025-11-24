package kinoko.server.command.tester;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.MapInfo;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.cashshop.CashShop;
import kinoko.server.cashshop.Commodity;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.util.Util;
import kinoko.world.user.User;
import kinoko.provider.map.PortalInfo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kinoko.provider.skill.SkillStringInfo;


/**
 * Fully detailed SearchCommand with helpers for item/map/mob/npc/skill/quest/commodity.
 */
public final class SearchCommand {
    /**
     * Searches for items, maps, mobs, NPCs, skills, quests, or commodities.
     * Usage: !search <item/map/mob/npc/skill/quest/commodity> <id or query>
     *        !lookup <item/map/mob/npc/skill/quest/commodity> <id or query>
     *        !find <item/map/mob/npc/skill/quest/commodity> <id or query>
     */
    @Command({ "search", "find", "lookup"})
    @Arguments({ "item/map/mob/npc/skill/quest/commodity", "id or query" })
    public static void find(User user, String[] args) {
        if (args.length < 2) {
            user.systemMessage("Usage: !find <type> <id or query>");
            return;
        }

        final String type = args[1].toLowerCase();
        final String query = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        final boolean isNumber = Util.isInteger(query);

        try {
            switch (type) {
                case "item" -> findItem(user, query, isNumber);
                case "map" -> findMap(user, query, isNumber);
                case "mob" -> findMob(user, query, isNumber);
                case "npc" -> findNpc(user, query, isNumber);
                case "skill" -> findSkill(user, query, isNumber);
                case "quest" -> findQuest(user, query, isNumber);
                case "commodity" -> findCommodity(user, query, isNumber);
                default -> user.systemMessage("Unknown type: %s", type);
            }
        } catch (NumberFormatException e) {
            user.systemMessage("Invalid number: %s", query);
        }
    }

    private static void findItem(User user, String query, boolean isNumber) {
        int itemId = -1;
        if (isNumber) {
            itemId = Integer.parseInt(query);
        } else {
            List<Map.Entry<Integer, String>> results = StringProvider.getItemNames().entrySet().stream()
                    .filter(entry -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Map.Entry.comparingByKey())
                    .toList();
            if (results.isEmpty()) {
                user.systemMessage("No item found for name: %s", query);
                return;
            } else if (results.size() == 1) {
                itemId = results.get(0).getKey();
            } else {
                user.systemMessage("Results for item name: \"%s\"", query);
                results.forEach(entry -> user.systemMessage("  %d : %s", entry.getKey(), entry.getValue()));
                return;
            }
        }

        Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            user.systemMessage("Could not find item with %s: %s", isNumber ? "ID" : "name", query);
            return;
        }

        ItemInfo ii = itemInfoResult.get();
        user.systemMessage("Item: %s (%d)", StringProvider.getItemName(itemId), itemId);
        if (!ii.getItemInfos().isEmpty()) {
            user.systemMessage("  info:");
            ii.getItemInfos().forEach((key, value) -> user.systemMessage("    %s : %s", key.name(), value));
        }
        if (!ii.getItemSpecs().isEmpty()) {
            user.systemMessage("  spec:");
            ii.getItemSpecs().forEach((key, value) -> user.systemMessage("    %s : %s", key.name(), value));
        }
    }

    private static void findMap(User user, String query, boolean isNumber) {
        int mapId = -1;
        if (isNumber) {
            mapId = Integer.parseInt(query);
        } else {
            List<Map.Entry<Integer, String>> results = StringProvider.getMapNames().entrySet().stream()
                    .filter(entry -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Map.Entry.comparingByKey())
                    .toList();
            if (results.isEmpty()) {
                user.systemMessage("No map found for name: %s", query);
                return;
            } else if (results.size() == 1) {
                mapId = results.get(0).getKey();
            } else {
                user.systemMessage("Results for map name: \"%s\"", query);
                results.forEach(entry -> user.systemMessage("  %d : %s", entry.getKey(), entry.getValue()));
                return;
            }
        }

        final int mapIdFinal = mapId; // make a final copy for lambdas

        Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapIdFinal);
        if (mapInfoResult.isEmpty()) {
            user.systemMessage("Could not find map with %s: %s", isNumber ? "ID" : "name", query);
            return;
        }

        MapInfo mapInfo = mapInfoResult.get();

        List<MapInfo> connectedMaps = MapProvider.getMapInfos().stream()
                .filter(m -> m.getPortalInfos().stream().anyMatch(p -> p.getDestinationFieldId() == mapIdFinal))
                .sorted(Comparator.comparingInt(MapInfo::getMapId))
                .toList();

        user.systemMessage("Map: %s (%d)", StringProvider.getMapName(mapIdFinal), mapIdFinal);
        user.systemMessage("  type: %s", mapInfo.getFieldType());
        user.systemMessage("  returnMap: %d", mapInfo.getReturnMap());
        user.systemMessage("  forcedReturn: %d", mapInfo.getForcedReturn());
        user.systemMessage("  onFirstUserEnter: %s", mapInfo.getOnFirstUserEnter());
        user.systemMessage("  onUserEnter: %s", mapInfo.getOnUserEnter());

        if (!mapInfo.getPortalInfos().isEmpty()) {
            user.systemMessage("  portals:");
            mapInfo.getPortalInfos().stream()
                    .sorted(Comparator.comparingInt(PortalInfo::getPortalId))
                    .forEach(p -> user.systemMessage("    %s (%d, %d)", p.getPortalName(), p.getX(), p.getY()));
        }

        if (!connectedMaps.isEmpty()) {
            user.systemMessage("  connectedMaps:");
            connectedMaps.forEach(m -> user.systemMessage("    %s (%d)", StringProvider.getMapName(m.getMapId()), m.getMapId()));
        }
    }


    private static void findMob(User user, String query, boolean isNumber) {
        int mobId = isNumber ? Integer.parseInt(query) : -1;
        if (!isNumber) {
            List<Map.Entry<Integer, String>> results = StringProvider.getMobNames().entrySet().stream()
                    .filter(entry -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Map.Entry.comparingByKey())
                    .toList();
            if (results.isEmpty()) {
                user.systemMessage("No mob found for name: %s", query);
                return;
            } else if (results.size() == 1) {
                mobId = results.get(0).getKey();
            } else {
                user.systemMessage("Results for mob name: \"%s\"", query);
                results.forEach(entry -> user.systemMessage("  %d : %s", entry.getKey(), entry.getValue()));
                return;
            }
        }

        Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(mobId);
        if (mobTemplateResult.isEmpty()) {
            user.systemMessage("Could not find mob with %s: %s", isNumber ? "ID" : "name", query);
            return;
        }

        MobTemplate mob = mobTemplateResult.get();
        user.systemMessage("Mob: %s (%d)", StringProvider.getMobName(mobId), mobId);
        user.systemMessage("  level: %d", mob.getLevel());
    }

    private static void findNpc(User user, String query, boolean isNumber) {
        int npcId = isNumber ? Integer.parseInt(query) : -1;
        if (!isNumber) {
            List<Map.Entry<Integer, String>> results = StringProvider.getNpcNames().entrySet().stream()
                    .filter(entry -> entry.getValue().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Map.Entry.comparingByKey())
                    .toList();
            if (results.isEmpty()) {
                user.systemMessage("No npc found for name: %s", query);
                return;
            } else if (results.size() == 1) {
                npcId = results.get(0).getKey();
            } else {
                user.systemMessage("Results for npc name: \"%s\"", query);
                results.forEach(entry -> user.systemMessage("  %d : %s", entry.getKey(), entry.getValue()));
                return;
            }
        }

        Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(npcId);
        if (npcTemplateResult.isEmpty()) {
            user.systemMessage("Could not find npc with %s: %s", isNumber ? "ID" : "name", query);
            return;
        }

        NpcTemplate npc = npcTemplateResult.get();
        List<MapInfo> npcFields = MapProvider.getMapInfos().stream()
                .filter(m -> m.getLifeInfos().stream().anyMatch(l -> l.getTemplateId() == npc.getId()))
                .sorted(Comparator.comparingInt(MapInfo::getMapId))
                .toList();

        user.systemMessage("Npc: %s (%d)", StringProvider.getNpcName(npcId), npcId);
        user.systemMessage("  script: %s", npc.getScript());
        npcFields.forEach(f -> user.systemMessage("  field: %s (%d)", StringProvider.getMapName(f.getMapId()), f.getMapId()));
    }

    private static void findSkill(User user, String query, boolean isNumber) {
        int skillId = isNumber ? Integer.parseInt(query) : -1;
        if (!isNumber) {
            List<Map.Entry<Integer, SkillStringInfo>> results = StringProvider.getSkillStrings().entrySet().stream()
                    .filter(e -> e.getValue().getName().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Map.Entry.comparingByKey())
                    .toList();
            if (results.isEmpty()) {
                user.systemMessage("No skill found for name: %s", query);
                return;
            } else if (results.size() == 1) {
                skillId = results.get(0).getKey();
            } else {
                user.systemMessage("Results for skill name: \"%s\"", query);
                results.forEach(e -> user.systemMessage("  %d : %s", e.getKey(), e.getValue().getName()));
                return;
            }
        }

        Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            user.systemMessage("Could not find skill with %s: %s", isNumber ? "ID" : "name", query);
            return;
        }

        user.systemMessage("Skill: %s (%d)", StringProvider.getSkillName(skillId), skillId);
    }

    private static void findQuest(User user, String query, boolean isNumber) {
        int questId = isNumber ? Integer.parseInt(query) : -1;
        if (!isNumber) {
            List<QuestInfo> results = QuestProvider.getQuestInfos().stream()
                    .filter(q -> q.getQuestName().toLowerCase().contains(query.toLowerCase()) ||
                            q.getQuestParent().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Comparator.comparingInt(QuestInfo::getQuestId))
                    .toList();
            if (results.isEmpty()) {
                user.systemMessage("No quest found for name: %s", query);
                return;
            } else if (results.size() == 1) {
                questId = results.get(0).getQuestId();
            } else {
                user.systemMessage("Results for quest name: \"%s\"", query);
                results.forEach(q -> user.systemMessage("  %d : %s%s",
                        q.getQuestId(),
                        q.getQuestParent().isEmpty() ? "" : q.getQuestParent() + " : ",
                        q.getQuestName()));
                return;
            }
        }

        Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            user.systemMessage("Could not find quest with %s: %s", isNumber ? "ID" : "name", query);
            return;
        }

        QuestInfo quest = questInfoResult.get();
        user.systemMessage("Quest %d : %s%s", quest.getQuestId(),
                quest.getQuestParent().isEmpty() ? "" : quest.getQuestParent() + " : ",
                quest.getQuestName());
    }

    private static void findCommodity(User user, String query, boolean isNumber) {
        if (!isNumber) {
            user.systemMessage("Can only lookup commodity by ID");
            return;
        }

        int commodityId = Integer.parseInt(query);
        Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
        if (commodityResult.isEmpty()) {
            user.systemMessage("Could not find commodity with ID: %d", commodityId);
            return;
        }

        Commodity commodity = commodityResult.get();
        user.systemMessage("Commodity: %d", commodityId);
        user.systemMessage("  itemId : %d (%s)", commodity.getItemId(), StringProvider.getItemName(commodity.getItemId()));
        user.systemMessage("  count : %d", commodity.getCount());
        user.systemMessage("  price : %d", commodity.getPrice());
        user.systemMessage("  period : %d", commodity.getPeriod());
        user.systemMessage("  gender : %d", commodity.getGender());
    }
}
