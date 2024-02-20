package kinoko.world.field;

import kinoko.provider.MobProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.map.*;
import kinoko.provider.mob.MobInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.mob.Mob;
import kinoko.world.field.life.npc.Npc;
import kinoko.world.user.User;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class Field {
    private static final AtomicInteger fieldKeyCounter = new AtomicInteger(1);
    private final AtomicInteger fieldObjectCounter = new AtomicInteger(1);
    private final UserPool userPool = new UserPool(this);
    private final LifePool lifePool = new LifePool(this);
    private final DropPool dropPool = new DropPool(this);

    private final MapInfo mapInfo;
    private final byte fieldKey;

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
        this.fieldKey = (byte) (fieldKeyCounter.getAndIncrement() % 0xFF);
    }

    public UserPool getUserPool() {
        return userPool;
    }

    public LifePool getLifePool() {
        return lifePool;
    }

    public DropPool getDropPool() {
        return dropPool;
    }

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public byte getFieldKey() {
        return fieldKey;
    }

    public int getFieldId() {
        return mapInfo.getMapId();
    }

    public Set<FieldOption> getFieldOptions() {
        return mapInfo.getFieldOptions();
    }

    public boolean hasFieldOption(FieldOption fieldOption) {
        return mapInfo.getFieldOptions().contains(fieldOption);
    }

    public FieldType getFieldType() {
        return mapInfo.getFieldType();
    }

    public Optional<PortalInfo> getPortalById(int portalId) {
        return mapInfo.getPortalById(portalId);
    }

    public Optional<PortalInfo> getPortalByName(String name) {
        return mapInfo.getPortalByName(name);
    }

    public Optional<Foothold> getFootholdBelow(int x, int y) {
        return mapInfo.getFootholdBelow(x, y);
    }

    public int getNewObjectId() {
        return fieldObjectCounter.getAndIncrement();
    }

    public void broadcastPacket(OutPacket outPacket) {
        broadcastPacket(outPacket, null);
    }

    public void broadcastPacket(OutPacket outPacket, User except) {
        userPool.forEach((user) -> {
            if (except != null && user.getCharacterId() == except.getCharacterId()) {
                return;
            }
            user.write(outPacket);
        });
    }

    public static Field from(MapInfo mapInfo) {
        final Field field = new Field(mapInfo);
        for (LifeInfo lifeInfo : mapInfo.getLifeInfos()) {
            switch (lifeInfo.getLifeType()) {
                case NPC -> {
                    final Optional<NpcInfo> npcInfoResult = NpcProvider.getNpcInfo(lifeInfo.getTemplateId());
                    final Npc npc = Npc.from(lifeInfo, npcInfoResult.orElseThrow());
                    field.getLifePool().addLife(npc);
                }
                case MOB -> {
                    final Optional<MobInfo> mobInfoResult = MobProvider.getMobInfo(lifeInfo.getTemplateId());
                    final Mob mob = Mob.from(lifeInfo, mobInfoResult.orElseThrow());
                    field.getLifePool().addLife(mob);
                }
            }
        }
        return field;
    }
}
