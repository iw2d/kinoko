package kinoko.world.user;

import kinoko.server.client.Client;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.FieldObject;

public final class User implements FieldObject {
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

    public int getCharacterId() {
        return characterData.getCharacterId();
    }

    public int getFieldId() {
        return characterData.getCharacterStat().getPosMap();
    }

    public void setFieldId(int fieldId) {
        characterData.getCharacterStat().setPosMap(fieldId);
    }

    @Override
    public OutPacket enterFieldPacket() {
        // TODO
        return OutPacket.of(OutHeader.USER_ENTER_FIELD);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_LEAVE_FIELD);
        outPacket.encodeInt(getCharacterId()); // dwCharacterId
        return outPacket;
    }
}
