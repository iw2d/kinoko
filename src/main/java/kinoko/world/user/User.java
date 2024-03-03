package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ChannelServer;
import kinoko.server.client.Client;
import kinoko.server.dialog.Dialog;
import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.world.Account;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.item.InventoryManager;
import kinoko.world.life.Life;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.funckey.FuncKeyManager;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import kinoko.world.user.temp.SecondaryStat;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class User extends Life implements Lockable<User> {
    private final Lock lock = new ReentrantLock();
    private final Client client;
    private final CharacterData characterData;
    private final Pet[] pets = new Pet[GameConstants.PET_COUNT_MAX];

    private Dialog dialog;
    private int portableChairId;

    public User(Client client, CharacterData characterData) {
        this.client = client;
        this.characterData = characterData;
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

    public String getCharacterName() {
        return characterData.getCharacterName();
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

    public SecondaryStat getSecondaryStat() {
        return characterData.getSecondaryStat();
    }

    public InventoryManager getInventoryManager() {
        return characterData.getInventoryManager();
    }

    public SkillManager getSkillManager() {
        return characterData.getSkillManager();
    }

    public QuestManager getQuestManager() {
        return characterData.getQuestManager();
    }

    public FuncKeyManager getFuncKeyManager() {
        return characterData.getFuncKeyManager();
    }

    public Pet[] getPets() {
        return pets;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public boolean hasDialog() {
        return getDialog() != null;
    }

    public void closeDialog() {
        setDialog(null);
    }

    public int getPortableChairId() {
        return portableChairId;
    }

    public void setPortableChairId(int portableChairId) {
        this.portableChairId = portableChairId;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public int getGender() {
        return getCharacterStat().getGender();
    }

    public int getJob() {
        return getCharacterStat().getJob();
    }

    public int getLevel() {
        return getCharacterStat().getLevel();
    }

    public int getHp() {
        return getCharacterStat().getHp();
    }

    public void setHp(int hp) {
        getCharacterStat().setHp(Math.max(Math.min(hp, getMaxHp()), 0));
        write(WvsContext.statChanged(Stat.HP, getHp(), true));
    }

    public void addHp(int hp) {
        setHp(getHp() + hp);
    }

    public int getMp() {
        return getCharacterStat().getMp();
    }

    public void setMp(int mp) {
        getCharacterStat().setMp(Math.max(Math.min(mp, getMaxMp()), 0));
        write(WvsContext.statChanged(Stat.MP, getMp(), true));
    }

    public void addMp(int mp) {
        setMp(getMp() + mp);
    }

    public int getMaxHp() {
        return getCharacterStat().getMaxHp();
    }

    public int getMaxMp() {
        return getCharacterStat().getMaxMp();
    }

    public void addExp(int exp) {
        final Map<Stat, Object> addExpResult = getCharacterStat().addExp(exp);
        if (addExpResult.containsKey(Stat.LEVEL)) {
            write(UserLocal.effect(Effect.levelUp()));
            getField().broadcastPacket(UserRemote.effect(this, Effect.levelUp()), this);
        }
        write(WvsContext.statChanged(addExpResult, true));
    }

    public long getPetSn(int index) {
        assert index >= 0 && index < GameConstants.PET_COUNT_MAX;
        if (index == 0) {
            return getCharacterStat().getPetSn1();
        } else if (index == 1) {
            return getCharacterStat().getPetSn2();
        } else {
            return getCharacterStat().getPetSn3();
        }
    }

    public Optional<Integer> getPetIndex(long petSn) {
        if (getCharacterStat().getPetSn1() == petSn) {
            return Optional.of(0);
        } else if (getCharacterStat().getPetSn2() == petSn) {
            return Optional.of(1);
        } else if (getCharacterStat().getPetSn3() == petSn) {
            return Optional.of(2);
        }
        return Optional.empty();
    }

    public void setPetIndex(int index, long petSn) {
        assert index >= 0 && index < GameConstants.PET_COUNT_MAX;
        if (index == 0) {
            getCharacterStat().setPetSn1(petSn);
            write(WvsContext.statChanged(Stat.PET_1, petSn, true));
        } else if (index == 1) {
            getCharacterStat().setPetSn2(petSn);
            write(WvsContext.statChanged(Stat.PET_2, petSn, true));
        } else {
            getCharacterStat().setPetSn3(petSn);
            write(WvsContext.statChanged(Stat.PET_3, petSn, true));
        }
    }

    public void warp(Field destination, PortalInfo portal, boolean isMigrate, boolean isRevive) {
        if (getField() != null) {
            getField().removeUser(this);
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
        write(WvsContext.statChanged(Map.of(), true));
    }

    public void logout() {
        if (getField() != null) {
            getField().removeUser(this);
        }
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

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
