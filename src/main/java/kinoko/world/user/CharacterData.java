package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.util.Lockable;
import kinoko.world.Encodable;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.Item;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestManager;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.temp.TemporaryStatManager;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class CharacterData implements Encodable, Lockable<CharacterData> {
    private final Lock lock = new ReentrantLock();
    private final int accountId;
    private CharacterStat characterStat;
    private TemporaryStatManager temporaryStatManager;
    private InventoryManager inventoryManager;
    private SkillManager skillManager;
    private QuestManager questManager;
    private WildHunterInfo wildHunterInfo;
    private AtomicInteger itemSnCounter;
    private int friendMax; // TODO: friend manager

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

    public TemporaryStatManager getTemporaryStatManager() {
        return temporaryStatManager;
    }

    public void setTemporaryStatManager(TemporaryStatManager temporaryStatManager) {
        this.temporaryStatManager = temporaryStatManager;
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

    public WildHunterInfo getWildHunterInfo() {
        return wildHunterInfo;
    }

    public void setWildHunterInfo(WildHunterInfo wildHunterInfo) {
        this.wildHunterInfo = wildHunterInfo;
    }

    public AtomicInteger getItemSnCounter() {
        return itemSnCounter;
    }

    public void setItemSnCounter(AtomicInteger itemSnCounter) {
        this.itemSnCounter = itemSnCounter;
    }

    public int getFriendMax() {
        return friendMax;
    }

    public void setFriendMax(int friendMax) {
        this.friendMax = friendMax;
    }

    public long getNextItemSn() {
        return ((long) itemSnCounter.getAndIncrement()) | (((long) getCharacterId()) << 32);
    }

    public int getCharacterId() {
        return characterStat.getId();
    }

    public String getCharacterName() {
        return characterStat.getName();
    }

    public AvatarLook getAvatarLook() {
        return AvatarLook.from(characterStat, inventoryManager.getEquipped());
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
        if (flag.hasFlag(DBChar.INVENTORY_SIZE)) {
            outPacket.encodeByte(inventoryManager.getEquipInventory().getSize());
            outPacket.encodeByte(inventoryManager.getConsumeInventory().getSize());
            outPacket.encodeByte(inventoryManager.getInstallInventory().getSize());
            outPacket.encodeByte(inventoryManager.getEtcInventory().getSize());
            outPacket.encodeByte(inventoryManager.getCashInventory().getSize());
        }
        if (flag.hasFlag(DBChar.EQUIP_EXT)) {
            outPacket.encodeFT(FileTime.DEFAULT_TIME); // aEquipExtExpire
        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_EQUIP)) {
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
        if (flag.hasFlag(DBChar.ITEM_SLOT_CONSUME)) {
            for (var entry : inventoryManager.getConsumeInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_INSTALL)) {
            for (var entry : inventoryManager.getInstallInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_ETC)) {
            for (var entry : inventoryManager.getEtcInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_CASH)) {
            for (var entry : inventoryManager.getCashInventory().getItems().entrySet()) {
                outPacket.encodeByte(entry.getKey());
                entry.getValue().encode(outPacket);
            }
            outPacket.encodeByte(0);
        }
        if (flag.hasFlag(DBChar.SKILL_RECORD)) {
            outPacket.encodeShort(skillManager.getSkillRecords().size());
            for (SkillRecord sr : skillManager.getSkillRecords().values()) {
                outPacket.encodeInt(sr.getSkillId());
                outPacket.encodeInt(sr.getSkillLevel());
                outPacket.encodeFT(FileTime.DEFAULT_TIME); // mSkillExpired
                if (SkillConstants.isSkillNeedMasterLevel(sr.getSkillId())) {
                    outPacket.encodeInt(sr.getMasterLevel());
                }
            }
        }
        if (flag.hasFlag(DBChar.SKILL_COOLTIME)) {
            final Map<Integer, Long> cooltimes = new HashMap<>();
            final Instant now = Instant.now();
            final var iter = skillManager.getSkillCooltimes().entrySet().iterator();
            while (iter.hasNext()) {
                final var entry = iter.next();
                final Instant end = entry.getValue();
                if (now.isAfter(end)) {
                    iter.remove();
                    continue;
                }
                cooltimes.put(entry.getKey(), Duration.between(now, end).getSeconds());
            }
            outPacket.encodeShort(cooltimes.size());
            for (var entry : cooltimes.entrySet()) {
                outPacket.encodeInt(entry.getKey());
                outPacket.encodeShort(entry.getValue().shortValue());
            }
        }
        if (flag.hasFlag(DBChar.QUEST_RECORD)) {
            final Set<QuestRecord> questRecords = questManager.getStartedQuests();
            outPacket.encodeShort(questRecords.size());
            for (QuestRecord qr : questRecords) {
                outPacket.encodeShort(qr.getQuestId());
                outPacket.encodeString(qr.getQuestInfo());
            }
        }
        if (flag.hasFlag(DBChar.QUEST_COMPLETE)) {
            final Set<QuestRecord> questRecords = questManager.getCompletedQuests();
            outPacket.encodeShort(questRecords.size());
            for (QuestRecord qr : questRecords) {
                outPacket.encodeShort(qr.getQuestId());
                outPacket.encodeFT(qr.getCompletedTime());
            }
        }
        if (flag.hasFlag(DBChar.MINIGAME_RECORD)) { // TODO
            outPacket.encodeShort(0); // short * (int, int, int, int, int)
            // GW_MiniGameRecord { nGameID, nWin, nDraw, nLose, nScore }
        }
        if (flag.hasFlag(DBChar.COUPLE_RECORD)) { // TODO
            outPacket.encodeShort(0); // short * GW_CoupleRecord::Decode
            outPacket.encodeShort(0); // short * GW_FriendRecord::Decode
            outPacket.encodeShort(0); // short * GW_MarriageRecord::Decode
        }
        if (flag.hasFlag(DBChar.MAP_TRANSFER)) { // TODO
            // adwMapTransfer
            for (int mapId : new int[5]) {
                outPacket.encodeInt(mapId);
            }
            // adwMapTransferEx
            for (int mapId : new int[10]) {
                outPacket.encodeInt(mapId);
            }
        }
        if (flag.hasFlag(DBChar.NEW_YEAR_CARD)) {
            outPacket.encodeShort(0); // short * GW_NewYearCardRecord
        }
        if (flag.hasFlag(DBChar.QUEST_RECORD_EX)) {
            final Set<QuestRecord> questRecords = questManager.getExQuests();
            outPacket.encodeShort(questRecords.size());
            for (QuestRecord qr : questRecords) {
                outPacket.encodeShort(qr.getQuestId());
                outPacket.encodeString(qr.getQuestInfo());
            }
        }
        if (flag.hasFlag(DBChar.WILD_HUNTER_INFO) &&
                JobConstants.isWildHunterJob(characterStat.getJob())) {
            wildHunterInfo.encode(outPacket);
        }
        if (flag.hasFlag(DBChar.QUEST_COMPLETE_OLD)) {
            outPacket.encodeShort(0); // short * (short, FT)
        }
        if (flag.hasFlag(DBChar.VISITOR_LOG)) {
            outPacket.encodeShort(0); // short * (short, short)
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        encodeCharacterData(DBChar.ALL, outPacket);
    }

    @Override
    public void lock() {
        characterStat.lock();
        inventoryManager.lock();
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
        inventoryManager.unlock();
        characterStat.unlock();
    }
}
