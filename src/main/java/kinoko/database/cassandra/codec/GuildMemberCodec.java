package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.GuildMemberUDT;
import kinoko.server.guild.GuildMember;
import kinoko.server.guild.GuildRank;

public final class GuildMemberCodec extends MappingCodec<UdtValue, GuildMember> {
    public GuildMemberCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<GuildMember> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected GuildMember innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final int characterId = value.getInt(GuildMemberUDT.CHARACTER_ID);
        final String characterName = value.getString(GuildMemberUDT.CHARACTER_NAME);
        final int job = value.getInt(GuildMemberUDT.JOB);
        final int level = value.getInt(GuildMemberUDT.LEVEL);
        final GuildRank guildRank = GuildRank.getByValue(value.getInt(GuildMemberUDT.GUILD_RANK));
        final GuildRank allianceRank = GuildRank.getByValue(value.getInt(GuildMemberUDT.ALLIANCE_RANK));
        return new GuildMember(
                characterId,
                characterName,
                job,
                level,
                false,
                guildRank,
                allianceRank
        );
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable GuildMember member) {
        if (member == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(GuildMemberUDT.CHARACTER_ID, member.getCharacterId())
                .setString(GuildMemberUDT.CHARACTER_NAME, member.getCharacterName())
                .setInt(GuildMemberUDT.JOB, member.getJob())
                .setInt(GuildMemberUDT.LEVEL, member.getLevel())
                .setInt(GuildMemberUDT.GUILD_RANK, member.getGuildRank().getValue())
                .setInt(GuildMemberUDT.ALLIANCE_RANK, member.getAllianceRank().getValue());
    }
}
