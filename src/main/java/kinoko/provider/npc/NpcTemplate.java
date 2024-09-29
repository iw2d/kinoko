package kinoko.provider.npc;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

public final class NpcTemplate {
    private final int id;
    private final boolean move;
    private final String script;
    private final int trunkPut;
    private final int trunkGet;
    private final boolean guildRank;
    private final boolean imitate;

    public NpcTemplate(int id, boolean move, String script, int trunkPut, int trunkGet, boolean guildRank, boolean imitate) {
        this.id = id;
        this.move = move;
        this.script = script;
        this.trunkPut = trunkPut;
        this.trunkGet = trunkGet;
        this.guildRank = guildRank;
        this.imitate = imitate;
    }

    public int getId() {
        return id;
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

    public boolean hasScript() {
        return getScript() != null && !getScript().isEmpty();
    }

    public boolean isTrunk() {
        return getTrunkGet() > 0 || getTrunkPut() > 0;
    }

    public boolean isGuildRank() {
        return guildRank;
    }

    public boolean isImitate() {
        return imitate;
    }

    @Override
    public String toString() {
        return "NpcTemplate{" +
                "id=" + id +
                ", move=" + move +
                ", script='" + script + '\'' +
                ", trunkPut=" + trunkPut +
                ", trunkGet=" + trunkGet +
                ", guildRank=" + guildRank +
                ", imitate=" + imitate +
                '}';
    }

    public static NpcTemplate from(int npcId, boolean move, WzListProperty infoProp) throws ProviderError {
        String script = null;
        int trunkPut = 0;
        int trunkGet = 0;
        boolean guildRank = false;
        boolean imitate = false;
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
                    trunkPut = WzProvider.getInteger(entry.getValue());
                }
                case "trunkGet" -> {
                    trunkGet = WzProvider.getInteger(entry.getValue());
                }
                case "guildRank" -> {
                    guildRank = WzProvider.getInteger(entry.getValue()) != 0;
                }
                case "imitate" -> {
                    imitate = WzProvider.getInteger(entry.getValue()) != 0;
                }
                default -> {
                    // System.err.printf("Unhandled info %s in npc %d%n", entry.getKey(), npcId);
                }
            }
        }
        return new NpcTemplate(npcId, move, script, trunkPut, trunkGet, guildRank, imitate);
    }

}
