package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ChannelServer;
import kinoko.server.client.Client;
import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.world.Account;
import kinoko.world.field.Field;
import kinoko.world.field.life.Life;
import kinoko.world.item.InventoryManager;
import kinoko.world.quest.QuestManager;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.temp.TemporaryStatManager;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class User extends Life implements Lockable<User> {
    private final Lock lock = new ReentrantLock();
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

    public Account getAccount() {
        return client.getAccount();
    }

    public ChannelServer getConnectedServer() {
        return (ChannelServer) client.getConnectedServer();
    }

    public int getChannelId() {
        return getConnectedServer().getChannelId();
    }

    public int getAccountId() {
        return characterData.getAccountId();
    }

    public int getCharacterId() {
        return characterData.getCharacterId();
    }

    public long getNextItemSn() {
        return characterData.getNextItemSn();
    }

    public CharacterData getCharacterData() {
        return characterData;
    }

    public CharacterStat getCharacterStat() {
        return characterData.getCharacterStat();
    }

    public TemporaryStatManager getTemporaryStatManager() {
        return characterData.getTemporaryStatManager();
    }

    public InventoryManager getInventoryManager() {
        return characterData.getInventoryManager();
    }

    public QuestManager getQuestManager() {
        return characterData.getQuestManager();
    }

    public CalcDamage getCalcDamage() {
        return calcDamage;
    }

    public void warp(Field destination, PortalInfo portal, boolean isMigrate, boolean isRevive) {
        if (getField() != null) {
            getField().getUserPool().removeUser(this);
        }
        setField(destination);
        setX(portal.getX());
        setY(portal.getY());
        getCharacterStat().setPosMap(destination.getFieldId());
        getCharacterStat().setPortal((byte) portal.getPortalId());
        write(StagePacket.setField(this, getChannelId(), isMigrate, isRevive));
        destination.addUser(this);
    }

    public void write(OutPacket outPacket) {
        getClient().write(outPacket);
    }

    public void dispose() {
        write(WvsContext.statChanged(Map.of()));
    }

    public void logout() {
        if (getField() != null) {
            getField().getUserPool().removeUser(this);
        }
    }

    @Override
    public int getId() {
        return getCharacterId();
    }

    @Override
    public void setId(int id) {
        throw new IllegalStateException("Tried to modify character ID");
    }

    @Override
    public OutPacket enterFieldPacket() {
        return UserPacket.userEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return UserPacket.userLeaveField(this);
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
