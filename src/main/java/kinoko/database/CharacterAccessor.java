package kinoko.database;

import kinoko.world.user.CharacterData;

import java.util.List;
import java.util.Optional;

public interface CharacterAccessor {
    Optional<CharacterData> getCharacterById(int characterId);

    Optional<CharacterData> getCharacterByName(String name);

    Optional<List<CharacterData>> getCharactersByAccountId(int accountId);

    boolean newCharacter(CharacterData characterData);
}
