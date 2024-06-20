package kinoko.world.user;

import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.SummonedPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.FriendPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.node.ChannelServerNode;
import kinoko.server.node.Client;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.Lockable;
import kinoko.world.GameConstants;
import kinoko.world.dialog.Dialog;
import kinoko.world.field.Field;
import kinoko.world.field.OpenGate;
import kinoko.world.field.TownPortal;
import kinoko.world.field.life.Life;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedEnterType;
import kinoko.world.field.summoned.SummonedLeaveType;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.Item;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.PassiveSkillData;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.social.friend.FriendManager;
import kinoko.world.user.config.ConfigManager;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class User extends Life implements Lockable<User> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Client client;
    private final CharacterData characterData;

    private final BasicStat basicStat = new BasicStat();
    private final ForcedStat forcedStat = new ForcedStat();
    private final SecondaryStat secondaryStat = new SecondaryStat();
    private final PassiveSkillData passiveSkillData = new PassiveSkillData();

    private final List<Pet> pets = new ArrayList<>();
    private final Map<Integer, List<Summoned>> summoned = new HashMap<>(); // skill id -> list of summons

    private Dialog dialog;
    private Dragon dragon;
    private TownPortal townPortal;
    private OpenGate openGate;
    private int partyId;
    private int partyMemberIndex;
    private int messengerId;
    private int effectItemId;
    private int portableChairId;
    private String adBoard;
    private boolean inTransfer;

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

    public ChannelServerNode getConnectedServer() {
        return (ChannelServerNode) client.getServerNode();
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

    public FriendManager getFriendManager() {
        return characterData.getFriendManager();
    }

    public ConfigManager getConfigManager() {
        return characterData.getConfigManager();
    }

    public MiniGameRecord getMiniGameRecord() {
        return characterData.getMiniGameRecord();
    }

    public WildHunterInfo getWildHunterInfo() {
        return characterData.getWildHunterInfo();
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

    public List<Pet> getPets() {
        return pets;
    }

    public Map<Integer, List<Summoned>> getSummoned() {
        return summoned;
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

    public Dragon getDragon() {
        return dragon;
    }

    public void setDragon(Dragon dragon) {
        this.dragon = dragon;
    }

    public TownPortal getTownPortal() {
        return townPortal;
    }

    public void setTownPortal(TownPortal townPortal) {
        this.townPortal = townPortal;
    }

    public int getTownPortalIndex() {
        return getPartyId() != 0 ? getPartyMemberIndex() - 1 : 0;
    }

    public OpenGate getOpenGate() {
        return openGate;
    }

    public void setOpenGate(OpenGate openGate) {
        this.openGate = openGate;
    }

    public int getPartyId() {
        return partyId;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }

    public int getPartyMemberIndex() {
        return partyMemberIndex;
    }

    public void setPartyMemberIndex(int partyMemberIndex) {
        this.partyMemberIndex = partyMemberIndex;
    }

    public int getMessengerId() {
        return messengerId;
    }

    public void setMessengerId(int messengerId) {
        this.messengerId = messengerId;
    }

    public int getEffectItemId() {
        return effectItemId;
    }

    public void setEffectItemId(int effectItemId) {
        this.effectItemId = effectItemId;
    }

    public int getPortableChairId() {
        return portableChairId;
    }

    public void setPortableChairId(int portableChairId) {
        this.portableChairId = portableChairId;
    }

    public String getAdBoard() {
        return adBoard;
    }

    public void setAdBoard(String adBoard) {
        this.adBoard = adBoard;
    }

    public boolean isInTransfer() {
        return inTransfer;
    }

    public void setInTransfer(boolean inTransfer) {
        this.inTransfer = inTransfer;
    }


    // STAT METHODS ----------------------------------------------------------------------------------------------------

    public int getGender() {
        return getCharacterStat().getGender();
    }

    public int getJob() {
        return getCharacterStat().getJob();
    }

    public void setJob(int jobId) {
        getCharacterStat().setJob((short) jobId);
        write(WvsContext.statChanged(Stat.JOB, (short) getJob(), true));
        getField().broadcastPacket(UserRemote.effect(this, Effect.jobChanged()), this);
        validateStat();
        getConnectedServer().notifyUserUpdate(this);
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
        // Update party
        getField().getUserPool().forEachPartyMember(this, (member) -> {
            member.write(UserRemote.receiveHp(this));
        });
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
        final Map<Stat, Object> addExpResult = getCharacterStat().addExp(exp, getBasicStat().getInt());
        write(WvsContext.statChanged(addExpResult, true));
        // Level up
        if (addExpResult.containsKey(Stat.LEVEL)) {
            getField().broadcastPacket(UserRemote.effect(this, Effect.levelUp()), this);
            validateStat();
            setHp(getMaxHp());
            setMp(getMaxMp());
            getConnectedServer().notifyUserUpdate(this);
        }
    }

    public int getPop() {
        return getBasicStat().getPop();
    }

    public void addPop(int pop) {
        final short newPop = (short) Math.min(getPop() + pop, Short.MAX_VALUE);
        getCharacterStat().setPop(newPop);
        validateStat();
        write(WvsContext.statChanged(Stat.POP, newPop, true));
    }

    public int getSkillLevel(int skillId) {
        return SkillManager.getSkillLevel(getSecondaryStat(), getSkillManager(), skillId);
    }

    public int getSkillStatValue(int skillId, SkillStat stat) {
        final int slv = getSkillLevel(skillId);
        if (slv == 0) {
            return 0;
        }
        return SkillProvider.getSkillInfoById(skillId).map(skillInfo -> skillInfo.getValue(stat, slv)).orElse(0);
    }

    public void updatePassiveSkillData() {
        getPassiveSkillData().setFrom(getBasicStat(), getSecondaryStat(), getSkillManager());
    }

    public void validateStat() {
        // get_real_equip
        final Map<Integer, Item> realEquip = EquipStat.getRealEquip(this);

        // BasicStat::SetFrom
        getBasicStat().setFrom(getCharacterStat(), getForcedStat(), getSecondaryStat(), getSkillManager(), getPassiveSkillData(), realEquip);

        // SecondaryStat::SetFrom
        getSecondaryStat().setFrom(getBasicStat(), getForcedStat(), getSecondaryStat(), getSkillManager(), realEquip);

        // CWvsContext::ValidateAdditionalItemEffect - ignore

        // Adjust hp and mp
        if (getField() != null) {
            if (getHp() > getMaxHp()) {
                setHp(getMaxHp());
            }
            if (getMp() > getMaxMp()) {
                setMp(getMaxMp());
            }
        } else {
            // validateStat called before SetField
            getCharacterStat().setHp(Math.clamp(getHp(), 0, getMaxHp()));
            getCharacterStat().setMp(Math.clamp(getMp(), 0, getMaxMp()));
        }
    }

    public void setTemporaryStat(CharacterTemporaryStat cts, TemporaryStatOption option) {
        setTemporaryStat(Map.of(cts, option));
    }

    public void setTemporaryStat(Map<CharacterTemporaryStat, TemporaryStatOption> setStats) {
        for (var entry : setStats.entrySet()) {
            getSecondaryStat().getTemporaryStats().put(entry.getKey(), entry.getValue());
        }
        updatePassiveSkillData();
        validateStat();
        final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(setStats.keySet(), CharacterTemporaryStat.FLAG_SIZE);
        if (!flag.isEmpty()) {
            write(WvsContext.temporaryStatSet(getSecondaryStat(), flag));
            getField().broadcastPacket(UserRemote.temporaryStatSet(this, getSecondaryStat(), flag), this);
        }
    }

    public void resetTemporaryStat(int skillId) {
        resetTemporaryStat((cts, option) -> option.rOption == skillId);
    }

    public void resetTemporaryStat(Set<CharacterTemporaryStat> stats) {
        resetTemporaryStat((cts, option) -> stats.contains(cts));
    }

    public void resetTemporaryStat(BiPredicate<CharacterTemporaryStat, TemporaryStatOption> predicate) {
        final Set<CharacterTemporaryStat> resetStats = getSecondaryStat().resetTemporaryStat(predicate);
        updatePassiveSkillData();
        validateStat();
        final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(resetStats, CharacterTemporaryStat.FLAG_SIZE);
        if (!flag.isEmpty()) {
            write(WvsContext.temporaryStatReset(flag));
            getField().broadcastPacket(UserRemote.temporaryStatReset(this, flag), this);
        }
    }

    public void setSkillCooltime(int skillId, int cooltime) {
        if (cooltime > 0) {
            getSkillManager().setSkillCooltime(skillId, Instant.now().plus(cooltime, ChronoUnit.SECONDS));
        } else {
            getSkillManager().getSkillCooltimes().remove(skillId);
        }
        write(UserLocal.skillCooltimeSet(skillId, cooltime));
    }


    // PET METHODS --------------------------------------------------------------------------------------------

    public Pet getPet(int petIndex) {
        if (getPets().size() < petIndex) {
            return null;
        }
        return getPets().get(petIndex);
    }

    public Optional<Integer> getPetIndex(long petSn) {
        for (int i = 0; i < getPets().size(); i++) {
            if (getPets().get(i).getItemSn() == petSn) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public void setPet(Pet pet, int petIndex, boolean isMigrate) {
        assert petIndex < GameConstants.PET_COUNT_MAX && petIndex <= getPets().size();
        if (petIndex == getPets().size()) {
            getPets().add(pet);
        } else {
            getPets().set(petIndex, pet);
        }
        setPetSn(petIndex, pet.getItemSn(), isMigrate);
    }

    public void setPetSn(int petIndex, long petSn, boolean isMigrate) {
        if (petIndex == 0) {
            getCharacterStat().setPetSn1(petSn);
            if (!isMigrate) {
                write(WvsContext.statChanged(Stat.PETSN, petSn, true));
            }
        } else if (petIndex == 1) {
            getCharacterStat().setPetSn2(petSn);
            if (!isMigrate) {
                write(WvsContext.statChanged(Stat.PETSN2, petSn, true));
            }
        } else if (petIndex == 2) {
            getCharacterStat().setPetSn3(petSn);
            if (!isMigrate) {
                write(WvsContext.statChanged(Stat.PETSN3, petSn, true));
            }
        }
    }

    public boolean addPet(Pet pet, boolean isMigrate) {
        final int index = getPets().size();
        if (index >= GameConstants.PET_COUNT_MAX) {
            return false;
        }
        setPet(pet, index, isMigrate);
        return true;
    }

    public boolean removePet(int petIndex) {
        if (petIndex >= getPets().size()) {
            return false;
        }
        getPets().remove(petIndex);
        setPetSn(petIndex, 0, false);
        return true;
    }


    // SUMMONED METHODS ------------------------------------------------------------------------------------------------

    public void addSummoned(Summoned summoned) {
        final int skillId = summoned.getSkillId();
        final List<Summoned> summonedList = getSummoned().computeIfAbsent(skillId, (key) -> new ArrayList<>());
        if (!SkillConstants.isSummonMultipleSkill(skillId) && !summonedList.isEmpty()) {
            for (Summoned existing : summonedList) {
                existing.setLeaveType(SummonedLeaveType.NOT_ABLE_MULTIPLE);
                getField().broadcastPacket(SummonedPacket.summonedLeaveField(this, existing));
            }
            summonedList.clear();
        }
        summonedList.add(summoned);
        summoned.setId(getField().getNewObjectId());
        getField().broadcastPacket(SummonedPacket.summonedEnterField(this, summoned));
        summoned.setEnterType(SummonedEnterType.DEFAULT);
    }

    public void removeSummoned(Summoned summoned) {
        removeSummoned((existing) -> existing.getId() == summoned.getId());
    }

    public void removeSummoned(Predicate<Summoned> predicate) {
        final var iter = getSummoned().values().iterator();
        while (iter.hasNext()) {
            final List<Summoned> summonedList = iter.next();
            summonedList.removeIf((summoned) -> {
                if (predicate.test(summoned)) {
                    getField().broadcastPacket(SummonedPacket.summonedLeaveField(this, summoned));
                    return true;
                }
                return false;
            });
            if (summonedList.isEmpty()) {
                iter.remove();
            }
        }
    }

    public Optional<Summoned> getSummonedById(int summonedId) {
        for (List<Summoned> summonedList : getSummoned().values()) {
            for (Summoned summoned : summonedList) {
                if (summoned.getId() == summonedId) {
                    return Optional.of(summoned);
                }
            }
        }
        return Optional.empty();
    }


    // OTHER HELPER METHODS --------------------------------------------------------------------------------------------

    public int getFieldId() {
        if (getField() != null) {
            return getField().getFieldId();
        }
        return GameConstants.UNDEFINED_FIELD_ID;
    }

    public void warp(Field destination, PortalInfo portalInfo, boolean isMigrate, boolean isRevive) {
        warp(destination, portalInfo.getX(), portalInfo.getY(), portalInfo.getPortalId(), isMigrate, isRevive);
    }

    public void warp(Field destination, int x, int y, int portalId, boolean isMigrate, boolean isRevive) {
        if (getField() != null) {
            getField().removeUser(this);
        }
        setField(destination);
        setX(x);
        setY(y);
        setFoothold(destination.getFootholdBelow(x, y).map(Foothold::getFootholdId).orElse(0));
        getCharacterStat().setPosMap(destination.getFieldId());
        getCharacterStat().setPortal((byte) portalId);
        write(StagePacket.setField(this, getChannelId(), isMigrate, isRevive));
        destination.addUser(this);
        getConnectedServer().notifyUserUpdate(this);
    }

    public void write(OutPacket outPacket) {
        getClient().write(outPacket);
    }

    public void dispose() {
        write(WvsContext.statChanged(Map.of(), true));
    }

    public void logout(boolean disconnect) {
        if (disconnect && isInTransfer()) {
            return;
        }
        // Remove user from field, set spawn portal
        final Field field = getField();
        if (field != null) {
            field.removeUser(this);
            if (field.hasForcedReturn()) {
                getCharacterStat().setPosMap(field.getForcedReturn());
                getCharacterStat().setPortal((byte) 0);
            } else {
                field.getNearestStartPoint(getX(), getY()).ifPresent((pi) -> {
                    getCharacterStat().setPortal((byte) pi.getPortalId());
                });
            }
        }
        if (getTownPortal() != null) {
            getTownPortal().destroy();
            setTownPortal(null);
        }
        // Notify central server
        getConnectedServer().notifyUserDisconnect(this);
        if (disconnect) {
            getConnectedServer().submitUserPacketBroadcast(
                    getFriendManager().getBroadcastTargets(),
                    FriendPacket.notify(getCharacterId(), GameConstants.CHANNEL_OFFLINE)
            );
        }
    }

    /**
     * This should be used in unfortunate cases where a method cannot accept a {@link kinoko.util.Locked<User>} to
     * ensure that the method is called after acquiring the lock. It is preferable to submit a runnable to the
     * {@link kinoko.server.event.EventScheduler} to acquire the lock in a separate thread, but sometimes we want our
     * code to run first - e.g. before saving to database.
     *
     * @return true if the current thread has acquired the {@link kinoko.util.Lockable<User>} object.
     */
    public boolean isLocked() {
        return lock.isHeldByCurrentThread();
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
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
