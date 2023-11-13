package kinoko.world.user;

public final class User {
    private final CharacterData characterData;
    private final CalcDamage calcDamage;

    public User(CharacterData characterData, CalcDamage calcDamage) {
        this.characterData = characterData;
        this.calcDamage = calcDamage;
    }

    public CharacterData getCharacterData() {
        return characterData;
    }

    public CalcDamage getCalcDamage() {
        return calcDamage;
    }

    public int getId() {
        return characterData.getCharacterId();
    }
}
