package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserPoolPacket;
import kinoko.packet.world.Message;
import kinoko.packet.world.WvsContext;
import kinoko.server.ChannelServer;
import kinoko.server.client.Client;
import kinoko.server.packet.OutPacket;
import kinoko.world.GameConstants;
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

    public CharacterStat getCharacterStat() {
        return characterData.getCharacterStat();
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

    public int getLevel() {
        return getCharacterStat().getLevel();
    }

    public byte getGender() {
        return getCharacterStat().getGender();
    }

    public short getJob() {
        return getCharacterStat().getJob();
    }

    public String getName() {
        return characterData.getCharacterName();
    }

    public CharacterInventory getInventory() {
        return characterData.getCharacterInventory();
    }

    public int getMoney() {
        return getInventory().getMoney();
    }


    // PACKET WRITES ---------------------------------------------------------------------------------------------------

    public void write(OutPacket outPacket) {
        getClient().write(outPacket);
    }

    public void warp(Field destination, int portalId, boolean isMigrate, boolean isRevive) {
        if (getField() != null) {
            getField().getUserPool().removeUser(this);
        }
        setField(destination);
        getCharacterStat().setPosMap(destination.getFieldId());
        getCharacterStat().setPortal((byte) portalId);
        write(StagePacket.setField(this, getChannelId(), isMigrate, isRevive));
        destination.getUserPool().addUser(this);
    }

    public void dispose() {
        write(WvsContext.statChanged(Set.of(), getCharacterData()));
    }

    public void logout() {
        if (getField() != null) {
            getField().getUserPool().removeUser(this);
        }
    }


    // UTILITY METHODS -------------------------------------------------------------------------------------------------

    public boolean addMoney(int money) {
        final long newMoney = ((long) getMoney()) + money;
        if (newMoney > Integer.MAX_VALUE || newMoney < 0) {
            return false;
        }
        getInventory().setMoney((int) newMoney);
        write(WvsContext.statChanged(Set.of(StatFlag.MONEY), characterData));
        return true;
    }

    public void addExp(int exp, boolean white, boolean quest) {
        final int newExp = getCharacterStat().getExp() + exp;
        final int nextLevelExp = GameConstants.getNextLevelExp(getLevel());
        if (newExp >= nextLevelExp) {
            // TODO level up
        }
        write(WvsContext.message(Message.incExp(exp, white, quest)));
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public int getId() {
        return characterData.getCharacterId();
    }

    @Override
    public void setId(int id) {
        throw new IllegalStateException();
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
