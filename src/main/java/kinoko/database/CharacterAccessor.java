package kinoko.database;

import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;

import java.util.List;
import java.util.Optional;

public interface CharacterAccessor {
    boolean checkCharacterNameAvailable(String name);

    Optional<CharacterData> getCharacterById(int characterId);

    Optional<CharacterData> getCharacterByName(String name);

    Optional<CharacterInfo> getCharacterInfoByName(String name);

    Optional<Integer> getAccountIdByCharacterId(int characterId);

    List<AvatarData> getAvatarDataByAccountId(int accountId);

    boolean newCharacter(CharacterData characterData);

    boolean saveCharacter(CharacterData characterData);

    boolean deleteCharacter(int accountId, int characterId);
}
