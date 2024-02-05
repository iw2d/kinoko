package kinoko.provider.npc;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;

import java.util.Objects;

public final class NpcInfo {
    private final int templateId;
    private final boolean move;
    private final String script;
    private final int trunkPut;
    private final int trunkGet;

    public NpcInfo(int templateId, boolean move, String script, int trunkPut, int trunkGet) {
        this.templateId = templateId;
        this.move = move;
        this.script = script;
        this.trunkPut = trunkPut;
        this.trunkGet = trunkGet;
    }

    public int getTemplateId() {
        return templateId;
    }

    public boolean isMove() {
        return move;
    }

    public String getScript() {
        return script;
    }

    public int getTrunkPut() {
        return trunkPut;
    }

    public int getTrunkGet() {
        return trunkGet;
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId);
    }

    @Override
    public String toString() {
        return "NpcInfo[" +
                "id=" + templateId + ", " +
                "move=" + move + ", " +
                "script=" + script + ", " +
                "trunkPut=" + trunkPut + ", " +
                "trunkGet=" + trunkGet + ']';
    }

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
                        throw new ProviderError("Multiple scripts found for npc %d", npcId);
                    }
                    if ((scriptProp.get("script")) instanceof String) {
                        script = scriptProp.get("script");
                    } else if (scriptProp.get("0") instanceof WzListProperty scriptList) {
                        script = scriptList.get("script");
                    } else {
                        throw new ProviderError("Could not resolve script for npc %d", npcId);
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
