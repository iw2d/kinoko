package kinoko.world.skill.maker;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;

import java.util.List;
import java.util.Optional;

public final class MakerConstants {
    public static int getMonsterTrophyLevel(int itemId) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        return itemInfoResult.map(itemInfo -> itemInfo.getInfo(ItemInfoType.lv)).orElse(0);
    }

    public static int getMonsterCrystalByLevel(int level) {
        // Item/Etc/0426.img/%d/lvMin
        if (level <= 30 || level > 200) {
            return 0;
        } else if (level <= 50) {
            return 4260000;
        } else if (level <= 60) {
            return 4260001;
        } else if (level <= 70) {
            return 4260002;
        } else if (level <= 80) {
            return 4260003;
        } else if (level <= 90) {
            return 4260004;
        } else if (level <= 100) {
            return 4260005;
        } else if (level <= 110) {
            return 4260006;
        } else if (level <= 120) {
            return 4260007;
        }
        return 4260008;
    }

    public static int getTotalCostToMake(int cost, boolean catalyst, List<Integer> gems) {
        // CUIItemMaker::GetTotalCostToMake
        final int gemLevelPoint = gems.stream().mapToInt((itemId) -> (itemId % 10) + 1).sum();
        final int mod = Math.max(cost / 10 * ((catalyst ? 5 : 0) + 3 * gemLevelPoint + 1), 0);
        return cost + mod - (Math.max((cost + mod) % 1000, 0));
    }

    public static int getTotalCostToDisassemble(int cost, int quality) {
        // CUIItemMaker::GetTotalCostToDisassemble
        final int a = cost / 10;
        final int b = switch (quality) {
            case -1 -> 100;
            case 1 -> 200;
            case 2 -> 250;
            case 3 -> 300;
            case 4 -> 350;
            case 5 -> 400;
            default -> 150;
        };
        return b * a / 100 - Math.max(b * a / 100 % 1000, 0);
    }
}
