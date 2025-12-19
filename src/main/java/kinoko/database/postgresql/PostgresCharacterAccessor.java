package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.CharacterAccessor;
import kinoko.database.CharacterInfo;
import kinoko.database.postgresql.type.*;
import kinoko.server.rank.CharacterRank;
import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;

import java.sql.*;
import java.util.*;

public final class PostgresCharacterAccessor extends PostgresAccessor implements CharacterAccessor {
    public PostgresCharacterAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Checks if a character name is available for creation.
     *
     * @param name the character name to check
     * @return true if the name is not already taken, false otherwise
     */
    @Override
    public boolean checkCharacterNameAvailable(String name) {
        return withTransaction(conn -> {
            return CharacterDataDao.checkCharacterNameAvailable(conn, name);
        });
    }

    /**
     * Retrieves a fully populated CharacterData by character ID.
     *
     * @param characterId the ID of the character
     * @return an Optional containing the CharacterData if found, empty otherwise
     */
    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        try (Connection conn = getConnection()) {
            return CharacterDataDao.getCharacterById(conn, characterId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Retrieves a fully populated CharacterData by character name (case-insensitive).
     *
     * @param name the name of the character
     * @return an Optional containing the CharacterData if found, empty otherwise
     */
    @Override
    public Optional<CharacterData> getCharacterByName(String name) {
        try (Connection conn = getConnection()) {
            return CharacterDataDao.getCharacterByName(conn, name);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Retrieves basic CharacterInfo by name (case-insensitive) without loading full data.
     *
     * @param name the character name
     * @return an Optional containing CharacterInfo if found, empty otherwise
     */
    @Override
    public Optional<CharacterInfo> getCharacterInfoByName(String name) {
        try (Connection conn = getConnection()) {
            return CharacterInfoDao.getCharacterInfoByName(conn, name);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Retrieves the account ID associated with a character ID.
     *
     * @param characterId the character ID
     * @return an Optional containing the account ID if found, empty otherwise
     */
    @Override
    public Optional<Integer> getAccountIdByCharacterId(int characterId) {
        try (Connection conn = getConnection()) {
            return AccountDao.getAccountIdByCharacterId(conn, characterId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Retrieves all AvatarData for a specific account ID.
     *
     * @param accountId the account ID
     * @return a list of AvatarData objects; empty if none are found or an error occurs
     */
    @Override
    public List<AvatarData> getAvatarDataByAccountId(int accountId) {
        try (Connection conn = getConnection()) {
            return AvatarDataDao.getAvatarDataByAccountId(conn, accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Creates a new character in the database.
     *
     * Performs all dependent inserts (stats, inventory, skills, quests, config, popularity)
     * using a single transaction.
     *
     * @param characterData the character data to insert
     * @return true if creation was successful, false otherwise
     */
    @Override
    public synchronized boolean newCharacter(CharacterData characterData) {
        return withTransaction(conn -> {
            return CharacterDataDao.newCharacter(conn, characterData);
        });
    }

    /**
     * Saves an existing character's data to the database.
     *
     * Updates the character row and all dependent tables within a transaction.
     *
     * @param characterData the character data to save
     * @return true if the save was successful, false otherwise
     */
    @Override
    public boolean saveCharacter(CharacterData characterData) {
        return withTransaction(conn -> {
            return CharacterDataDao.saveCharacter(conn, characterData);
        });
    }

    /**
     * Deletes a character associated with the given account ID.
     *
     * @param accountId   the account ID
     * @param characterId the character ID to delete
     * @return true if the character was deleted, false otherwise
     */
    @Override
    public boolean deleteCharacter(int accountId, int characterId) {
        return withTransaction(conn -> {
            return UserDao.deleteCharacter(conn, accountId, characterId);
        });
    }

    /**
     * Retrieves the world and job-specific ranks of all characters.
     *
     * Characters with admin or manager jobs are excluded. Ranking is
     * based on cumulative EXP, and ties are broken by earliest max level time.
     *
     * @return a map from character ID to CharacterRank; empty map if an error occurs
     */
    @Override
    public Map<Integer, CharacterRank> getCharacterRanks() {
        try (Connection conn = getConnection()) {
            return CharacterRankDao.getCharacterRanks(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
