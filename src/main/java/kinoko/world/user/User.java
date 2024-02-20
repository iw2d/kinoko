package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserPoolPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ChannelServer;
import kinoko.server.client.Client;
import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.util.Locked;
import kinoko.world.field.Field;
import kinoko.world.item.InventoryManager;
import kinoko.world.life.Life;
import kinoko.world.quest.QuestManager;
import kinoko.world.user.temp.TemporaryStatManager;

import java.util.Map;

public final class User extends Life implements Lockable<User> {
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

    public Locked<CharacterData> acquireCharacterData() {
        return characterData.acquire();
    }

    public CharacterStat getCharacterStat() {
        return characterData.getCharacterStat();
    }

    public Locked<CharacterStat> acquireCharacterStat() {
        return characterData.getCharacterStat().acquire();
    }

    public TemporaryStatManager getTemporaryStatManager() {
        return characterData.getTemporaryStatManager();
    }

    public Locked<TemporaryStatManager> acquireTemporaryStatManager() {
        return characterData.getTemporaryStatManager().acquire();
    }

    public InventoryManager getInventoryManager() {
        return characterData.getInventoryManager();
    }

    public Locked<InventoryManager> acquireInventoryManager() {
        return characterData.getInventoryManager().acquire();
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
        destination.getUserPool().addUser(this);
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
        return UserPoolPacket.userEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return UserPoolPacket.userLeaveField(this);
    }

    @Override
    public void lock() {
        characterData.lock();
    }

    @Override
    public void unlock() {
        characterData.unlock();
    }
}
