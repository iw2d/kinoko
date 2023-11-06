package kinoko.database.postgres;

import kinoko.database.CharacterAccessor;
import kinoko.database.ConnectionPool;
import kinoko.world.user.CharacterData;

import java.util.List;
import java.util.Optional;

public final class PostgresCharacterAccessor extends PostgresAccessor implements CharacterAccessor {
    public PostgresCharacterAccessor(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        return null;
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
    public void newCharacter(CharacterData characterData) {

    }


}
