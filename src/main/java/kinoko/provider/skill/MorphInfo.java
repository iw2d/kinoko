package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;

public final class MorphInfo {
    private final int id;
    private final boolean superman;
    private final boolean attackable;

    public MorphInfo(int id, boolean superman, boolean attackable) {
        this.id = id;
        this.superman = superman;
        this.attackable = attackable;
    }

    public int getId() {
        return id;
    }

    public boolean isSuperman() {
        return superman;
    }

    public boolean isAttackable() {
        return attackable;
    }

    public static MorphInfo from(int morphId, WzProperty infoProp) throws ProviderError {
        return new MorphInfo(
                morphId,
                WzProvider.getInteger(infoProp.get("superman"), 0) != 0,
                WzProvider.getInteger(infoProp.get("attackable"), 0) != 0
        );
    }
}
