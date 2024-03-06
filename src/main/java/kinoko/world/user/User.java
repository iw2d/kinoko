package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ChannelServer;
import kinoko.server.client.Client;
import kinoko.server.dialog.Dialog;
import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.world.Account;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.item.*;
import kinoko.world.life.Life;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.PassiveSkillData;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.funckey.FuncKeyManager;
import kinoko.world.user.stat.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class User extends Life implements Lockable<User> {
    private final Lock lock = new ReentrantLock();
    private final Client client;
    private final CharacterData characterData;

    private final BasicStat basicStat = new BasicStat();
    private final ForcedStat forcedStat = new ForcedStat();
    private final SecondaryStat secondaryStat = new SecondaryStat();
    private final PassiveSkillData passiveSkillData = new PassiveSkillData();
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

    public BasicStat getBasicStat() {
        return basicStat;
    }

    public ForcedStat getForcedStat() {
        return forcedStat;
    }

    public SecondaryStat getSecondaryStat() {
        return secondaryStat;
    }

    public PassiveSkillData getPassiveSkillData() {
        return passiveSkillData;
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


    // STAT METHODS ----------------------------------------------------------------------------------------------------

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
        getCharacterStat().setHp(Math.clamp(hp, 0, getMaxHp()));
        write(WvsContext.statChanged(Stat.HP, getHp(), true));
    }

    public void addHp(int hp) {
        setHp(getHp() + hp);
    }

    public int getMp() {
        return getCharacterStat().getMp();
    }

    public void setMp(int mp) {
        getCharacterStat().setMp(Math.clamp(mp, 0, getMaxMp()));
        write(WvsContext.statChanged(Stat.MP, getMp(), true));
    }

    public void addMp(int mp) {
        setMp(getMp() + mp);
    }

    public int getMaxHp() {
        return getBasicStat().getMaxHp();
    }

    public int getMaxMp() {
        return getBasicStat().getMaxMp();
    }

    public void addExp(int exp) {
        final Map<Stat, Object> addExpResult = getCharacterStat().addExp(exp);
        write(WvsContext.statChanged(addExpResult, true));
        // Level up
        if (addExpResult.containsKey(Stat.LEVEL)) {
            getField().broadcastPacket(UserRemote.effect(this, Effect.levelUp()), this);
            validateStat();
            setHp(getMaxHp());
            setMp(getMaxMp());
        }
    }

    public void updatePassiveSkillData() {
        getPassiveSkillData().setFrom(getBasicStat(), getSecondaryStat(), getSkillManager());
    }

    public void validateStat() {
        // get_real_equip
        final Map<Integer, Item> realEquip = getRealEquip();

        // BasicStat::SetFrom
        getBasicStat().setFrom(getCharacterStat(), getForcedStat(), getSecondaryStat(), getSkillManager(), getPassiveSkillData(), realEquip);

        // SecondaryStat::SetFrom
        getSecondaryStat().setFrom(getBasicStat(), getForcedStat(), getSkillManager(), realEquip);

        // Adjust hp and mp
        if (getHp() > getMaxHp()) {
            setHp(getMaxHp());
        }
        if (getMp() > getMaxMp()) {
            setMp(getMaxMp());
        }
    }

    public void setTemporaryStat(CharacterTemporaryStat cts, TemporaryStatOption option) {
        setTemporaryStat(Map.of(cts, option));
    }

    public void setTemporaryStat(Map<CharacterTemporaryStat, TemporaryStatOption> setStats) {
        for (var entry : setStats.entrySet()) {
            getSecondaryStat().getTemporaryStats().put(entry.getKey(), entry.getValue());
        }
        write(WvsContext.temporaryStatSet(getSecondaryStat(), setStats));
        getField().broadcastPacket(UserRemote.temporaryStatSet(this, setStats));
    }

    public void resetTemporaryStat(CharacterTemporaryStat cts) {
        resetTemporaryStat(Set.of(cts));
    }

    public void resetTemporaryStat(Set<CharacterTemporaryStat> resetStats) {
        write(WvsContext.temporaryStatReset(resetStats));
        getField().broadcastPacket(UserRemote.temporaryStatReset(this, resetStats));
    }


    // OTHER HELPER METHODS --------------------------------------------------------------------------------------------

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


    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    public Map<Integer, Item> getRealEquip() {
        final CharacterStat cs = getCharacterStat();
        final int basicStatUp = getSecondaryStat().getOption(CharacterTemporaryStat.BasicStatUp).nOption;
        final int baseStr = cs.getBaseStr() + (basicStatUp * cs.getBaseStr() / 100);
        final int baseDex = cs.getBaseDex() + (basicStatUp * cs.getBaseDex() / 100);
        final int baseInt = cs.getBaseInt() + (basicStatUp * cs.getBaseInt() / 100);
        final int baseLuk = cs.getBaseLuk() + (basicStatUp * cs.getBaseLuk() / 100);
        int incStr = 0, incDex = 0, incInt = 0, incLuk = 0;

        // Compute total stat
        final Inventory equipped = getInventoryManager().getEquipped();
        for (var entry : equipped.getItems().entrySet()) {
            final Item item = entry.getValue();
            if (item.getItemType() != ItemType.EQUIP) {  // TODO: check equip slot ext expired
                continue;
            }
            final EquipData ed = item.getEquipData();
            incStr += ed.getIncStr();
            incDex += ed.getIncDex();
            incInt += ed.getIncInt();
            incLuk += ed.getIncLuk();
            // TODO ApplyItemOption, ApplyItemOptionR
        }
        // TODO set items

        // Build real equip list
        final Map<Integer, Item> realEquip = new HashMap<>();
        final Item weapon = equipped.getItem(BodyPart.WEAPON.getValue());
        for (var entry : equipped.getItems().entrySet()) {
            // Resolve item info and equip data
            final int position = entry.getKey();
            final Item item = entry.getValue();
            if (item.getItemType() != ItemType.EQUIP) {
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
            if (itemInfoResult.isEmpty()) {
                continue;
            }
            final ItemInfo ii = itemInfoResult.get();
            final EquipData ed = item.getEquipData();

            // Find applicable pet
            final Pet pet;
            if (position == BodyPart.PET_EQUIP_1.getValue()) {
                pet = getPets()[0];
            } else if (position == BodyPart.PET_EQUIP_2.getValue()) {
                pet = getPets()[2];
            } else if (position == BodyPart.PET_EQUIP_3.getValue()) {
                pet = getPets()[3];
            } else {
                pet = null;
            }

            // Check if able to equip
            if (ii.isAbleToEquip(
                    cs.getGender(),
                    cs.getLevel(),
                    cs.getJob(),
                    cs.getSubJob(),
                    baseStr + incStr - ed.getIncStr(),
                    baseDex + incDex - ed.getIncDex(),
                    baseInt + incInt - ed.getIncInt(),
                    baseLuk + incLuk - ed.getIncLuk(),
                    cs.getPop(),
                    ed.getDurability(),
                    weapon != null ? weapon.getItemId() : 0,
                    pet != null ? pet.getTemplateId() : 0
            )) {
                realEquip.put(position, item);
            }
        }
        return Collections.unmodifiableMap(realEquip);
    }


}
