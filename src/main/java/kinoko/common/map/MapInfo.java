package kinoko.common.map;

import java.util.List;

public record MapInfo(
        int id,
        List<Foothold> foothold,
        List<LifeInfo> life,
        List<PortalInfo> portal,
        List<ReactorInfo> reactor,

        // info
        String bgm,
        int version,
        boolean town,
        boolean swim,
        boolean fly,
        int returnMap,
        int forcedReturn,
        int fieldLimit,
        float mobRate,
        String onFirstUserEnter,
        String onUserEnter,
        int VRTop,
        int VRLeft,
        int VRBottom,
        int VRRight
) {
}
