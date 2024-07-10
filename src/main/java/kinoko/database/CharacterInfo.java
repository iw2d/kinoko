package kinoko.database;

public final class CharacterInfo {
    private final int accountId;
    private final int characterId;
    private final String characterName;

    public CharacterInfo(int accountId, int characterId, String characterName) {
        this.accountId = accountId;
        this.characterId = characterId;
        this.characterName = characterName;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getCharacterName() {
        return characterName;
    }
}
