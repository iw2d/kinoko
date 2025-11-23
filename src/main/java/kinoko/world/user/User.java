package kinoko.world.user;

import kinoko.handler.stage.MigrationHandler;
import kinoko.handler.user.FriendHandler;
import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.PetPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.AdminPacket;
import kinoko.packet.world.FriendPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.WzProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemSpecType;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.Server;
import kinoko.server.dialog.Dialog;
import kinoko.server.dialog.ScriptDialog;
import kinoko.server.dialog.miniroom.MiniRoom;
import kinoko.server.event.EventType;
import kinoko.server.family.FamilyTree;
import kinoko.server.guild.GuildRank;
import kinoko.server.node.ChannelServerNode;
import kinoko.server.node.Client;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.OutPacket;
import kinoko.server.party.PartyRequest;
import kinoko.server.user.AdminResultType;
import kinoko.server.user.RemoteUser;
import kinoko.util.BitFlag;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.OpenGate;
import kinoko.world.field.TownPortal;
import kinoko.world.field.life.Life;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedLeaveType;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.Item;
import kinoko.world.job.staff.SuperGM;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.PassiveSkillData;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.data.ConfigManager;
import kinoko.world.user.data.MapTransferInfo;
import kinoko.world.user.data.MiniGameRecord;
import kinoko.world.user.data.WildHunterInfo;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendStatus;
import kinoko.world.user.stat.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class User extends Life {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    private final Client client;
    private final CharacterData characterData;

    private final BasicStat basicStat = new BasicStat();
    private final ForcedStat forcedStat = new ForcedStat();
    private final SecondaryStat secondaryStat = new SecondaryStat();
    private final PassiveSkillData passiveSkillData = new PassiveSkillData();
    private final CalcDamage calcDamage = new CalcDamage();

    private final List<Pet> pets = new ArrayList<>();
    private final Map<Integer, List<Summoned>> summoned = new HashMap<>(); // skill id -> list of summons
    private final Map<Integer, Instant> schedules = new HashMap<>();
    private final AtomicInteger fieldKey = new AtomicInteger(0);

    private int messengerId;
    private PartyInfo partyInfo;
    private GuildInfo guildInfo;
    private FamilyMember familyInfo;

    private Dialog dialog;
    private Dragon dragon;
    private TownPortal townPortal;
    private OpenGate openGate;
    private int effectItemId;
    private int portableChairId;
    private boolean inCashShop = false;
    private String adBoard;
    private int dojoEnergy;
    private boolean inTransfer;
    private List<EventCoolDown> cooldowns = new ArrayList<>();
    private Instant nextCheckItemExpire;
    private boolean hidden;

    public User(Client client, CharacterData characterData) {
        this.client = client;
        this.characterData = characterData;
        this.nextCheckItemExpire = Instant.MIN;
    }

    public Client getClient() {
        return client;
    }

    public Account getAccount() {
        return client.getAccount();
    }

    public boolean isGM() {
        return getAdminLevel().isAtLeast(AdminLevel.JR_GM);
    }

    public ChannelServerNode getConnectedServer() {
        return (ChannelServerNode) client.getServerNode();
    }

    public void changeChannels(int channelID){
        if (channelID == getConnectedServer().getChannelId()){
            return;
        }

        MigrationHandler.handleTransferChannel(this, this.getAccount(), channelID);
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

    public AdminLevel getAdminLevel(){
        return characterData.getCharacterStat().getAdminLevel();
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

    public ConfigManager getConfigManager() {
        return characterData.getConfigManager();
    }

    public MiniGameRecord getMiniGameRecord() {
        return characterData.getMiniGameRecord();
    }

    public MapTransferInfo getMapTransferInfo() {
        return characterData.getMapTransferInfo();
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

    public CalcDamage getCalcDamage() {
        return calcDamage;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public Map<Integer, List<Summoned>> getSummoned() {
        return summoned;
    }

    public Map<Integer, Instant> getSchedules() {
        return schedules;
    }

    public Instant getSchedule(int skillId) {
        return schedules.getOrDefault(skillId, Instant.MAX);
    }

    public void setSchedule(int skillId, Instant nextSchedule) {
        schedules.put(skillId, nextSchedule);
    }

    public byte getFieldKey() {
        return (byte) (fieldKey.get() % 0xFF);
    }

    public byte getNextFieldKey() {
        return (byte) (fieldKey.incrementAndGet() % 0xFF);
    }

    public int getMessengerId() {
        return messengerId;
    }

    public void setMessengerId(int messengerId) {
        this.messengerId = messengerId;
    }

    public PartyInfo getPartyInfo() {
        return partyInfo != null ? partyInfo : PartyInfo.EMPTY;
    }

    public void setPartyInfo(PartyInfo partyInfo) {
        this.partyInfo = partyInfo;
        getCharacterData().setPartyId(getPartyInfo().getPartyId());
    }

    public int getPartyId() {
        return getPartyInfo().getPartyId();
    }

    public boolean hasParty() {
        return getPartyId() != 0;
    }

    public int getPartyMemberIndex() {
        return getPartyInfo().getMemberIndex();
    }

    public boolean isPartyBoss() {
        return getPartyInfo().isBoss();
    }

    public GuildInfo getGuildInfo() {
        return guildInfo != null ? guildInfo : GuildInfo.EMPTY;
    }

    public void setGuildInfo(GuildInfo guildInfo) {
        this.guildInfo = guildInfo;
        getCharacterData().setGuildId(getGuildInfo().getGuildId());
    }

    public int getGuildId() {
        return getGuildInfo().getGuildId();
    }

    public boolean hasGuild() {
        return getGuildId() != 0;
    }

    public GuildRank getGuildRank() {
        return getGuildInfo().getGuildRank();
    }

    public int getAllianceId() {
        return getGuildInfo().getAllianceId();
    }

    public boolean hasAlliance() {
        return getAllianceId() != 0;
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
        if (getDialog() instanceof ScriptDialog scriptDialog) {
            scriptDialog.close();
        } else if (getDialog() instanceof MiniRoom miniRoom) {
            miniRoom.leave(this);
        } else {
            setDialog(null);
        }
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

    public int getDojoEnergy() {
        return dojoEnergy;
    }

    public void setDojoEnergy(int newEnergy) {
        this.dojoEnergy = newEnergy;
    }

    public void resetDojoEnergy() {
        this.dojoEnergy = 0;
    }
    public EventCoolDown getCoolDownByType(EventType eventType) {
        return this.cooldowns.stream().filter(eventCoolDown -> eventCoolDown.getEventType() == eventType).toList().getFirst();
    }

    public void addCoolDown(EventType eventType, long time) {
        addCoolDown(eventType, 1, System.currentTimeMillis() + time);
    }

    public void addCoolDown(EventType eventType, int amountDone, long nextReset) {
        EventCoolDown cd = this.cooldowns.stream().filter(eventCoolDown -> eventCoolDown.getEventType() == eventType).findFirst().orElse(null);
        if (cd == null) {
            cd = new EventCoolDown(eventType, amountDone, nextReset);
            this.cooldowns.add(cd);
        } else {
            cd.setNextResetTime(nextReset);
            cd.setAmountDone(amountDone);
        }
    }

    public int getEventAmountDone(EventType eventType) {
        EventCoolDown cd = this.cooldowns.stream().filter(eventCoolDown -> eventCoolDown.getEventType() == eventType).findFirst().orElse(null);
        if (cd == null) {
            return 0;
        }
        if (System.currentTimeMillis() > cd.getNextResetTime()) {
            cd.setAmountDone(0);
        }
        return cd.getAmountDone();
    }
    public int getTownPortalIndex() {
        return hasParty() ? getPartyMemberIndex() - 1 : 0;
    }

    public OpenGate getOpenGate() {
        return openGate;
    }

    public void setOpenGate(OpenGate openGate) {
        this.openGate = openGate;
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

    public Instant getNextCheckItemExpire() {
        return nextCheckItemExpire;
    }

    public void setNextCheckItemExpire(Instant nextCheckItemExpire) {
        this.nextCheckItemExpire = nextCheckItemExpire;
    }

    // STAT METHODS ----------------------------------------------------------------------------------------------------

    public int getGender() {
        return getCharacterStat().getGender();
    }

    public int getJob() {
        return getCharacterStat().getJob();
    }

    public boolean is4thJob() {
        return getCharacterStat().getJob() % 10 == 2;
    }

    public int getLevel() {
        return getCharacterStat().getLevel();
    }

    public int getHp() {
        return getCharacterStat().getHp();
    }

    public void setHp(int hp) {
        getCharacterStat().setHp(Math.clamp(hp, 0, getMaxHp()));
        write(WvsContext.statChanged(Stat.HP, getHp(), false));
        // Update party
        getField().getUserPool().forEachPartyMember(this, (member) -> {
            member.write(UserRemote.receiveHp(this));
        });
    }

    public void heal(){
        setHp(getMaxHp());
        setMp(getMaxMp());
    }

    public void hide(boolean hide, boolean isLoggingIn) {
        this.hidden = hide;

        SecondaryStat ss = getSecondaryStat();
        CharacterTemporaryStat stat = CharacterTemporaryStat.Sneak;

        BitFlag<CharacterTemporaryStat> flag = BitFlag.from(
                Set.of(stat),
                CharacterTemporaryStat.FLAG_SIZE
        );

        ss.getTemporaryStats().put(
                stat,
                TwoStateTemporaryStat.ofTwoState(stat, 1, SuperGM.HIDE, 0)
        );

        if (hide){
            write(AdminPacket.getAdminEffect(AdminResultType.SET_HIDE_STATUS.getValue(), (byte) 1));
            if (!isLoggingIn) {
                getField().broadcastToNonGMs(UserPacket.userLeaveField(this));
            }
            // let GMs see that they are hidden with Sneak / Dark Sight
            // We do not want to broadcast this to our own user, otherwise they cannot use skills in dark sight.
            getField().getUserPool().broadcastPacketToGMs(UserRemote.temporaryStatSet(this, ss, flag), this);
        }
        else {  // unhide
            write(AdminPacket.getAdminEffect(AdminResultType.SET_HIDE_STATUS.getValue(), (byte) 0));
            ss.getTemporaryStats().remove(CharacterTemporaryStat.Sneak);
            getField().getUserPool().broadcastPacketToGMs(UserRemote.temporaryStatReset(this, flag), this);
            getField().broadcastToNonGMs(UserPacket.userEnterField(this));
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void kill() {
        setHp(0);
    }

    public void addHp(int hp) {
        setHp(getHp() + hp);
    }

    public int getMp() {
        return getCharacterStat().getMp();
    }

    public void setMp(int mp) {
        getCharacterStat().setMp(Math.clamp(mp, 0, getMaxMp()));
        write(WvsContext.statChanged(Stat.MP, getMp(), false));
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
        write(WvsContext.statChanged(addExpResult, false));
        // Level up
        if (addExpResult.containsKey(Stat.LEVEL)) {
            getField().broadcastPacket(UserRemote.effect(this, Effect.levelUp()), this);
            validateStat();
            setHp(getMaxHp());
            setMp(getMaxMp());
            getConnectedServer().notifyUserUpdate(this);
            // Max level
            if (getLevel() == GameConstants.getLevelMax(getJob())) {
                getCharacterData().setMaxLevelTime(Instant.now());
            }
        }
    }

    public int getPop() {
        return getBasicStat().getPop();
    }

    public void addPop(int pop) {
        final short newPop = (short) Math.clamp(getPop() + pop, Short.MIN_VALUE, Short.MAX_VALUE);
        getCharacterStat().setPop(newPop);
        validateStat();
        write(WvsContext.statChanged(Stat.POP, newPop, false));
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
        if (!resetStats.isEmpty()) {
            updatePassiveSkillData();
            validateStat();
            final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(resetStats, CharacterTemporaryStat.FLAG_SIZE);
            if (!flag.isEmpty()) {
                write(WvsContext.temporaryStatReset(flag));
                getField().broadcastPacket(UserRemote.temporaryStatReset(this, flag), this);
            }
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

    public List<Integer> expireSkillCooltime(Instant now) {
        final List<Integer> resetCooltimes = new ArrayList<>();
        final var iter = getSkillManager().getSkillCooltimes().entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Integer, Instant> entry = iter.next();
            final int skillId = entry.getKey();
            // Battleship durability is stored as cooltime
            if (skillId == SkillConstants.BATTLESHIP_DURABILITY) {
                continue;
            }
            // Check skill cooltime and remove
            final Instant nextAvailable = entry.getValue();
            if (now.isBefore(nextAvailable)) {
                continue;
            }
            iter.remove();
            resetCooltimes.add(skillId);
        }
        return resetCooltimes;
    }


    public void setConsumeItemEffect(ItemInfo itemInfo) {
        // Apply recovery and resolve stat ups
        int statUpDuration = 0;
        final Map<CharacterTemporaryStat, Integer> statUps = new EnumMap<>(CharacterTemporaryStat.class); // cts -> value
        final Set<CharacterTemporaryStat> resetStats = new HashSet<>();
        for (var entry : itemInfo.getItemSpecs().entrySet()) {
            final ItemSpecType specType = entry.getKey();
            switch (specType) {
                // Recovery
                case hp -> {
                    addHp(getItemBonusRecovery(itemInfo.getSpec(specType)));
                }
                case mp -> {
                    addMp(getItemBonusRecovery(itemInfo.getSpec(specType)));
                }
                case hpR -> {
                    addHp(getMaxHp() * itemInfo.getSpec(specType) / 100);
                }
                case mpR -> {
                    addMp(getMaxMp() * itemInfo.getSpec(specType) / 100);
                }
                // Reset stats
                case curse, darkness, poison, seal, weakness -> {
                    resetStats.add(specType.getStat());
                }
                // Stat ups
                case time -> {
                    statUpDuration = getItemBonusDuration(itemInfo.getSpec(specType));
                }
                case defenseAtt -> {
                    statUps.put(CharacterTemporaryStat.DefenseAtt, itemInfo.getSpec(ItemSpecType.prob));
                    statUps.put(CharacterTemporaryStat.DefenseAtt_Elem, (int) WzProvider.getString(entry.getValue()).charAt(0));
                }
                case defenseState -> {
                    statUps.put(CharacterTemporaryStat.DefenseState, itemInfo.getSpec(ItemSpecType.prob));
                    statUps.put(CharacterTemporaryStat.DefenseState_Stat, (int) WzProvider.getString(entry.getValue()).charAt(0)); // C | D | F | S | W
                }
                case respectPimmune, respectMimmune, itemupbyitem, mesoupbyitem -> {
                    statUps.put(specType.getStat(), itemInfo.getSpec(ItemSpecType.prob));
                }
                default -> {
                    final CharacterTemporaryStat cts = specType.getStat();
                    if (cts != null) {
                        statUps.put(cts, itemInfo.getSpec(specType));
                    }
                }
            }
        }
        // Apply stat ups
        if (!statUps.isEmpty()) {
            if (statUpDuration > 0) {
                final Map<CharacterTemporaryStat, TemporaryStatOption> setStats = new EnumMap<>(CharacterTemporaryStat.class);
                for (var entry : statUps.entrySet()) {
                    setStats.put(entry.getKey(), TemporaryStatOption.of(entry.getValue(), -itemInfo.getItemId(), statUpDuration));
                }
                setTemporaryStat(setStats);
            }
        }
        // Reset stats
        if (!resetStats.isEmpty()) {
            resetTemporaryStat(resetStats);
        }
    }

    private int getItemBonusRecovery(int recovery) {
        final int bonusRecoveryRate = getSkillStatValue(SkillConstants.getItemBonusRateSkill(getJob()), SkillStat.x);
        if (bonusRecoveryRate != 0) {
            return recovery * bonusRecoveryRate / 100;
        }
        return recovery;
    }

    private int getItemBonusDuration(int duration) {
        final int bonusDurationRate = getSkillStatValue(SkillConstants.getItemBonusRateSkill(getJob()), SkillStat.x);
        if (bonusDurationRate != 0) {
            return duration * bonusDurationRate / 100;
        }
        return duration;
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
                write(WvsContext.statChanged(Stat.PETSN, petSn, false));
            }
        } else if (petIndex == 1) {
            getCharacterStat().setPetSn2(petSn);
            if (!isMigrate) {
                write(WvsContext.statChanged(Stat.PETSN2, petSn, false));
            }
        } else if (petIndex == 2) {
            getCharacterStat().setPetSn3(petSn);
            if (!isMigrate) {
                write(WvsContext.statChanged(Stat.PETSN3, petSn, false));
            }
        }
    }

    public boolean addPet(Pet pet, boolean isMigrate) {
        final int index = getPets().size();
        if (index >= GameConstants.PET_COUNT_MAX) {
            return false;
        }
        if (getPetIndex(pet.getItemSn()).isPresent()) {
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

    public void updatePets(Instant now) {
        final var iter = pets.iterator();
        while (iter.hasNext()) {
            final Pet pet = iter.next();
            int reason = 0;
            if (pet.updateFullness(now)) {
                reason = 1; // The pet went back home because it's hungry.
            } else if (pet.updateRemainLife(now)) {
                reason = 2; // The pet's magical time has run out and so it has turned back into a doll.
            }
            // Deactivate pet
            if (reason != 0) {
                final int petIndex = pet.getPetIndex();
                setPetSn(petIndex, 0, false);
                getField().broadcastPacket(PetPacket.petDeactivated(this, petIndex, reason));
                iter.remove();
            }
        }
    }


    // SUMMONED METHODS ------------------------------------------------------------------------------------------------

    public void addSummoned(Summoned summoned) {
        final int skillId = summoned.getSkillId();
        final List<Summoned> summonedList = getSummoned().computeIfAbsent(skillId, (key) -> new ArrayList<>());
        if (!SkillConstants.isSummonMultipleSkill(skillId) && !summonedList.isEmpty()) {
            for (Summoned existing : summonedList) {
                existing.setLeaveType(SummonedLeaveType.NOT_ABLE_MULTIPLE);
                getField().getSummonedPool().removeSummoned(this, existing);
            }
            summonedList.clear();
        }
        summonedList.add(summoned);
        getField().getSummonedPool().addSummoned(this, summoned);
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
                    getField().getSummonedPool().removeSummoned(this, summoned);
                    return true;
                }
                return false;
            });
            if (summonedList.isEmpty()) {
                iter.remove();
            }
        }
    }

    public Optional<Summoned> getSummonedBySkillId(int skillId) {
        final List<Summoned> summonedList = getSummoned().getOrDefault(skillId, List.of());
        if (summonedList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(summonedList.getFirst());
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
        setFoothold(destination.getFootholdBelow(x, y).map(Foothold::getSn).orElse(0));
        getCharacterStat().setPosMap(destination.getFieldId());
        getCharacterStat().setPortal((byte) portalId);
        if (isMigrate) {
            completeWarp(destination, true, isRevive);
        } else {
            ServerExecutor.submit(destination, () -> {
                completeWarp(destination, false, isRevive);
            });
        }
    }

    public void warp(int newFieldId, boolean isMigrate, boolean isRevive) {
        if (getFieldId() == newFieldId){
            return;
        }

        final Optional<Field> fieldResult = getConnectedServer().getFieldById(newFieldId);
        if (fieldResult.isEmpty()) {
            systemMessage("A system error has occurred when changing maps.");
            log.error("Field with ID {} does not exist.", newFieldId);
            return;
        }

        final Field targetField = fieldResult.get();

        // get the default portal
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(GameConstants.DEFAULT_PORTAL_NAME);
        if (portalResult.isEmpty()) {
            systemMessage("A system error has occurred when deciding the entry portal.");
            log.error("Portal {} does not exist in Field {}", GameConstants.DEFAULT_PORTAL_NAME, newFieldId);
            return;
        }

        final PortalInfo targetPortalInfo = portalResult.get();

        this.warp(targetField, targetPortalInfo, isMigrate, isRevive);
    }

    public void warpTo(User user){
        changeChannels(user.getChannelId());
        warp(user.getField(), user.getX(), user.getY(), 0, false, false);
    }

    public void warpTo(RemoteUser user){
        changeChannels(user.getChannelId());
        warp(user.getFieldId(), false, false);
    }

    private void completeWarp(Field destination, boolean isMigrate, boolean isRevive) {
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
        // Remove user from field
        final Field field = getField();
        if (field != null) {
            field.removeUser(this);
            // Set spawn point
            if (field.hasForcedReturn()) {
                getCharacterStat().setPosMap(field.getForcedReturn());
                getCharacterStat().setPortal((byte) 0);
            } else {
                field.getNearestStartPoint(getX(), getY()).ifPresent((pi) -> {
                    getCharacterStat().setPortal((byte) pi.getPortalId());
                });
            }
            // Assign new party leader on disconnect
            if (disconnect && isPartyBoss()) {
                for (User member : field.getUserPool().getPartyMembers(getPartyId())) {
                    getConnectedServer().submitPartyRequest(this, PartyRequest.changePartyBoss(member.getCharacterId(), true));
                    break;
                }
            }
        }
        // Remove town portal
        if (getTownPortal() != null) {
            getTownPortal().destroy();
            setTownPortal(null);
        }
        // Notify central server
        getConnectedServer().notifyUserDisconnect(this);
        // Notify friends
        if (disconnect) {
            FriendHandler.loadFriends(this, (friendMap) -> {
                final List<Integer> friendIds = friendMap.values().stream()
                        .filter((friend) -> friend.getStatus() == FriendStatus.NORMAL)
                        .map(Friend::getFriendId)
                        .toList();
                if (!friendIds.isEmpty()) {
                    getConnectedServer().submitUserPacketBroadcast(friendIds, FriendPacket.notify(getCharacterId(), GameConstants.CHANNEL_OFFLINE, false));
                }
            });
        }
    }

    public boolean isInCashShop() {
        return inCashShop;
    }

    public void setInCashShop(boolean inCashShop) {
        this.inCashShop = inCashShop;
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


    // UTILITY ---------------------------------------------------------------------------------------------------------
    public void systemMessage(String text, Object... args){
        write(MessagePacket.system(text, args));
    }

    public FamilyMember getFamilyInfo() {
        return familyInfo;
    }

    public Optional<FamilyTree> getFamilyTree() {
        return Server.getCentralServerNode().getFamilyTree(this.getCharacterId());
    }

    /**
     * Calculates the effective drop rate modifier for the user, taking into account
     * both their personal family drop modifier and the modifiers from their family tree.
     *
     * The method ensures that the returned modifier is never less than 1.0.
     *
     * @return the highest applicable drop modifier between personal and family values,
     *         with a minimum of 1.0
     */
    public double getFamilyDropModifier() {
        double personalModifier = GameConstants.DEFAULT_FAMILY_PERSONAL_DROP_MODIFIER;
        double familyModifier = GameConstants.DEFAULT_FAMILY_DROP_MODIFIER;
        if (this.familyInfo != null && this.familyInfo.hasFamily()){
            personalModifier = this.familyInfo.getDropModifier();
        }

        Optional<FamilyTree> userTreeOpt = getFamilyTree();
        if (userTreeOpt.isPresent()) {
            familyModifier = userTreeOpt.get().getDropModifier();
        }

        return Math.max(GameConstants.DEFAULT_FAMILY_DROP_MODIFIER, Math.max(personalModifier, familyModifier));
    }

    /**
     * Calculates the effective experience (EXP) modifier for the user, considering
     * both their personal family EXP modifier and any modifiers from their family tree.
     *
     * The method ensures that the returned modifier is never less than 1.0.
     *
     * @return the highest applicable EXP modifier between personal and family values,
     *         with a minimum of 1.0
     */
    public double getFamilyEXPModifier() {
        double personalModifier = GameConstants.DEFAULT_FAMILY_PERSONAL_EXP_MODIFIER;
        double familyModifier = GameConstants.DEFAULT_FAMILY_EXP_MODIFIER;

        if (this.familyInfo != null && this.familyInfo.hasFamily()) {
            personalModifier = this.familyInfo.getExpModifier();
        }

        Optional<FamilyTree> userTreeOpt = getFamilyTree();
        if (userTreeOpt.isPresent()) {
            familyModifier = userTreeOpt.get().getExpModifier();
        }

        return Math.max(GameConstants.DEFAULT_FAMILY_EXP_MODIFIER, Math.max(personalModifier, familyModifier));
    }

    /**
     * Sets the user's FamilyMember info and updates the last login time
     * if the user has been a part of a family before.
     *
     * @param familyInfo the FamilyMember data to assign
     */
    public void setFamilyInfo(FamilyMember familyInfo) {
        this.familyInfo = familyInfo;
        if (this.familyInfo != null && !this.familyInfo.isDefault()){
            this.familyInfo.updateLastLogin();
        }
    }
}
