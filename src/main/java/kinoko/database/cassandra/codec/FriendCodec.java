package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.FriendUDT;
import kinoko.world.social.friend.Friend;
import kinoko.world.social.friend.FriendStatus;

public final class FriendCodec extends MappingCodec<UdtValue, Friend> {
    public FriendCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<Friend> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected Friend innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final int friendId = value.getInt(FriendUDT.FRIEND_ID);
        final String friendName = value.getString(FriendUDT.FRIEND_NAME);
        final String friendGroup = value.getString(FriendUDT.FRIEND_GROUP);
        final FriendStatus status = FriendStatus.getByValue(value.getInt(FriendUDT.FRIEND_STATUS));
        return new Friend(friendId, friendName, friendGroup, status);
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable Friend friend) {
        if (friend == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(FriendUDT.FRIEND_ID, friend.getFriendId())
                .setString(FriendUDT.FRIEND_NAME, friend.getFriendName())
                .setString(FriendUDT.FRIEND_GROUP, friend.getFriendGroup())
                .setInt(FriendUDT.FRIEND_STATUS, friend.getStatus().getValue());
    }
}
