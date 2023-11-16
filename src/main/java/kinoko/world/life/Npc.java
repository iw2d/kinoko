package kinoko.world.life;

import kinoko.provider.map.LifeInfo;
import kinoko.provider.npc.NpcInfo;

public final class Npc extends Life {
    private final LifeInfo lifeInfo;
    private final NpcInfo npcInfo;

    public Npc(LifeInfo lifeInfo, NpcInfo npcInfo) {
        this.lifeInfo = lifeInfo;
        this.npcInfo = npcInfo;
    }
}
