package kinoko.provider.npc;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;

public record NpcInfo(int id, boolean move, String script, int trunkPut, int trunkGet) {
    public static NpcInfo from(int npcId, boolean move, WzListProperty infoProp) throws ProviderError {
        String script = null;
        int trunkPut = 0;
        int trunkGet = 0;
        for (var entry : infoProp.getItems().entrySet()) {
            switch (entry.getKey()) {
                case "script" -> {
                    if (!(entry.getValue() instanceof WzListProperty scriptProp)) {
                        throw new ProviderError("Failed to resolve script property");
                    }
                    if (scriptProp.getItems().size() > 1) {
                        throw new ProviderError("\"Multiple scripts found for npc {}", npcId);
                    }
                    if ((scriptProp.get("script")) instanceof String) {
                        script = scriptProp.get("script");
                    } else if (scriptProp.get("0") instanceof WzListProperty scriptList) {
                        script = scriptList.get("script");
                    } else {
                        throw new ProviderError("Could not resolve script for npc {}", npcId);
                    }
                }
                case "trunkPut" -> {
                    trunkPut = (int) entry.getValue();
                }
                case "trunkGet" -> {
                    trunkGet = (int) entry.getValue();
                }
                default -> {
                    // System.err.printf("Unhandled info %s in npc %d%n", entry.getKey(), npcId);
                }
            }
        }
        return new NpcInfo(npcId, move, script, trunkPut, trunkGet);
    }
}
