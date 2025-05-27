package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MobSummonInfo {
    private final int itemId;
    private final List<Tuple<Integer, Integer>> entries;

    public MobSummonInfo(int itemId, List<Tuple<Integer, Integer>> entries) {
        this.itemId = itemId;
        this.entries = entries;
    }

    public int getItemId() {
        return itemId;
    }

    public List<Tuple<Integer, Integer>> getEntries() {
        return entries;
    }

    public static MobSummonInfo from(int itemId, WzProperty mobSummonList) throws ProviderError {
        final List<Tuple<Integer, Integer>> entries = new ArrayList<>();
        for (var entry : mobSummonList.getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzProperty mobSummonProp)) {
                throw new ProviderError("Could not resolve mob summon info");
            }
            final int mobId = WzProvider.getInteger(mobSummonProp.get("id"));
            final int prob = WzProvider.getInteger(mobSummonProp.get("prob"));
            entries.add(Tuple.of(mobId, prob));
        }
        return new MobSummonInfo(itemId, Collections.unmodifiableList(entries));
    }
}
