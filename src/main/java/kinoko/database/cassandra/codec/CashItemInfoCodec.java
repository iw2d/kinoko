package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.type.CashItemInfoUDT;
import kinoko.server.cashshop.CashItemInfo;
import kinoko.world.item.Item;

public final class CashItemInfoCodec extends MappingCodec<UdtValue, CashItemInfo> {
    public CashItemInfoCodec(TypeCodec<UdtValue> innerCodec, GenericType<CashItemInfo> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected CashItemInfo innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        return new CashItemInfo(
                value.get(CashItemInfoUDT.ITEM, Item.class),
                value.getInt(CashItemInfoUDT.COMMODITY_ID),
                value.getInt(CashItemInfoUDT.ACCOUNT_ID),
                value.getInt(CashItemInfoUDT.CHARACTER_ID),
                value.getString(CashItemInfoUDT.CHARACTER_NAME)
        );
    }

    @Override
    protected UdtValue outerToInner(CashItemInfo cii) {
        if (cii == null) {
            return null;
        }
        return getCqlType().newValue()
                .set(CashItemInfoUDT.ITEM, cii.getItem(), Item.class)
                .setInt(CashItemInfoUDT.COMMODITY_ID, cii.getCommodityId())
                .setInt(CashItemInfoUDT.ACCOUNT_ID, cii.getAccountId())
                .setInt(CashItemInfoUDT.CHARACTER_ID, cii.getCharacterId())
                .setString(CashItemInfoUDT.CHARACTER_NAME, cii.getCharacterName());
    }
}
