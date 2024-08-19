package kinoko.script.common;

import kinoko.packet.field.FieldEffectPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.NpcPacket;
import kinoko.packet.user.DragonPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.MobProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.StringProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.reward.Reward;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.dialog.ScriptDialog;
import kinoko.server.event.EventState;
import kinoko.server.event.EventType;
import kinoko.server.field.Instance;
import kinoko.server.field.InstanceFieldStorage;
import kinoko.server.packet.OutPacket;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.npc.Npc;
import kinoko.world.item.*;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.Dragon;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import kinoko.world.user.stat.StatConstants;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class ScriptManagerImpl implements ScriptManager {
    private final User user;
    private final Field field;
    private final FieldObject source;
    private final ScriptMemory scriptMemory;
    private final Set<ScriptMessageParam> messageParams;

    private int speakerId;
    private CompletableFuture<ScriptAnswer> answerFuture;

    public ScriptManagerImpl(User user, Field field, FieldObject source, int speakerId) {
        this.user = user;
        this.field = field;
        this.source = source;
        this.speakerId = speakerId;
        this.scriptMemory = new ScriptMemory();
        this.messageParams = EnumSet.noneOf(ScriptMessageParam.class);
    }

    public void submitAnswer(ScriptAnswer answer) {
        answerFuture.complete(answer);
    }

    public void close() {
        answerFuture.completeExceptionally(ScriptTermination.getInstance());
        user.setDialog(null);
    }


    // USER METHODS ----------------------------------------------------------------------------------------------------
    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void dispose() {
        user.dispose();
    }

    @Override
    public void write(OutPacket outPacket) {
        user.write(outPacket);
    }

    @Override
    public void message(String message) {
        user.write(MessagePacket.system(message));
    }

    @Override
    public void scriptProgressMessage(String message) {
        user.write(WvsContext.scriptProgressMessage(message));
    }

    @Override
    public void playPortalSE() {
        user.write(UserLocal.effect(Effect.playPortalSE()));
    }

    @Override
    public void balloonMsg(String text, int width, int duration) {
        user.write(UserLocal.balloonMsg(text, width, duration));
    }

    @Override
    public void setDirectionMode(boolean set, int delay) {
        user.write(UserLocal.setDirectionMode(set, delay));
    }

    @Override
    public void avatarOriented(String effectPath) {
        user.write(UserLocal.effect(Effect.avatarOriented(effectPath)));
    }

    @Override
    public void squibEffect(String effectPath) {
        user.write(UserLocal.effect(Effect.squibEffect(effectPath)));
    }

    @Override
    public void reservedEffect(String effectPath) {
        user.write(UserLocal.effect(Effect.reservedEffect(effectPath)));
    }

    @Override
    public void screenEffect(String effectPath) {
        user.write(FieldEffectPacket.screen(effectPath));
    }

    @Override
    public void soundEffect(String effectPath) {
        user.write(FieldEffectPacket.sound(effectPath));
    }


    // STAT METHODS ----------------------------------------------------------------------------------------------------

    @Override
    public int getGender() {
        return user.getGender();
    }

    @Override
    public int getLevel() {
        return user.getLevel();
    }

    @Override
    public void addExp(int exp) {
        user.addExp(exp);
        user.write(MessagePacket.incExp(exp, 0, true, true));
    }

    @Override
    public void setJob(Job job) {
        // Set job
        final CharacterStat cs = user.getCharacterStat();
        cs.setJob(job.getJobId());
        // Assign minimum stats
        final int sumAp = StatConstants.getSumAp(user.getLevel(), cs.getJob(), cs.getSubJob());
        switch (job) {
            case WARRIOR, DAWN_WARRIOR_1, ARAN_1 -> {
                cs.setBaseStr((short) 35);
                cs.setBaseDex((short) 4);
                cs.setBaseInt((short) 4);
                cs.setBaseLuk((short) 4);
                cs.setAp((short) (sumAp - (35 + 4 + 4 + 4)));
            }
            case MAGICIAN, BLAZE_WIZARD_1, EVAN_1, BATTLE_MAGE_1 -> {
                cs.setBaseStr((short) 4);
                cs.setBaseDex((short) 4);
                cs.setBaseInt((short) 20);
                cs.setBaseLuk((short) 4);
                cs.setAp((short) (sumAp - (4 + 4 + 20 + 4)));
            }
            case ARCHER, WIND_ARCHER_1, WILD_HUNTER_1, ROGUE, NIGHT_WALKER_1 -> {
                cs.setBaseStr((short) 4);
                cs.setBaseDex((short) 25);
                cs.setBaseInt((short) 4);
                cs.setBaseLuk((short) 4);
                cs.setAp((short) (sumAp - (4 + 25 + 4 + 4)));
            }
            case PIRATE, THUNDER_BREAKER_1, MECHANIC_1 -> {
                cs.setBaseStr((short) 4);
                cs.setBaseDex((short) 20);
                cs.setBaseInt((short) 4);
                cs.setBaseLuk((short) 4);
                cs.setAp((short) (sumAp - (4 + 20 + 4 + 4)));
            }
        }
        // Add max hp / mp for specific jobs, TODO: Evan, Resistance
        switch (job) {
            case WARRIOR, DAWN_WARRIOR_1 -> {
                cs.setMaxHp(cs.getMaxHp() + Util.getRandom(200, 250));
            }
            case FIGHTER, DAWN_WARRIOR_2 -> {
                cs.setMaxHp(cs.getMaxHp() + Util.getRandom(300, 350));
            }
            case PAGE, SPEARMAN, MAGICIAN, BLAZE_WIZARD_1, ARAN_2 -> {
                cs.setMaxMp(cs.getMaxMp() + Util.getRandom(100, 150));
            }
            case MAGE_FP, MAGE_IL, CLERIC, BLAZE_WIZARD_2 -> {
                cs.setMaxMp(cs.getMaxMp() + Util.getRandom(450, 500));
            }
            case ARCHER, ROGUE, PIRATE, WIND_ARCHER_1, NIGHT_WALKER_1, THUNDER_BREAKER_1 -> {
                cs.setMaxHp(cs.getMaxHp() + Util.getRandom(100, 150));
                cs.setMaxMp(cs.getMaxMp() + Util.getRandom(25, 50));
            }
            case HUNTER, CROSSBOWMAN, ASSASSIN, BANDIT, BRAWLER, GUNSLINGER, WIND_ARCHER_2, NIGHT_WALKER_2, THUNDER_BREAKER_2 -> {
                cs.setMaxHp(cs.getMaxHp() + Util.getRandom(300, 350));
                cs.setMaxMp(cs.getMaxMp() + Util.getRandom(150, 200));
            }
            case ARAN_1 -> {
                cs.setMaxHp(cs.getMaxHp() + Util.getRandom(250, 300));
                cs.setMaxMp(cs.getMaxMp() + Util.getRandom(10, 20));
            }
        }
        // Add ap by job level
        final int jobId = job.getJobId();
        final int jobLevel = JobConstants.getJobLevel(jobId);
        if (JobConstants.isEvanJob(jobId)) {
            if (jobId >= 2214 && jobId <= 2218) {
                cs.setAp((short) (cs.getAp() + 5));
            }
        } else {
            if (job == Job.BLADE_RECRUIT || job == Job.BLADE_SPECIALIST) {
                cs.setAp((short) (cs.getAp() + 1));
            } else if (jobLevel == 3 || jobLevel == 4) {
                cs.setAp((short) (cs.getAp() + 5));
            }
        }
        // Add sp by job level
        if (JobConstants.isEvanJob(jobId)) {
            switch (jobLevel) {
                case 1 -> {
                    cs.getSp().setSp(1, Math.max(cs.getLevel() - 10, 0) * 3 + 3);
                }
                case 2, 3, 4, 5, 6, 10 -> {
                    cs.getSp().addSp(jobLevel, 3); // 1st – 6th & 10th Mastery -> Extra 3 SP at each activation level
                }
                case 7, 8, 9 -> {
                    cs.getSp().addSp(jobLevel, 5); // 7th-9th Mastery -> Extra 5 SP at each activation level
                }
            }
        } else if (JobConstants.isExtendSpJob(jobId)) {
            switch (jobLevel) {
                case 1 -> {
                    cs.getSp().setSp(jobLevel, Math.max(cs.getLevel() - 10, 0) * 3 + 1);
                }
                case 2, 3 -> {
                    cs.getSp().addSp(jobLevel, 1);
                }
                case 4 -> {
                    cs.getSp().addSp(jobLevel, 3);
                }
            }
        } else {
            switch (jobLevel) {
                case 1 -> {
                    if (job == Job.MAGICIAN) {
                        cs.getSp().setNonExtendSp(Math.max(cs.getLevel() - 8, 0) * 3 + 1);
                    } else {
                        cs.getSp().setNonExtendSp(Math.max(cs.getLevel() - 10, 0) * 3 + 1);
                    }
                }
                case 2, 3 -> {
                    if (job != Job.BLADE_RECRUIT && job != Job.BLADE_SPECIALIST) {
                        cs.getSp().addNonExtendSp(1);
                    }
                }
                case 4 -> {
                    cs.getSp().addNonExtendSp(3);
                }
            }
        }
        // Update stats
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        statMap.put(Stat.STR, cs.getBaseStr());
        statMap.put(Stat.DEX, cs.getBaseDex());
        statMap.put(Stat.INT, cs.getBaseInt());
        statMap.put(Stat.LUK, cs.getBaseLuk());
        statMap.put(Stat.MHP, cs.getMaxHp());
        statMap.put(Stat.MMP, cs.getMaxMp());
        statMap.put(Stat.AP, cs.getAp());
        statMap.put(Stat.SP, JobConstants.isExtendSpJob(jobId) ? cs.getSp() : (short) cs.getSp().getNonExtendSp());
        statMap.put(Stat.JOB, cs.getJob());
        user.write(WvsContext.statChanged(statMap, false));
        user.getField().broadcastPacket(UserRemote.effect(user, Effect.jobChanged()), user);
        // Update skills
        final List<SkillRecord> skillRecords = new ArrayList<>();
        for (int skillRoot : JobConstants.getSkillRootFromJob(jobId)) {
            if (JobConstants.isBeginnerJob(skillRoot)) {
                continue;
            }
            for (SkillInfo si : SkillProvider.getSkillsForJob(job)) {
                if (si.isInvisible()) {
                    continue;
                }
                final SkillRecord sr = si.createRecord();
                sr.setSkillLevel(0);
                sr.setMasterLevel(SkillConstants.isSkillNeedMasterLevel(si.getSkillId()) ? 0 : si.getMaxLevel());
                user.getSkillManager().addSkill(sr);
                skillRecords.add(sr);
            }
        }
        user.updatePassiveSkillData();
        user.validateStat();
        user.write(WvsContext.changeSkillRecordResult(skillRecords, false));
        // Additional handling
        if (JobConstants.isDragonJob(jobId)) {
            final Dragon dragon = new Dragon(jobId);
            user.setDragon(dragon);
            user.getField().broadcastPacket(DragonPacket.dragonEnterField(user, dragon));
        } else if (JobConstants.isWildHunterJob(jobId)) {
            user.write(WvsContext.wildHunterInfo(user.getWildHunterInfo()));
        }
        user.getConnectedServer().notifyUserUpdate(user);
    }

    @Override
    public void setAvatar(int look) {
        if (look >= 0 && look <= GameConstants.SKIN_MAX) {
            user.getCharacterStat().setSkin((byte) look);
            user.write(WvsContext.statChanged(Stat.SKIN, user.getCharacterStat().getSkin(), false));
            user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
        } else if (look >= GameConstants.FACE_MIN && look <= GameConstants.FACE_MAX) {
            if (StringProvider.getItemName(look) == null) {
                throw new ScriptError("Tried to set face with invalid ID : %d", look);
            }
            user.getCharacterStat().setFace(look);
            user.write(WvsContext.statChanged(Stat.FACE, user.getCharacterStat().getFace(), false));
            user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
        } else if (look >= GameConstants.HAIR_MIN && look <= GameConstants.HAIR_MAX) {
            if (StringProvider.getItemName(look) == null) {
                throw new ScriptError("Tried to set hair with invalid ID : %d", look);
            }
            user.getCharacterStat().setHair(look);
            user.write(WvsContext.statChanged(Stat.HAIR, user.getCharacterStat().getHair(), false));
            user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
        } else {
            throw new ScriptError("Tried to set avatar with invalid ID : %d", look);
        }
    }

    @Override
    public void addSkill(int skillId, int skillLevel, int masterLevel) {
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            throw new ScriptError("Could not resolve skill info for skill ID : %d", skillId);
        }
        // Create skill record
        final SkillInfo si = skillInfoResult.get();
        final SkillRecord sr = si.createRecord();
        sr.setSkillLevel(Math.min(skillLevel, si.getMaxLevel()));
        sr.setMasterLevel(masterLevel);
        // Add skill
        user.getSkillManager().addSkill(sr);
        user.updatePassiveSkillData();
        user.validateStat();
        user.write(WvsContext.changeSkillRecordResult(sr, false));
    }

    @Override
    public void removeSkill(int skillId) {
        final Optional<SkillRecord> skillRecordResult = user.getSkillManager().getSkill(skillId);
        if (skillRecordResult.isEmpty()) {
            return;
        }
        // Update skill
        final SkillRecord sr = skillRecordResult.get();
        sr.setSkillLevel(0);
        sr.setMasterLevel(0);
        user.updatePassiveSkillData();
        user.validateStat();
        user.write(WvsContext.changeSkillRecordResult(sr, false));
    }

    @Override
    public void setConsumeItemEffect(int itemId) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            throw new ScriptError("Could not resolve item info for item ID : %d", itemId);
        }
        user.setConsumeItemEffect(itemInfoResult.get());
        user.write(MessagePacket.giveBuff(itemId));
    }

    @Override
    public void resetConsumeItemEffect(int itemId) {
        user.resetTemporaryStat(-itemId);
    }


    // INVENTORY METHODS -----------------------------------------------------------------------------------------------

    @Override
    public boolean addMoney(int money) {
        final InventoryManager im = user.getInventoryManager();
        if (!im.addMoney(money)) {
            return false;
        }
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
        user.write(MessagePacket.incMoney(money));
        return true;
    }

    @Override
    public boolean canAddMoney(int money) {
        return user.getInventoryManager().canAddMoney(money);
    }

    @Override
    public boolean addItem(int itemId, int quantity) {
        return addItems(List.of(Tuple.of(itemId, quantity)));
    }

    @Override
    public boolean addItems(List<Tuple<Integer, Integer>> items) {
        if (!canAddItems(items)) {
            return false;
        }
        // Create items
        final List<Item> itemList = new ArrayList<>();
        for (var tuple : items) {
            final int itemId = tuple.getLeft();
            final int quantity = tuple.getRight();
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty()) {
                throw new ScriptError("Could not resolve item info for item ID : %d", itemId);
            }
            final ItemInfo itemInfo = itemInfoResult.get();
            itemList.add(itemInfo.createItem(user.getNextItemSn(), Math.min(quantity, itemInfo.getSlotMax())));
        }
        // Add items to inventory
        for (Item item : itemList) {
            final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(item);
            if (addItemResult.isEmpty()) {
                throw new IllegalStateException("Failed to add item to inventory");
            }
            user.write(WvsContext.inventoryOperation(addItemResult.get(), false));
            user.write(UserLocal.effect(Effect.gainItem(item)));
        }
        return true;
    }

    @Override
    public boolean canAddItem(int itemId, int quantity) {
        return canAddItems(List.of(Tuple.of(itemId, quantity)));
    }

    @Override
    public boolean canAddItems(List<Tuple<Integer, Integer>> items) {
        return user.getInventoryManager().canAddItems(items);
    }

    @Override
    public boolean removeItem(int itemId) {
        final int itemCount = getItemCount(itemId);
        if (itemCount > 0) {
            return removeItem(itemId, itemCount);
        }
        return true;
    }

    @Override
    public boolean removeItem(int itemId, int quantity) {
        final Optional<List<InventoryOperation>> removeItemResult = user.getInventoryManager().removeItem(itemId, quantity);
        if (removeItemResult.isPresent()) {
            user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
            user.write(UserLocal.effect(Effect.gainItem(itemId, -quantity)));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeEquipped(BodyPart bodyPart) {
        final Inventory equipped = user.getInventoryManager().getEquipped();
        final Item item = equipped.removeItem(bodyPart.getValue());
        if (item == null) {
            return false;
        }
        // Equipped inventory = equip inventory with -position
        user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(InventoryType.EQUIP, -bodyPart.getValue()), false));
        return true;
    }

    @Override
    public boolean hasItem(int itemId) {
        return hasItem(itemId, 1);
    }

    @Override
    public boolean hasItem(int itemId, int quantity) {
        return user.getInventoryManager().hasItem(itemId, quantity);
    }

    @Override
    public int getItemCount(int itemId) {
        return user.getInventoryManager().getItemCount(itemId);
    }

    @Override
    public void addInventorySlots(InventoryType inventoryType, int addSlots) {
        final Inventory inventory = user.getInventoryManager().getInventoryByType(inventoryType);
        inventory.setSize(Math.min(inventory.getSize() + addSlots, GameConstants.INVENTORY_SLOT_MAX));
        user.write(WvsContext.inventoryGrow(inventoryType, inventory.getSize()));
    }

    // QUEST METHODS ---------------------------------------------------------------------------------------------------

    @Override
    public boolean hasQuestStarted(int questId) {
        return user.getQuestManager().hasQuestStarted(questId);
    }

    @Override
    public boolean hasQuestCompleted(int questId) {
        return user.getQuestManager().hasQuestCompleted(questId);
    }

    @Override
    public void forceStartQuest(int questId) {
        final QuestRecord qr = user.getQuestManager().forceStartQuest(questId);
        user.write(MessagePacket.questRecord(qr));
        user.validateStat();
    }

    @Override
    public void forceCompleteQuest(int questId) {
        final QuestRecord qr = user.getQuestManager().forceCompleteQuest(questId);
        user.write(MessagePacket.questRecord(qr));
        user.validateStat();
        // Quest complete effect
        user.write(UserLocal.effect(Effect.questComplete()));
        user.getField().broadcastPacket(UserRemote.effect(user, Effect.questComplete()), user);
    }

    @Override
    public String getQRValue(QuestRecordType questRecordType) {
        final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(questRecordType.getQuestId());
        return questRecordResult.map(QuestRecord::getValue).orElse("");
    }

    @Override
    public boolean hasQRValue(QuestRecordType questRecordType, String value) {
        return Arrays.asList(getQRValue(questRecordType).split(";")).contains(value);
    }

    @Override
    public void setQRValue(QuestRecordType questRecordType, String value) {
        final QuestRecord qr = user.getQuestManager().setQuestInfoEx(questRecordType.getQuestId(), value);
        user.write(MessagePacket.questRecord(qr));
        user.validateStat();
    }

    @Override
    public void addQRValue(QuestRecordType questRecordType, String value) {
        final String existingValue = getQRValue(questRecordType);
        if (existingValue == null || existingValue.isEmpty()) {
            setQRValue(questRecordType, value);
        } else {
            setQRValue(questRecordType, String.format("%s;%s", existingValue, value));
        }
    }

    // WARP METHODS ----------------------------------------------------------------------------------------------------

    @Override
    public void warp(int mapId) {
        final Field targetField;
        final Optional<Field> instanceFieldResult = field.getFieldStorage().getFieldById(mapId);
        if (instanceFieldResult.isPresent()) {
            targetField = instanceFieldResult.get();
        } else {
            final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(mapId);
            if (fieldResult.isEmpty()) {
                throw new ScriptError("Could not resolve field ID : %d", mapId);
            }
            targetField = fieldResult.get();
        }
        final Optional<PortalInfo> portalResult = targetField.getRandomStartPoint();
        if (portalResult.isEmpty()) {
            throw new ScriptError("Could not resolve start point portal for field ID : %d", targetField.getFieldId());
        }
        user.warp(targetField, portalResult.get(), false, false);
    }

    @Override
    public void warp(int mapId, String portalName) {
        final Field targetField;
        final Optional<Field> instanceFieldResult = field.getFieldStorage().getFieldById(mapId);
        if (instanceFieldResult.isPresent()) {
            targetField = instanceFieldResult.get();
        } else {
            final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(mapId);
            if (fieldResult.isEmpty()) {
                throw new ScriptError("Could not resolve field ID : %d", mapId);
            }
            targetField = fieldResult.get();
        }
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            throw new ScriptError("Tried to warp to portal : %s on field ID : %d", portalName, targetField.getFieldId());
        }
        user.warp(targetField, portalResult.get(), false, false);
    }

    @Override
    public void partyWarp(int mapId, String portalName) {
        final Field targetField;
        final Optional<Field> instanceFieldResult = field.getFieldStorage().getFieldById(mapId);
        if (instanceFieldResult.isPresent()) {
            targetField = instanceFieldResult.get();
        } else {
            final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(mapId);
            if (fieldResult.isEmpty()) {
                throw new ScriptError("Could not resolve field ID : %d", mapId);
            }
            targetField = fieldResult.get();
        }
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            throw new ScriptError("Tried to warp to portal : %s on field ID : %d", portalName, targetField.getFieldId());
        }
        final PortalInfo targetPortal = portalResult.get();
        // Warp user and party members in field
        field.getUserPool().forEachPartyMember(user, (member) -> {
            try (var lockedMember = member.acquire()) {
                member.warp(targetField, targetPortal, false, false);
            }
        });
        user.warp(targetField, targetPortal, false, false);
    }

    @Override
    public void warpInstance(int mapId, String portalName, int returnMap, int timeLimit) {
        warpInstance(List.of(mapId), portalName, returnMap, timeLimit);
    }

    @Override
    public void warpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit) {
        // Create instance
        final Optional<Instance> instanceResult = user.getConnectedServer().createInstance(mapIds, returnMap, timeLimit);
        if (instanceResult.isEmpty()) {
            throw new ScriptError("Could not create instance for map IDs : %s", mapIds);
        }
        final Instance instance = instanceResult.get();
        final Field targetField = instance.getFieldStorage().getFieldById(mapIds.get(0)).orElseThrow();
        // Resolve portal
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            throw new ScriptError("Tried to warp to portal : %s on field ID : %d", portalName, targetField.getFieldId());
        }
        // Warp user
        user.warp(targetField, portalResult.get(), false, false);
    }

    @Override
    public void partyWarpInstance(int mapId, String portalName, int returnMap, int timeLimit) {
        partyWarpInstance(List.of(mapId), portalName, returnMap, timeLimit);
    }

    @Override
    public void partyWarpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit) {
        // Create instance
        final Optional<Instance> instanceResult = user.getConnectedServer().createInstance(mapIds, returnMap, timeLimit);
        if (instanceResult.isEmpty()) {
            throw new ScriptError("Could not create instance for map IDs : %s", mapIds);
        }
        final Instance instance = instanceResult.get();
        final Field targetField = instance.getFieldStorage().getFieldById(mapIds.get(0)).orElseThrow();
        // Resolve portal
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            throw new ScriptError("Tried to warp to portal : %s on field ID : %d", portalName, targetField.getFieldId());
        }
        final PortalInfo targetPortal = portalResult.get();
        // Warp user and party members in field
        field.getUserPool().forEachPartyMember(user, (member) -> {
            try (var lockedMember = member.acquire()) {
                member.warp(targetField, targetPortal, false, false);
            }
        });
        user.warp(targetField, targetPortal, false, false);
    }


    // FIELD METHODS ---------------------------------------------------------------------------------------------------

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public int getFieldId() {
        return field.getFieldId();
    }

    @Override
    public void spawnMob(int templateId, MobAppearType appearType, int x, int y) {
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(templateId);
        if (mobTemplateResult.isEmpty()) {
            throw new ScriptError("Could not resolve mob template ID : %d", templateId);
        }
        final Optional<Foothold> footholdResult = field.getFootholdBelow(x, y - GameConstants.REACTOR_SPAWN_HEIGHT);
        final Mob mob = new Mob(
                mobTemplateResult.get(),
                null,
                x,
                y,
                footholdResult.map(Foothold::getSn).orElse(0)
        );
        mob.setAppearType(appearType);
        field.getMobPool().addMob(mob);
    }

    @Override
    public void dropRewards(List<Reward> rewards) {
        // Create drops from possible rewards
        final List<Drop> drops = new ArrayList<>();
        for (Reward reward : rewards) {
            // Drop probability
            if (!Util.succeedDouble(reward.getProb())) {
                continue;
            }
            // Create drop
            if (reward.isMoney()) {
                final int money = Util.getRandom(reward.getMin(), reward.getMax());
                if (money <= 0) {
                    continue;
                }
                drops.add(Drop.money(DropOwnType.USEROWN, source, money, user.getCharacterId()));
            } else {
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(reward.getItemId());
                if (itemInfoResult.isEmpty()) {
                    continue;
                }
                final int quantity = Util.getRandom(reward.getMin(), reward.getMax());
                final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), quantity);
                drops.add(Drop.item(DropOwnType.USEROWN, source, item, user.getCharacterId(), reward.getQuestId()));
            }
        }
        // Add drops to field
        source.getField().getDropPool().addDrops(drops, DropEnterType.CREATE, source.getX(), source.getY() - GameConstants.DROP_HEIGHT, 0, 200);
    }

    @Override
    public void setNpcAction(int templateId, String action) {
        final Optional<Npc> npcResult = field.getNpcPool().getByTemplateId(templateId);
        if (npcResult.isEmpty()) {
            throw new ScriptError("Could not resolve npc with template ID : %d", templateId);
        }
        final Npc npc = npcResult.get();
        user.write(NpcPacket.npcSpecialAction(npc, action));
    }


    // EVENT METHODS ---------------------------------------------------------------------------------------------------

    @Override
    public boolean checkParty(int memberCount, int levelMin) {
        final List<User> members = field.getUserPool().getPartyMembers(user.getPartyId());
        if (members.size() < memberCount) {
            return false;
        }
        for (User member : members) {
            if (member.getLevel() < levelMin) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EventState getEventState(EventType eventType) {
        final Optional<EventState> eventStateResult = user.getConnectedServer().getEventState(eventType);
        if (eventStateResult.isEmpty()) {
            throw new ScriptError("Could not resolve event state for event type : %s", eventType);
        }
        return eventStateResult.get();
    }

    @Override
    public String getInstanceVariable(String key) {
        if (field.getFieldStorage() instanceof InstanceFieldStorage instanceFieldStorage) {
            final Instance instance = instanceFieldStorage.getInstance();
            return instance.getVariable(key);
        } else {
            throw new ScriptError("Tried to get instance variable %s while not in an instance", key);
        }
    }

    @Override
    public void setInstanceVariable(String key, String value) {
        if (field.getFieldStorage() instanceof InstanceFieldStorage instanceFieldStorage) {
            final Instance instance = instanceFieldStorage.getInstance();
            instance.setVariable(key, value);
        } else {
            throw new ScriptError("Tried to set instance variable %s while not in an instance", key);
        }
    }

    @Override
    public void addExpAll(int exp) {
        addExp(exp);
        field.getUserPool().forEach((member) -> {
            if (member.getCharacterId() != user.getCharacterId()) {
                try (var lockedMember = member.acquire()) {
                    member.addExp(exp);
                }
            }
        });
    }

    @Override
    public void broadcastPacket(OutPacket outPacket) {
        field.broadcastPacket(outPacket);
    }

    @Override
    public void broadcastMessage(String message) {
        field.broadcastPacket(MessagePacket.system(message));
    }

    @Override
    public void broadcastScreenEffect(String effectPath) {
        field.broadcastPacket(FieldEffectPacket.screen(effectPath));
    }

    @Override
    public void broadcastSoundEffect(String effectPath) {
        field.broadcastPacket(FieldEffectPacket.sound(effectPath));
    }


    // CONVERSATION METHODS --------------------------------------------------------------------------------------------

    @Override
    public int getSpeakerId() {
        return speakerId;
    }

    @Override
    public void setSpeakerId(int speakerId) {
        this.speakerId = speakerId;
    }

    @Override
    public void setNotCancellable(boolean notCancellable) {
        toggleParam(ScriptMessageParam.NOT_CANCELLABLE, notCancellable);
    }

    @Override
    public void setPlayerAsSpeaker(boolean playerAsSpeaker) {
        toggleParam(ScriptMessageParam.PLAYER_AS_SPEAKER, playerAsSpeaker);
    }

    @Override
    public void setFlipSpeaker(boolean flipSpeaker) {
        toggleParam(ScriptMessageParam.FLIP_SPEAKER, flipSpeaker);
    }

    @Override
    public void sayOk(String text) {
        sendMessage(ScriptMessage.say(speakerId, getParam(), text, false, false));
        handleAnswer();
    }

    @Override
    public void sayPrev(String text) {
        sendMessage(ScriptMessage.say(speakerId, getParam(), text, true, false));
        handleAnswer();
    }

    @Override
    public void sayNext(String text) {
        sendMessage(ScriptMessage.say(speakerId, getParam(), text, false, true));
        handleAnswer();
    }

    @Override
    public void sayBoth(String text) {
        sendMessage(ScriptMessage.say(speakerId, getParam(), text, true, true));
        handleAnswer();
    }

    @Override
    public void sayImage(List<String> images) {
        sendMessage(ScriptMessage.sayImage(speakerId, getParam(), images));
        handleAnswer();
    }

    @Override
    public boolean askYesNo(String text) {
        sendMessage(ScriptMessage.ask(speakerId, getParam(), ScriptMessageType.ASKYESNO, text));
        return handleAnswer().getAction() != 0;
    }

    @Override
    public boolean askAccept(String text) {
        sendMessage(ScriptMessage.ask(speakerId, getParam(), ScriptMessageType.ASKACCEPT, text));
        return handleAnswer().getAction() != 0;
    }

    @Override
    public int askMenu(String text, Map<Integer, String> options) {
        final String optionString = options.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format("#L%d# #b%s#k#l", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
        sendMessage(ScriptMessage.ask(speakerId, getParam(), ScriptMessageType.ASKMENU, text != null ? String.join("\r\n", text, optionString) : optionString));
        final int answer = handleAnswer().getAnswer();
        if (!options.containsKey(answer)) {
            throw new ScriptError("Received unexpected answer %d for askMenu options : %s", answer, options);
        }
        return answer;
    }

    @Override
    public int askSlideMenu(int type, Map<Integer, String> options) {
        final String text = options.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format("#%d#%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());
        sendMessage(ScriptMessage.askSlideMenu(speakerId, getParam(), type, text));
        final int answer = handleAnswer().getAnswer();
        if (!options.containsKey(answer)) {
            throw new ScriptError("Received unexpected answer %d for askSlideMenu options : %s", answer, options);
        }
        return answer;
    }

    @Override
    public int askAvatar(String text, List<Integer> options) {
        sendMessage(ScriptMessage.askAvatar(speakerId, getParam(), text, options));
        final int answer = handleAnswer().getAnswer();
        if (answer < 0 || answer >= options.size()) {
            throw new ScriptError("Received unexpected answer %d for askAvatar options : %s", answer, options);
        }
        return answer;
    }

    @Override
    public int askNumber(String text, int numberDefault, int numberMin, int numberMax) {
        sendMessage(ScriptMessage.askNumber(speakerId, getParam(), text, numberDefault, numberMin, numberMax));
        final int answer = handleAnswer().getAnswer();
        if (answer < numberMin || answer > numberMax) {
            throw new ScriptError("Received number answer out of range : %d, min : %d, max %d", answer, numberMin, numberMax);
        }
        return answer;
    }

    @Override
    public String askText(String text, String textDefault, int textLengthMin, int textLengthMax) {
        sendMessage(ScriptMessage.askText(speakerId, getParam(), text, textDefault, textLengthMin, textLengthMax));
        final String answer = handleAnswer().getTextAnswer();
        if (answer.length() < textLengthMin || answer.length() > textLengthMax) {
            throw new ScriptError("Received text answer with invalid length : %d, min : %d, max %d", answer, textLengthMin, textLengthMax);
        }
        return answer;
    }

    @Override
    public String askBoxText(String text, String textDefault, int textBoxColumns, int textBoxLines) {
        sendMessage(ScriptMessage.askBoxText(speakerId, getParam(), text, textDefault, textBoxColumns, textBoxLines));
        return handleAnswer().getTextAnswer();
    }

    private byte getParam() {
        return ScriptMessageParam.from(messageParams);
    }

    private void toggleParam(ScriptMessageParam messageParam, boolean enabled) {
        if (enabled) {
            messageParams.add(messageParam);
        } else {
            messageParams.remove(messageParam);
        }
    }

    private void sendMessage(ScriptMessage scriptMessage) {
        scriptMemory.recordMessage(scriptMessage);
        if (user.hasDialog()) {
            throw new ScriptError("Tried to send script message with a dialog present");
        }
        user.setDialog(ScriptDialog.from(this));
        user.write(FieldPacket.scriptMessage(scriptMessage));
    }

    private ScriptAnswer handleAnswer() {
        // Unlock user while waiting on answer
        user.unlock();
        answerFuture = new CompletableFuture<>();
        final ScriptAnswer answer = answerFuture.join();
        answerFuture = null;
        user.lock();
        user.setDialog(null);
        // Handle answer
        if (answer.getAction() == -1 || answer.getAction() == 5) {
            throw ScriptTermination.getInstance();
        } else if (answer.getAction() == 0 && scriptMemory.isPrevPossible()) {
            // prev message in memory
            user.setDialog(ScriptDialog.from(this));
            user.write(FieldPacket.scriptMessage(scriptMemory.prevMessage()));
            return handleAnswer();
        } else if (scriptMemory.isInMemory()) {
            // next message in memory
            user.setDialog(ScriptDialog.from(this));
            user.write(FieldPacket.scriptMessage(scriptMemory.nextMessage()));
            return handleAnswer();
        }
        return answer;
    }
}
