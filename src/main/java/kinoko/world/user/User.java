package kinoko.world.user;


public final class User {
    private final CharacterData characterData;

    public User(CharacterData characterData) {
        this.characterData = characterData;
    }

    public int getId() {
        return characterData.getCharacterId();
    }
}
