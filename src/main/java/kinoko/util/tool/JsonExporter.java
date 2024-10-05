package kinoko.util.tool;

import kinoko.provider.SkillProvider;
import kinoko.provider.StringProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStringInfo;
import kinoko.util.Rect;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static kinoko.world.job.Job.*;

final class JsonExporter {
    public static final String JSON_DIRECTORY = "json";
    public static final String CLASSES_DIR = "classes";
    public static final String FILE_EXTENSION = ".json";

    public static final Map<String, List<Job>> CLASS_TYPES = Map.ofEntries(
            // EXPLORERS
            Map.entry("Hero", List.of(BEGINNER, WARRIOR, FIGHTER, CRUSADER, HERO)),
            Map.entry("Paladin", List.of(BEGINNER, WARRIOR, PAGE, WHITE_KNIGHT, PALADIN)),
            Map.entry("Dark Knight", List.of(BEGINNER, WARRIOR, SPEARMAN, DRAGON_KNIGHT, DARK_KNIGHT)),
            Map.entry("Arch Mage (F/P)", List.of(BEGINNER, MAGICIAN, WIZARD_FP, MAGE_FP, ARCH_MAGE_FP)),
            Map.entry("Arch Mage (I/L)", List.of(BEGINNER, MAGICIAN, WIZARD_IL, MAGE_IL, ARCH_MAGE_IL)),
            Map.entry("Bishop", List.of(BEGINNER, MAGICIAN, CLERIC, PRIEST, BISHOP)),
            Map.entry("Bowmaster", List.of(BEGINNER, ARCHER, HUNTER, RANGER, BOWMASTER)),
            Map.entry("Marksman", List.of(BEGINNER, ARCHER, CROSSBOWMAN, SNIPER, MARKSMAN)),
            Map.entry("Night Lord", List.of(BEGINNER, ROGUE, ASSASSIN, HERMIT, NIGHT_LORD)),
            Map.entry("Shadower", List.of(BEGINNER, ROGUE, BANDIT, CHIEF_BANDIT, SHADOWER)),
            Map.entry("Dual Blade", List.of(BEGINNER, ROGUE, BLADE_RECRUIT, BLADE_ACOLYTE, BLADE_SPECIALIST, BLADE_LORD, BLADE_MASTER)),
            Map.entry("Buccaneer", List.of(BEGINNER, PIRATE, BRAWLER, MARAUDER, BUCCANEER)),
            Map.entry("Corsair", List.of(BEGINNER, PIRATE, GUNSLINGER, OUTLAW, CORSAIR)),
            // CYGNUS
            Map.entry("Dawn Warrior", List.of(NOBLESSE, DAWN_WARRIOR_1, DAWN_WARRIOR_2, DAWN_WARRIOR_3)),
            Map.entry("Blaze Wizard", List.of(NOBLESSE, BLAZE_WIZARD_1, BLAZE_WIZARD_2, BLAZE_WIZARD_3)),
            Map.entry("Wind Archer", List.of(NOBLESSE, WIND_ARCHER_1, WIND_ARCHER_2, WIND_ARCHER_3)),
            Map.entry("Night Walker", List.of(NOBLESSE, NIGHT_WALKER_1, NIGHT_WALKER_2, NIGHT_WALKER_3)),
            Map.entry("Thunder Breaker", List.of(NOBLESSE, THUNDER_BREAKER_1, THUNDER_BREAKER_2, THUNDER_BREAKER_3)),
            // LEGEND
            Map.entry("Aran", List.of(ARAN_BEGINNER, ARAN_1, ARAN_2, ARAN_3, ARAN_4)),
            Map.entry("Evan", List.of(EVAN_BEGINNER, EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10)),
            // RESISTANCE
            Map.entry("Battle Mage", List.of(CITIZEN, BATTLE_MAGE_1, BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4)),
            Map.entry("Wild Hunter", List.of(CITIZEN, WILD_HUNTER_1, WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4)),
            Map.entry("Mechanic", List.of(CITIZEN, MECHANIC_1, MECHANIC_2, MECHANIC_3, MECHANIC_4))
    );

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of(JSON_DIRECTORY));

        SkillProvider.initialize();
        StringProvider.initialize();
        exportClasses();
    }

    private static String getClassGroup(short jobId) {
        if (jobId >= 0 && jobId < 600) {
            return "Explorers";
        } else if (JobConstants.isCygnusJob(jobId)) {
            return "Cygnus Knights";
        } else if (JobConstants.isAranJob(jobId) || JobConstants.isEvanJob(jobId)) {
            return "Legends";
        } else if (JobConstants.isResistanceJob(jobId)) {
            return "Resistance";
        }
        return "Others";
    }

    private static String getClassCategory(short jobId) {
        return switch (JobConstants.getJobCategory(jobId)) {
            case 1 -> "Warrior";
            case 2 -> "Magician";
            case 3 -> "Archer";
            case 4 -> "Thief";
            case 5 -> "Pirate";
            default -> "Other";
        };
    }

    private static void exportClasses() throws IOException {
        Files.createDirectories(Path.of(JSON_DIRECTORY, CLASSES_DIR));

        for (var classEntry : CLASS_TYPES.entrySet()) {
            final String className = classEntry.getKey();
            final List<Job> classJobs = classEntry.getValue();

            final String classGroup = getClassGroup(classJobs.getLast().getJobId());
            final String classCategory = getClassCategory(classJobs.getLast().getJobId());

            // Create JSON object
            final JSONObject classObject = new JSONObject();
            classObject.put("class", className);
            classObject.put("group", classGroup);
            classObject.put("category", classCategory);

            // Create array for jobs
            final JSONArray jobArray = new JSONArray();
            for (Job job : classJobs) {
                final JSONObject jobObject = new JSONObject();
                jobObject.put("id", job.getJobId());
                jobObject.put("name", job.name());

                // Create array for skills
                final JSONArray skillArray = new JSONArray();
                for (SkillInfo si : SkillProvider.getSkillsForJob(job)) {
                    JSONObject skillObject = new JSONObject();
                    skillObject.put("id", si.getSkillId());

                    final SkillStringInfo skillStringInfo = StringProvider.getSkillString(si.getSkillId());
                    if (skillStringInfo != null) {
                        skillObject.put("name", skillStringInfo.getName());
                        skillObject.put("desc", skillStringInfo.getDesc());
                        skillObject.put("h", skillStringInfo.getH());

                        final JSONObject hmap = new JSONObject();
                        for (var entry : skillStringInfo.getHMap().entrySet()) {
                            hmap.put(String.format("h%d", entry.getKey()), entry.getValue());
                        }
                        if (!hmap.isEmpty()) {
                            skillObject.put("hmap", hmap);
                        }
                    } else {
                        skillObject.put("name", "");
                        skillObject.put("desc", "");
                        skillObject.put("h", "");
                    }

                    skillObject.put("max_level", si.getMaxLevel());
                    skillObject.put("invisible", si.isInvisible());
                    skillObject.put("psd", si.isPsd());
                    final JSONArray psdArray = new JSONArray();
                    psdArray.putAll(si.getPsdSkills());
                    skillObject.put("psd_skills", psdArray);

                    // Skill stats
                    final JSONObject skillStatObject = new JSONObject();
                    // TODO
//                    if (si instanceof StaticSkillInfo ssi) {
//                        skillObject.put("type", "static");
//                        // Stats for each level
//                        for (var entry : ssi.getStats().entrySet()) {
//                            final JSONArray levelArray = new JSONArray();
//                            levelArray.putAll(entry.getValue());
//                            skillStatObject.put(entry.getKey().name(), levelArray);
//                        }
//                    } else if (si instanceof ComputedSkillInfo csi) {
//                        skillObject.put("type", "computed");
//                        // Expression for each stat
//                        for (var entry : csi.getStrings().entrySet()) {
//                            skillStatObject.put(entry.getKey().name(), entry.getValue());
//                        }
//                    } else {
//                        throw new IllegalStateException();
//                    }
                    skillObject.put("stats", skillStatObject);

                    // Skill rect
                    if (si.getRect(1) instanceof Rect rect) {
                        final JSONObject rectObject = new JSONObject();
                        rectObject.put("left", rect.getLeft());
                        rectObject.put("top", rect.getTop());
                        rectObject.put("right", rect.getRight());
                        rectObject.put("bottom", rect.getBottom());
                        skillObject.put("rect", rectObject);
                    }

                    // Add to array
                    skillArray.put(skillObject);
                }

                // Add to array
                jobObject.put("skills", skillArray);
                jobArray.put(jobObject);
            }

            // Add to class object
            classObject.put("jobs", jobArray);

            // Write to file
            final String fileName = className.toLowerCase().replaceAll("\\s", "_").replaceAll("[\\(\\)\\/]", "");
            try (BufferedWriter bw = Files.newBufferedWriter(Path.of(JSON_DIRECTORY, CLASSES_DIR, fileName + FILE_EXTENSION))) {
                classObject.write(bw, 2, 0);
            }
        }
    }
}
