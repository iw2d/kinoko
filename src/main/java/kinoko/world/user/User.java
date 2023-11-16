package kinoko.world.user;

import kinoko.packet.user.UserPacket;
import kinoko.server.client.Client;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;

public final class User implements FieldObject {
    private final Client client;
    private final CharacterData characterData;
    private final CalcDamage calcDamage;

    private Field field;

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

    public int getCharacterId() {
        return getCharacterData().getCharacterId();
    }


    public int getPosMap() {
        return getCharacterData().getCharacterStat().getPosMap();
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
        getCharacterData().getCharacterStat().setPosMap(field.getFieldId());
    }

    @Override
    public OutPacket enterFieldPacket() {
        return UserPacket.userEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return UserPacket.userLeaveField(this);
    }
}
