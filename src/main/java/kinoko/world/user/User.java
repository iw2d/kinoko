package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserPoolPacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.ChannelServer;
import kinoko.server.client.Client;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.Field;
import kinoko.world.life.Life;
import kinoko.world.user.temp.TemporaryStatManager;

import java.util.Set;

public final class User extends Life {
    private final Client client;
    private final CharacterData characterData;
    private final CalcDamage calcDamage;
    private final TemporaryStatManager temporaryStatManager;

    public User(Client client, CharacterData characterData, CalcDamage calcDamage, TemporaryStatManager temporaryStatManager) {
        this.client = client;
        this.characterData = characterData;
        this.calcDamage = calcDamage;
        this.temporaryStatManager = temporaryStatManager;
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

    public TemporaryStatManager getTemporaryStatManager() {
        return temporaryStatManager;
    }


    // CONVENIENCE METHODS ---------------------------------------------------------------------------------------------

    public ChannelServer getConnectedServer() {
        return (ChannelServer) client.getConnectedServer();
    }

    public int getChannelId() {
        return getConnectedServer().getChannelId();
    }

    public int getCharacterId() {
        return characterData.getCharacterId();
    }

    public int getLevel() {
        return characterData.getCharacterStat().getLevel();
    }

    public short getJob() {
        return characterData.getCharacterStat().getJob();
    }

    public String getName() {
        return characterData.getCharacterName();
    }

    public int getMoney() {
        return characterData.getCharacterInventory().getMoney();
    }


    // PACKET WRITES ---------------------------------------------------------------------------------------------------

    public void write(OutPacket outPacket) {
        getClient().write(outPacket);
    }

    public void warp(Field destination, int portalId, boolean isMigrate, boolean isRevive) {
        if (getField() != null) {
            getField().removeUser(getCharacterId());
        }
        setField(destination);
        getCharacterData().getCharacterStat().setPosMap(destination.getFieldId());
        getCharacterData().getCharacterStat().setPortal((byte) portalId);
        write(StagePacket.setField(this, getChannelId(), isMigrate, isRevive));
        destination.addUser(this);
    }

    public void dispose() {
        write(WvsContext.statChanged(Set.of(), getCharacterData()));
    }

    public void logout() {
        if (getField() != null) {
            getField().removeUser(getCharacterId());
        }
    }


    // UTILITY METHODS -------------------------------------------------------------------------------------------------

    public boolean addMoney(int money) {
        final long newMoney = ((long) getMoney()) + money;
        if (newMoney > Integer.MAX_VALUE || newMoney < 0) {
            return false;
        }
        getCharacterData().getCharacterInventory().setMoney((int) newMoney);
        write(WvsContext.statChanged(Set.of(StatFlag.MONEY), characterData));
        return true;
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public OutPacket enterFieldPacket() {
        return UserPoolPacket.userEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return UserPoolPacket.userLeaveField(this);
    }
}
