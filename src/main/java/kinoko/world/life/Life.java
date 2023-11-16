package kinoko.world.life;

import kinoko.provider.NpcProvider;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.world.field.FieldObject;

import java.util.Optional;

public abstract class Life implements FieldObject {
    public static Optional<Life> fromLifeInfo(LifeInfo lifeInfo) {
        switch (lifeInfo.type()) {
            case NPC -> {
                final Optional<NpcInfo> npcInfoResult = NpcProvider.getNpcInfo(lifeInfo.id());
                return Optional.of(new Npc(lifeInfo, npcInfoResult.orElse(null)));
            }
            case MOB -> {
                // TODO
            }
        }
        return Optional.empty();
    }
}
