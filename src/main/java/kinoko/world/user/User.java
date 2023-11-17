package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserPacket;
import kinoko.server.ChannelServer;
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

    // CONVENIENCE METHODS ---------------------------------------------------------------------------------------------

    public ChannelServer getConnectedServer() {
        return (ChannelServer) getClient().getConnectedServer();
    }

    public int getChannelId() {
        return getConnectedServer().getChannelId();
    }

    public int getCharacterId() {
        return getCharacterData().getCharacterId();
    }

    // PACKET WRITES ---------------------------------------------------------------------------------------------------

    public void write(OutPacket outPacket) {
        getClient().write(outPacket);
    }

    public void warp(Field destination, boolean isMigrate, boolean isRevive) {
        warp(destination, 0, isMigrate, isRevive);
    }

    public void warp(Field destination, int portalId, boolean isMigrate, boolean isRevive) {
        if (this.field != null) {
            this.field.removeUser(getCharacterId());
        }
        this.field = destination;
        getCharacterData().getCharacterStat().setPosMap(destination.getFieldId());
        getCharacterData().getCharacterStat().setPortal((byte) portalId);
        write(StagePacket.setField(this, getChannelId(), isMigrate, isRevive));
        destination.addUser(this);
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public Field getField() {
        return field;
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
