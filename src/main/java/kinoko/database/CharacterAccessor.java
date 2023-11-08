package kinoko.database;

import kinoko.world.user.CharacterData;

import java.util.Optional;

public interface CharacterAccessor {
    Optional<Integer> nextCharacterId();

    Optional<CharacterData> getCharacterById(int characterId);

    Optional<CharacterData> getCharacterByName(String name);

    boolean saveCharacter(CharacterData characterData);
}
