package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserPoolPacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.ChannelServer;
import kinoko.server.client.Client;
import kinoko.server.packet.OutPacket;
import kinoko.server.script.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;

import java.util.Set;

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

    public void dispose() {
        write(WvsContext.statChanged(Set.of(), getCharacterData()));
    }

    public void logout() {
        if (this.field != null) {
            this.field.removeUser(getCharacterId());
        }
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public OutPacket enterFieldPacket() {
        return UserPoolPacket.userEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return UserPoolPacket.userLeaveField(this);
    }
}
