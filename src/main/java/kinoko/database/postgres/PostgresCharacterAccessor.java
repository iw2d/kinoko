package kinoko.database.postgres;

import kinoko.database.CharacterAccessor;
import kinoko.database.ConnectionPool;
import kinoko.database.generated.public_.tables.records.CharacterInventoryRecord;
import kinoko.database.generated.public_.tables.records.CharacterStatRecord;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterInventory;
import kinoko.world.user.CharacterStat;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static kinoko.database.generated.public_.Tables.*;

public final class PostgresCharacterAccessor extends PostgresAccessor implements CharacterAccessor {
    public PostgresCharacterAccessor(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        try (final Connection conn = getConnection()) {
            final DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            final Result<Record> result = context.select()
                    .from(CHARACTER_DATA
                        .join(CHARACTER_STAT)
                        .on(CHARACTER_STAT.ID.eq(CHARACTER_DATA.CHARACTER_STAT))
                        .join(CHARACTER_INVENTORY
                                .join(INVENTORY
                                        .join(ITEM)
                                        .on(ITEM.INVENTORY_ID.eq(INVENTORY.ID))
                                )
                                .on(INVENTORY.ID.in(
                                        CHARACTER_INVENTORY.EQUIPPED,
                                        CHARACTER_INVENTORY.EQUIP_INVENTORY,
                                        CHARACTER_INVENTORY.CONSUME_INVENTORY,
                                        CHARACTER_INVENTORY.INSTALL_INVENTORY,
                                        CHARACTER_INVENTORY.ETC_INVENTORY,
                                        CHARACTER_INVENTORY.CASH_INVENTORY
                                ))
                        )
                        .on(CHARACTER_INVENTORY.ID.eq(CHARACTER_DATA.CHARACTER_INVENTORY))
                    )
                    .where(CHARACTER_DATA.ID.eq(characterId))
                    .fetch();
            for (Record r : result) {
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterData> getCharacterByName(String name) {
        return null;
    }

    @Override
    public Optional<List<CharacterData>> getCharactersByAccountId(int accountId) {
        return null;
    }

    @Override
    public boolean newCharacter(CharacterData characterData) {
        try (final Connection conn = getConnection()) {
            final DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            context.transaction((Configuration transaction) -> {
                final CharacterStat cs = characterData.getCharacterStat();
                final CharacterStatRecord csRecord = transaction.dsl()
                        .insertInto(CHARACTER_STAT,
                                CHARACTER_STAT.GENDER, CHARACTER_STAT.SKIN, CHARACTER_STAT.FACE, CHARACTER_STAT.HAIR,
                                CHARACTER_STAT.LEVEL, CHARACTER_STAT.JOB, CHARACTER_STAT.SUB_JOB,
                                CHARACTER_STAT.BASE_STR, CHARACTER_STAT.BASE_DEX, CHARACTER_STAT.BASE_INT, CHARACTER_STAT.BASE_LUK,
                                CHARACTER_STAT.HP, CHARACTER_STAT.MAX_HP, CHARACTER_STAT.MP, CHARACTER_STAT.MAX_MP,
                                CHARACTER_STAT.AP, CHARACTER_STAT.SP, CHARACTER_STAT.EXP, CHARACTER_STAT.POP,
                                CHARACTER_STAT.POS_MAP, CHARACTER_STAT.PORTAL
                        )
                        .values(
                                (int) cs.getGender(), (int) cs.getSkin(), cs.getFace(), cs.getHair(),
                                (int) cs.getLevel(), (int) cs.getJob(), (int) cs.getSubJob(),
                                (int) cs.getBaseStr(), (int) cs.getBaseDex(), (int) cs.getBaseInt(), (int) cs.getBaseLuk(),
                                cs.getHp(), cs.getMaxHp(), cs.getMp(), cs.getMaxMp(),
                                (int) cs.getAp(), cs.getSp().toArray(), cs.getExp(), (int) cs.getPop(),
                                cs.getPosMap(), (int) cs.getPortal()
                        )
                        .returning()
                        .fetchOne();

                final CharacterInventory ci = characterData.getCharacterInventory();
                final CharacterInventoryRecord ciRecord = transaction.dsl()
                        .insertInto(CHARACTER_INVENTORY,
                                CHARACTER_INVENTORY.MONEY
                        )
                        .values(
                                ci.getMoney()
                        )
                        .returning()
                        .fetchOne();

                transaction.dsl()
                        .insertInto(CHARACTER_DATA,
                                CHARACTER_DATA.NAME, CHARACTER_DATA.ACCOUNT_ID, CHARACTER_DATA.CHARACTER_STAT,
                                CHARACTER_DATA.CHARACTER_INVENTORY, CHARACTER_DATA.FRIEND_MAX
                        )
                        .values(
                                characterData.getName(), characterData.getAccountId(), csRecord.getId(),
                                ciRecord.getId(), characterData.getFriendMax()
                        )
                        .execute();
            });
        } catch (SQLException e) {
            log.error(e);
            return false;
        }
        return true;
    }
}
