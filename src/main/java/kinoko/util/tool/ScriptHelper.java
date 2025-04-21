package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.MapInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

final class ScriptHelper {
    private static final Logger log = LogManager.getLogger(ScriptHelper.class);
    private static final Map<String, List<String>> collectedComments = new HashMap<>(); // script name -> comments

    public static void main(String[] args) throws IOException {
        StringProvider.initialize();
        MapProvider.initialize();
        NpcProvider.initialize();
        ItemProvider.initialize();
        QuestProvider.initialize();
        ReactorProvider.initialize();

        // Load scripts
        final Pattern scriptPattern = Pattern.compile("(\\ +)@Script\\(\\\"(.+)\\\"\\)");
        final Map<Path, Map<Integer, String>> separators = new HashMap<>();
        final Map<Path, List<String>> scriptMapping = new HashMap<>();
        final Map<String, List<String>> scriptMethods = new HashMap<>();
        try (final Stream<Path> stream = Files.walk(Path.of("src/main/java/kinoko/script"))) {
            for (Path path : stream.toList()) {
                if (!Files.isRegularFile(path) || !path.toString().endsWith(".java")) {
                    continue;
                }
                final List<String> lines = Files.readAllLines(path);
                final String content = Files.readString(path);
                final Matcher m = scriptPattern.matcher(content);
                while (m.find()) {
                    final String space = m.group(1);
                    final String scriptName = m.group(2);
                    final int start = lines.indexOf(m.group(0));
                    final int count = lines.subList(start, lines.size()).indexOf(space + "}");

                    final List<String> scriptBody = new ArrayList<>();
                    for (int i = 0; i <= count; i++) {
                        scriptBody.add(lines.get(start + i));
                    }
                    scriptMethods.put(scriptName, scriptBody);
                    // Script ordering
                    final List<String> scriptList = scriptMapping.computeIfAbsent(path, (key) -> new ArrayList<>());
                    scriptList.add(scriptName);
                    // Handle separator
                    final String separator = lines.get(start - 2);
                    if (separator.trim().startsWith("//") && separator.endsWith("-----")) {
                        separators.computeIfAbsent(path, (key) -> new HashMap<>()).put(scriptList.size() - 1, separator);
                    }
                }
            }
        }


        // Collect field enter script comments
        final List<MapInfo> mapInfoList = MapProvider.getMapInfos().stream()
                .sorted(Comparator.comparingInt(MapInfo::getMapId)).toList();
        final Map<String, Map<Integer, List<String>>> mapComments = new HashMap<>();
        final Function<Integer, List<String>> mapperFunction = (mapId) -> {
            final List<String> comments = new ArrayList<>();
            comments.add(String.format("// %s (%d)", resolveMapName(mapId), mapId));
            return comments;
        };
        for (MapInfo mapInfo : mapInfoList) {
            final int mapId = mapInfo.getMapId();
            final String firstUserEnter = mapInfo.getOnFirstUserEnter();
            alertScript("Map FUE", mapId, firstUserEnter);
            if (firstUserEnter != null && !firstUserEnter.isEmpty() && scriptMethods.containsKey(firstUserEnter)) {
                final Map<Integer, List<String>> commentMap = mapComments.computeIfAbsent(firstUserEnter, (key) -> new HashMap<>());
                final List<String> commentList = commentMap.computeIfAbsent(mapId, mapperFunction);
                // commentList.add("//   onFirstUserEnter");
            }
            final String userEnter = mapInfo.getOnUserEnter();
            alertScript("Map UE", mapId, userEnter);
            if (userEnter != null && !userEnter.isEmpty() && scriptMethods.containsKey(userEnter)) {
                final Map<Integer, List<String>> commentMap = mapComments.computeIfAbsent(userEnter, (key) -> new HashMap<>());
                final List<String> commentList = commentMap.computeIfAbsent(mapId, mapperFunction);
                // commentList.add("//   onUserEnter");
            }
        }
        // Collect portal script comments
        for (MapInfo mapInfo : mapInfoList) {
            final int mapId = mapInfo.getMapId();
            if (MapProvider.getMapLink(mapId).isPresent()) {
                // skip linked maps
                continue;
            }
            for (PortalInfo portalInfo : mapInfo.getPortalInfos().stream().sorted(Comparator.comparing(PortalInfo::getPortalName)).toList()) {
                final String scriptName = portalInfo.getScript();
                alertScript("Portal " + portalInfo.getPortalName(), mapId, scriptName);
                if (scriptName == null || scriptName.isEmpty() || !scriptMethods.containsKey(scriptName)) {
                    continue;
                }
                final Map<Integer, List<String>> commentMap = mapComments.computeIfAbsent(scriptName, (key) -> new HashMap<>());
                final List<String> commentList = commentMap.computeIfAbsent(mapId, mapperFunction);
                commentList.add(String.format("//   %s (%d, %d)", portalInfo.getPortalName(), portalInfo.getX(), portalInfo.getY()));
            }
        }
        // Process map comments
        for (var scriptEntry : mapComments.entrySet()) {
            final String scriptName = scriptEntry.getKey();
            final List<String> comments = new ArrayList<>();
            for (var mapEntry : scriptEntry.getValue().entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).toList()) {
                comments.addAll(mapEntry.getValue());
            }
            addComments(scriptName, comments);
        }


        // Process npc scripts
        for (NpcTemplate npcTemplate : NpcProvider.getNpcTemplates().stream().sorted(Comparator.comparingInt(NpcTemplate::getId)).toList()) {
            final int npcId = npcTemplate.getId();
            final String scriptName = npcTemplate.getScript();
            alertScript("Npc", npcId, scriptName);
            if (scriptName == null || scriptName.isEmpty() || !scriptMethods.containsKey(scriptName)) {
                continue;
            }
            final List<String> comments = new ArrayList<>();
            comments.add(String.format("// %s (%d)", StringProvider.getNpcName(npcId), npcId));
            final List<MapInfo> npcFields = MapProvider.getMapInfos().stream()
                    .filter((mapInfo) -> mapInfo.getLifeInfos().stream().anyMatch((lifeInfo) -> lifeInfo.getTemplateId() == npcId))
                    .sorted(Comparator.comparingInt(MapInfo::getMapId))
                    .toList();
            for (MapInfo mapInfo : npcFields) {
                final int mapId = mapInfo.getMapId();
                comments.add(String.format("//   %s (%d)", resolveMapName(mapId), mapId));
            }
            addComments(scriptName, comments);
        }


        // Process item scripts
        for (ItemInfo itemInfo : ItemProvider.getItemInfos()) {
            final int itemId = itemInfo.getItemId();
            final String scriptName = itemInfo.getScript();
            alertScript("Item", itemId, scriptName);
            if (scriptName == null || scriptName.isEmpty() || !scriptMethods.containsKey(scriptName)) {
                continue;
            }
            addComments(scriptName, List.of(
                    String.format("// %s (%d)", StringProvider.getItemName(itemId), itemId)
            ));
        }


        // Process quest scripts
        try (final WzArchiveReader reader = WzArchiveReader.build(QuestProvider.QUEST_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            final WzImage infoImage = wzPackage.getDirectory().getImages().get("QuestInfo.img");
            final WzImage checkImage = wzPackage.getDirectory().getImages().get("Check.img");
            for (var entry : checkImage.getProperty().getItems().entrySet()) {
                final int questId = WzProvider.getInteger(entry.getKey());
                if (!(entry.getValue() instanceof WzListProperty questCheck) ||
                        !(questCheck.get("0") instanceof WzListProperty startCheck) ||
                        !(questCheck.get("1") instanceof WzListProperty endCheck)) {
                    throw new ProviderError("Could not resolve quest check");
                }
                if (!(infoImage.getProperty().get(entry.getKey()) instanceof WzListProperty questInfo)) {
                    throw new ProviderError("Could not resolve quest check");
                }
                final String questName = WzProvider.getString(questInfo.get("name"));
                // Start script
                final String startScript = WzProvider.getString(startCheck.get("startscript"), "");
                final int startNpc = WzProvider.getInteger(startCheck.get("npc"), 0);
                if (startScript != null && !startScript.isEmpty() && scriptMethods.containsKey(startScript)) {
                    final List<String> comments = new ArrayList<>();
                    comments.add(String.format("// %s (%d - start)", questName, questId));
                    if (startNpc != 0) {
                        // comments.add(String.format("//   %s (%d)", StringProvider.getNpcName(startNpc), startNpc));
                    }
                    addComments(startScript, comments);
                }
                // End script
                final String endScript = WzProvider.getString(endCheck.get("endscript"), "");
                final int endNpc = WzProvider.getInteger(endCheck.get("npc"), 0);
                if (endScript != null && !endScript.isEmpty() && scriptMethods.containsKey(endScript)) {
                    final List<String> comments = new ArrayList<>();
                    comments.add(String.format("// %s (%d - end)", questName, questId));
                    if (endNpc != 0) {
                        // comments.add(String.format("//   %s (%d)", StringProvider.getNpcName(endNpc), endNpc));
                    }
                    addComments(endScript, comments);
                }
            }
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Quest.wz", e);
        }


        // Process reactor scripts
        for (ReactorTemplate reactorTemplate : ReactorProvider.getReactorTemplates().stream().sorted(Comparator.comparingInt(ReactorTemplate::getId)).toList()) {
            final int reactorId = reactorTemplate.getId();
            final String scriptName = reactorTemplate.getAction();
            alertScript("Reactor", reactorId, scriptName);
            if (scriptName == null || scriptName.isEmpty() || !scriptMethods.containsKey(scriptName)) {
                continue;
            }
            final List<String> comments = new ArrayList<>();
            comments.add(String.format("// %s (%d)", scriptName, reactorId));
            final List<MapInfo> npcFields = MapProvider.getMapInfos().stream()
                    .filter((mapInfo) -> mapInfo.getReactorInfos().stream().anyMatch((reactorInfo) -> reactorInfo.getTemplateId() == reactorId))
                    .sorted(Comparator.comparingInt(MapInfo::getMapId))
                    .toList();
            for (MapInfo mapInfo : npcFields) {
                final int mapId = mapInfo.getMapId();
                comments.add(String.format("//   %s (%d)", resolveMapName(mapId), mapId));
            }
            addComments(scriptName, comments);
        }


        // Update script files
        try (final Stream<Path> stream = Files.walk(Path.of("src/main/java/kinoko/script"))) {
            for (Path path : stream.toList()) {
                if (!Files.isRegularFile(path) || !path.toString().endsWith(".java")) {
                    continue;
                }
                final List<String> lines = Files.readAllLines(path);
                final String content = Files.readString(path);
                final Matcher m = scriptPattern.matcher(content);
                int start = -1;
                while (m.find()) {
                    start = lines.indexOf(m.group(0));
                    break;
                }
                if (start < 0) {
                    continue;
                }
                // Rewrite script file
                Files.write(path, lines.subList(0, start), StandardOpenOption.TRUNCATE_EXISTING);
                final Map<Integer, String> sep = separators.getOrDefault(path, Map.of());
                final List<String> scriptNames = scriptMapping.get(path);
                for (int i = 0; i < scriptNames.size(); i++) {
                    // Separator
                    if (sep.containsKey(i)) {
                        // System.out.printf("%d %s\n", i, sep.get(i));
                        Files.writeString(path, String.format("\n%s\n\n", sep.get(i)), StandardOpenOption.APPEND);
                    }
                    // Script content
                    final String scriptName = scriptNames.get(i);
                    final List<String> scriptBody = scriptMethods.get(scriptName);
                    int bodyStart;
                    for (bodyStart = 2; bodyStart < scriptBody.size(); bodyStart++) {
                        if (!scriptBody.get(bodyStart).trim().startsWith("//")) {
                            break;
                        }
                    }
                    Files.write(path, scriptBody.subList(0, 2), StandardOpenOption.APPEND);
                    if (bodyStart != scriptBody.size() - 1) {
                        for (String comment : collectedComments.getOrDefault(scriptName, List.of())) {
                            Files.writeString(path, String.format("        %s\n", comment), StandardOpenOption.APPEND);
                        }
                    } else {
                        // log.info("Blank script method : {}", scriptName);
                    }
                    Files.write(path, scriptBody.subList(bodyStart, scriptBody.size()), StandardOpenOption.APPEND);
                    if (i != scriptNames.size() - 1) {
                        Files.writeString(path, "\n", StandardOpenOption.APPEND);
                    }
                }
                Files.write(path, "}\n".getBytes(), StandardOpenOption.APPEND);
            }
        }
    }

    private static void addComments(String scriptName, List<String> comments) throws IOException {
        // Check if script has been processed
        final List<String> combinedComments = new ArrayList<>();
        if (collectedComments.containsKey(scriptName)) {
            final List<String> existingComments = collectedComments.get(scriptName);
            if (existingComments.contains(comments.get(0))) {
                return;
            }
            // Add existing comments
            // log.warn("Existing comments for path : {}", path);
            combinedComments.addAll(existingComments);
        }
        combinedComments.addAll(comments);
        collectedComments.put(scriptName, combinedComments);
    }

    private static String resolveMapName(int mapId) {
        final String name = StringProvider.getMapName(mapId);
        if (name != null) {
            return name;
        }
        final Optional<Integer> linkResult = MapProvider.getMapLink(mapId);
        if (linkResult.isEmpty()) {
            // log.warn("Could not resolve name for map ID : {}", mapId);
            return null;
        }
        return StringProvider.getMapName(linkResult.get());
    }

    private static void alertScript(String scriptType, int source, String scriptName) {
        if (scriptName != null && scriptName.toLowerCase().startsWith("party_")) {
            log.info("{} script for {} : {}", scriptType, source, scriptName);
        }
    }
}
