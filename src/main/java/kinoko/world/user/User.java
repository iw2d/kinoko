package kinoko.world.user;


import kinoko.server.Client;

public final class User {
    private final Client client;
    private final CharacterData characterData;
    private final CalcDamage calcDamage;

    public User(Client client, CharacterData characterData, CalcDamage calcDamage) {
        this.client = client;
        this.characterData = characterData;
        this.calcDamage = calcDamage;
    }

    public Client getClient() {
        return client;
    }

    public CharacterData getCharacterData() {
        return characterData;
    }

    public CalcDamage getCalcDamage() {
        return calcDamage;
    }

    // UTILITY ---------------------------------------------------------------------------------------------------------

    public int getId() {
        return characterData.getCharacterId();
    }
}
