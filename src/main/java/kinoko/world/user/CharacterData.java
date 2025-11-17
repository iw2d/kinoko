package kinoko.world.user;

import kinoko.database.DatabaseManager;
import kinoko.server.dialog.miniroom.MiniRoomType;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.FileTime;
import kinoko.world.item.BodyPart;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.Item;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestManager;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.data.*;
import kinoko.world.user.stat.CharacterStat;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class CharacterData implements Encodable {
    private final int accountId;
    private CharacterStat characterStat;
    private InventoryManager inventoryManager;
    private SkillManager skillManager;
    private QuestManager questManager;
    private ConfigManager configManager;
    private PopularityRecord popularityRecord;
    private MiniGameRecord miniGameRecord;
    private CoupleRecord coupleRecord;
    private MapTransferInfo mapTransferInfo;
    private WildHunterInfo wildHunterInfo;
    private AtomicInteger itemSnCounter;
    private int friendMax;
    private int partyId;
    private int guildId;
    private Instant creationTime;
    private Instant maxLevelTime;

    public CharacterData(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }

    public CharacterStat getCharacterStat() {
        return characterStat;
    }

    public void setCharacterStat(CharacterStat characterStat) {
        this.characterStat = characterStat;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public void setQuestManager(QuestManager questManager) {
        this.questManager = questManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public PopularityRecord getPopularityRecord() {
        return popularityRecord;
    }

    public void setPopularityRecord(PopularityRecord popularityRecord) {
        this.popularityRecord = popularityRecord;
    }

    public MiniGameRecord getMiniGameRecord() {
        return miniGameRecord;
    }

    public void setMiniGameRecord(MiniGameRecord miniGameRecord) {
        this.miniGameRecord = miniGameRecord;
    }

    public CoupleRecord getCoupleRecord() {
        return coupleRecord;
    }

    public void setCoupleRecord(CoupleRecord coupleRecord) {
        this.coupleRecord = coupleRecord;
    }

    public MapTransferInfo getMapTransferInfo() {
        return mapTransferInfo;
    }

    public void setMapTransferInfo(MapTransferInfo mapTransferInfo) {
        this.mapTransferInfo = mapTransferInfo;
    }

    public WildHunterInfo getWildHunterInfo() {
        return wildHunterInfo;
    }

    public void setWildHunterInfo(WildHunterInfo wildHunterInfo) {
        this.wildHunterInfo = wildHunterInfo;
    }

    public AtomicInteger getItemSnCounter() {
        return DatabaseManager.isRelational() ? new AtomicInteger(-1) : itemSnCounter;
    }

    public void setItemSnCounter(AtomicInteger itemSnCounter) {
        this.itemSnCounter = DatabaseManager.isRelational() ? new AtomicInteger(-1) : itemSnCounter;
    }

    public int getFriendMax() {
        return friendMax;
    }

    public void setFriendMax(int friendMax) {
        this.friendMax = friendMax;
    }

    public int getPartyId() {
        return partyId;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }

    public int getGuildId() {
        return guildId;
    }

    public void setGuildId(int guildId) {
        this.guildId = guildId;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getMaxLevelTime() {
        return maxLevelTime;
    }

    public void setMaxLevelTime(Instant maxLevelTime) {
        this.maxLevelTime = maxLevelTime;
    }

    public long getNextItemSn() {
        if (DatabaseManager.isRelational()) {
            // Generate temporary unique SN for PostgreSQL using timestamp + random bits
            // This prevents in-memory collisions before database save
            // Database will replace with actual sequence value during save
            long timestamp = System.currentTimeMillis();
            long random = (long) (Math.random() * 1048576); // 20 bits of randomness
            long counter = itemSnCounter.getAndIncrement() & 0x3; // 2 bits of counter
            return -(timestamp << 22 | random << 2 | counter); // Negative to distinguish from real SNs
        }

        return ((long) itemSnCounter.getAndIncrement()) | (((long) getCharacterId()) << 32);
    }

    public int getCharacterId() {
        return characterStat.getId();
    }

    public String getCharacterName() {
        return characterStat.getName();
    }

    public AvatarLook getAvatarLook() {
        return AvatarLook.from(characterStat, inventoryManager.getEquipped(), inventoryManager.getCashInventory());
    }

    public void encodeCharacterData(DBChar flag, OutPacket outPacket) {
        outPacket.encodeLong(flag.getValue());
        outPacket.encodeByte(0); // nCombatOrders
        outPacket.encodeByte(false); // bool -> byte, int * FT, int * FT

        if (flag.hasFlag(DBChar.CHARACTER)) {
            characterStat.encode(outPacket);
            outPacket.encodeByte(friendMax); // nFriendMax
            outPacket.encodeByte(false); // sLinkedCharacter: bool -> str
        }
        if (flag.hasFlag(DBChar.MONEY)) {
            outPacket.encodeInt(inventoryManager.getMoney()); // nMoney
        }
        if (flag.hasFlag(DBChar.INVENTORYSIZE)) {
            outPacket.encodeByte(inventoryManager.getEquipInventory().getSize());
            outPacket.encodeByte(inventoryManager.getConsumeInventory().getSize());
            outPacket.encodeByte(inventoryManager.getInstallInventory().getSize());
            outPacket.encodeByte(inventoryManager.getEtcInventory().getSize());
            outPacket.encodeByte(inventoryManager.getCashInventory().getSize());
        }
        if (flag.hasFlag(DBChar.EQUIPEXT)) {
            outPacket.encodeFT(inventoryManager.getExtSlotExpire()); // aEquipExtExpire
        }
        if (flag.hasFlag(DBChar.ITEMSLOTEQUIP)) {
            final Map<Integer, Item> equippedItems = inventoryManager.getEquipped().getItems();
            // Normal Equipped Items
            for (var entry : equippedItems.entrySet()) {
                final int bodyPart = entry.getKey();
                if (bodyPart >= BodyPart.EQUIPPED_BASE.getValue() && bodyPart < BodyPart.EQUIPPED_END.getValue()) {
                    outPacket.encodeShort(bodyPart);
                    entry.getValue().encode(outPacket);
                }
            }
            outPacket.encodeShort(0);
            // Cash Equipped Items
            for (var entry : equippedItems.entrySet()) {
                final int bodyPart = entry.getKey();
                if (bodyPart >= BodyPart.CASH_BASE.getValue() && bodyPart < BodyPart.CASH_END.getValue()) {
                    outPacket.encodeShort(bodyPart - BodyPart.CASH_BASE.getValue());
                    entry.getValue().encode(outPacket);
                }
            }
            outPacket.encodeShort(0);

            // Equip Inventory
            for (var entry : inventoryManager.getEquipInventory().getItems().entrySet()) {
                outPacket.encodeShort(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeShort(0);

            // Dragon Equips
            for (var entry : equippedItems.entrySet()) {
                final int bodyPart = entry.getKey();
                if (bodyPart >= BodyPart.DRAGON_BASE.getValue() && bodyPart < BodyPart.DRAGON_END.getValue()) {
                    outPacket.encodeShort(bodyPart);
                    entry.getValue().encode(outPacket);
                }
            }
            outPacket.encodeShort(0);
            // Mechanic Equips
            for (var entry : equippedItems.entrySet()) {
                final int bodyPart = entry.getKey();
                if (bodyPart >= BodyPart.MECHANIC_BASE.getValue() && bodyPart < BodyPart.MECHANIC_END.getValue()) {
                    outPacket.encodeShort(bodyPart);
                    entry.getValue().encode(outPacket);
                }
            }
            outPacket.encodeShort(0);
        }
        if (flag.hasFlag(DBChar.ITEMSLOTCONSUME)) {
            for (var entry : inventoryManager.getConsumeInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.ITEMSLOTINSTALL)) {
            for (var entry : inventoryManager.getInstallInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.ITEMSLOTETC)) {
            for (var entry : inventoryManager.getEtcInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.ITEMSLOTCASH)) {
            for (var entry : inventoryManager.getCashInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.SKILLRECORD)) {
            outPacket.encodeShort(skillManager.getSkillRecords().size());
            for (SkillRecord sr : skillManager.getSkillRecords()) {
                outPacket.encodeInt(sr.getSkillId());
                outPacket.encodeInt(sr.getSkillLevel());
                outPacket.encodeFT(FileTime.DEFAULT_TIME); // mSkillExpired
                if (SkillConstants.isSkillNeedMasterLevel(sr.getSkillId())) {
                    outPacket.encodeInt(sr.getMasterLevel());
                }
            }
        }
        if (flag.hasFlag(DBChar.SKILLCOOLTIME)) {
            final Map<Integer, Long> cooltimes = new HashMap<>();
            final Instant now = Instant.now();
            final var iter = skillManager.getSkillCooltimes().entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry<Integer, Instant> entry = iter.next();
                final int skillId = entry.getKey();
                final Instant end = entry.getValue();
                // Battleship durability is stored as cooltime
                if (skillId == SkillConstants.BATTLESHIP_DURABILITY) {
                    cooltimes.put(skillId, end.getEpochSecond());
                } else {
                    if (now.isAfter(end)) {
                        iter.remove();
                        continue;
                    }
                    cooltimes.put(skillId, Duration.between(now, end).getSeconds());
                }

            }
            outPacket.encodeShort(cooltimes.size());
            for (var entry : cooltimes.entrySet()) {
                outPacket.encodeInt(entry.getKey());
                outPacket.encodeShort(entry.getValue().shortValue());
            }
        }
        if (flag.hasFlag(DBChar.QUESTRECORD)) {
            final List<QuestRecord> questRecords = questManager.getStartedQuests();
            outPacket.encodeShort(questRecords.size());
            for (QuestRecord qr : questRecords) {
                outPacket.encodeShort(qr.getQuestId());
                outPacket.encodeString(qr.getValue());
            }
        }
        if (flag.hasFlag(DBChar.QUESTCOMPLETE)) {
            final List<QuestRecord> questRecords = questManager.getCompletedQuests();
            outPacket.encodeShort(questRecords.size());
            for (QuestRecord qr : questRecords) {
                outPacket.encodeShort(qr.getQuestId());
                outPacket.encodeFT(qr.getCompletedTime());
            }
        }
        if (flag.hasFlag(DBChar.MINIGAMERECORD)) {
            outPacket.encodeShort(2); // 2 * GW_MiniGameRecord { nGameID, nWin, nDraw, nLose, nScore }
            miniGameRecord.encode(MiniRoomType.OmokRoom, outPacket);
            miniGameRecord.encode(MiniRoomType.MemoryGameRoom, outPacket);
        }
        if (flag.hasFlag(DBChar.COUPLERECORD)) {
            coupleRecord.encodeForLocal(outPacket);
        }
        if (flag.hasFlag(DBChar.MAPTRANSFER)) {
            mapTransferInfo.encodeMapTransfer(outPacket); // adwMapTransfer
            mapTransferInfo.encodeMapTransferEx(outPacket); // adwMapTransferEx
        }
        if (flag.hasFlag(DBChar.NEWYEARCARD)) {
            outPacket.encodeShort(0); // short * GW_NewYearCardRecord
        }
        if (flag.hasFlag(DBChar.QUESTRECORDEX)) {
            final List<QuestRecord> questRecords = questManager.getExQuests();
            outPacket.encodeShort(questRecords.size());
            for (QuestRecord qr : questRecords) {
                outPacket.encodeShort(qr.getQuestId());
                outPacket.encodeString(qr.getValue());
            }
        }
        if (flag.hasFlag(DBChar.WILDHUNTERINFO) &&
                JobConstants.isWildHunterJob(characterStat.getJob())) {
            wildHunterInfo.encode(outPacket);
        }
        if (flag.hasFlag(DBChar.QUESTCOMPLETEOLD)) {
            outPacket.encodeShort(0); // short * (short, FT)
        }
        if (flag.hasFlag(DBChar.VISITORLOG)) {
            outPacket.encodeShort(0); // short * (short, short)
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        encodeCharacterData(DBChar.ALL, outPacket);
    }
}
