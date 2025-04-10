package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.MobPacket;
import kinoko.packet.field.NpcPacket;
import kinoko.packet.user.*;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.QuestProvider;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.ServerConfig;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.item.*;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class UserPool extends FieldObjectPool<User> {
    public UserPool(Field field) {
        super(field);
    }

    public Optional<User> getByCharacterName(String name) {
        return getBy((user) -> user.getCharacterName().equalsIgnoreCase(name));
    }

    public synchronized void addUser(User user) {
        // Update client with existing users in pool
        forEach((existingUser) -> {
            user.write(UserPacket.userEnterField(user));
            for (Pet pet : existingUser.getPets()) {
                user.write(PetPacket.petActivated(existingUser, pet));
            }
            for (List<Summoned> summonedList : existingUser.getSummoned().values()) {
                for (Summoned summoned : summonedList) {
                    user.write(SummonedPacket.summonedEnterField(existingUser, summoned));
                }
            }
            if (existingUser.getDragon() != null) {
                user.write(DragonPacket.dragonEnterField(existingUser, existingUser.getDragon()));
            }
            if (existingUser.getOpenGate() != null) {
                user.write(FieldPacket.openGateCreated(existingUser, existingUser.getOpenGate(), false));
                if (existingUser.getOpenGate().getSecondGate() != null) {
                    user.write(FieldPacket.openGateCreated(existingUser, existingUser.getOpenGate().getSecondGate(), false));
                }
            }
        });

        // Add user to pool
        addObject(user);
        broadcastPacket(UserPacket.userEnterField(user), user);

        // Add user pets
        for (Pet pet : user.getPets()) {
            pet.setPosition(field, user.getX(), user.getY());
            broadcastPacket(PetPacket.petActivated(user, pet));
            user.write(PetPacket.petLoadExceptionList(user, pet.getPetIndex(), pet.getItemSn(), user.getConfigManager().getPetExceptionList()));
        }

        // Add user dragon
        if (user.getDragon() != null) {
            user.getDragon().setPosition(field, user.getX(), user.getY());
            broadcastPacket(DragonPacket.dragonEnterField(user, user.getDragon()));
        }

        // Add user summoned
        for (List<Summoned> summonedList : user.getSummoned().values()) {
            for (Summoned summoned : summonedList) {
                summoned.setPosition(field, user.getX(), user.getY(), user.isLeft());
                field.getSummonedPool().addSummoned(user, summoned);
            }
        }

        // Update party
        forEachPartyMember(user, (member) -> {
            user.write(UserRemote.receiveHp(member));
            member.write(UserRemote.receiveHp(user));
        });

        // Create field objects for user
        field.getMobPool().forEach((mob) -> {
            user.write(MobPacket.mobEnterField(mob));
            if (mob.getController() == null) {
                mob.setController(user);
                user.write(MobPacket.mobChangeController(mob, true));
            } else {
                user.write(MobPacket.mobChangeController(mob, false));
            }
        });
        field.getNpcPool().forEach((npc) -> {
            user.write(NpcPacket.npcEnterField(npc));
            if (npc.getController() == null) {
                npc.setController(user);
                user.write(NpcPacket.npcChangeController(npc, true));
            } else {
                user.write(NpcPacket.npcChangeController(npc, false));
            }
        });
        if (field.getNpcPool().hasNpcImitateData()) {
            user.write(WvsContext.imitatedNpcData(field.getNpcPool().getNpcImitateData()));
        }
        field.getDropPool().forEach((drop) -> {
            if (drop.isQuest()) {
                final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(drop.getQuestId());
                if (questRecordResult.isEmpty() || questRecordResult.get().getState() != QuestState.PERFORM) {
                    return;
                }
                final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(drop.getQuestId());
                if (questInfoResult.isPresent() && questInfoResult.get().hasRequiredItem(user, drop.getItem().getItemId())) {
                    return;
                }
            }
            user.write(FieldPacket.dropEnterField(drop, DropEnterType.ON_THE_FOOTHOLD, 0));
        });
        field.getReactorPool().forEach((reactor) -> {
            user.write(FieldPacket.reactorEnterField(reactor));
        });
        field.getTownPortalPool().forEach((townPortal) -> {
            final User owner = townPortal.getOwner();
            if ((!owner.hasParty() && owner.getCharacterId() == user.getCharacterId()) ||
                    (owner.hasParty() && owner.getPartyId() == user.getPartyId())) {
                if (townPortal.getTownField() == field) {
                    final Optional<PortalInfo> portalPointResult = townPortal.getTownPortalPoint();
                    if (portalPointResult.isPresent()) {
                        final PortalInfo portalPoint = portalPointResult.get();
                        user.write(FieldPacket.townPortalCreated(owner, portalPoint.getX(), portalPoint.getY(), false));
                    }
                } else {
                    user.write(FieldPacket.townPortalCreated(owner, townPortal.getX(), townPortal.getY(), false));
                }
            }
        });
        field.getAffectedAreaPool().forEach((affectedArea) -> {
            user.write(FieldPacket.affectedAreaCreated(affectedArea));
        });
    }

    public synchronized boolean removeUser(User user) {
        if (!removeObject(user)) {
            return false;
        }
        broadcastPacket(UserPacket.userLeaveField(user), user);

        // Handle controller change
        field.getMobPool().forEach((mob) -> {
            if (mob.getController() == user) {
                assignController(mob);
            }
        });
        field.getNpcPool().forEach((npc) -> {
            if (npc.getController() == user) {
                assignController(npc);
            }
        });

        // Remove summoned
        final var iter = user.getSummoned().entrySet().iterator();
        while (iter.hasNext()) {
            final var entry = iter.next(); // Skill ID, List<Summoned>
            // Remove from pool
            for (Summoned summoned : entry.getValue()) {
                field.getSummonedPool().removeSummoned(user, summoned);
            }
            // Remove from user
            if (!SkillConstants.isSummonMigrateSkill(entry.getKey())) {
                iter.remove();
            }
        }

        // Remove affected areas
        field.getAffectedAreaPool().removeByOwnerId(user.getCharacterId());

        // Remove gates
        if (user.getOpenGate() != null) {
            user.getOpenGate().destroy();
            user.setOpenGate(null);
        }

        // Remove party aura
        user.resetTemporaryStat(CharacterTemporaryStat.AURA_STAT);
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Aura)) {
            BattleMage.cancelPartyAura(user, user.getSecondaryStat().getOption(CharacterTemporaryStat.Aura).rOption);
        }
        return true;
    }

    public void updateUsers(Instant now) {
        for (User user : getObjects()) {
            // Handle CTS updates on tick
            SkillProcessor.processUpdate(user, now);
            // Expire temporary stat
            user.resetTemporaryStat((cts, option) -> now.isAfter(option.getExpireTime()));
            // Expire skill cooltimes
            for (int skillId : user.expireSkillCooltime(now)) {
                user.write(UserLocal.skillCooltimeSet(skillId, 0));
            }
            // Update pets
            user.updatePets(now);
            // Expire summoned
            user.removeSummoned((summoned) -> now.isAfter(summoned.getExpireTime()));
            // Expire town portal
            final TownPortal townPortal = user.getTownPortal();
            if (townPortal != null) {
                if (townPortal.getExpireTime().isBefore(now)) {
                    townPortal.destroy();
                    user.setTownPortal(null);
                    user.write(WvsContext.resetTownPortal());
                    user.write(MessagePacket.skillExpire(townPortal.getSkillId()));
                    user.getConnectedServer().notifyUserUpdate(user);
                }
            }
            // Expire open gate
            final OpenGate openGate = user.getOpenGate();
            if (openGate != null) {
                if (openGate.getExpireTime().isBefore(now)) {
                    openGate.destroy();
                    user.setOpenGate(null);
                }
            }
            // Expire items
            if (now.isAfter(user.getNextCheckItemExpire())) {
                user.setNextCheckItemExpire(now.plus(ServerConfig.ITEM_EXPIRE_INTERVAL, ChronoUnit.SECONDS));
                boolean itemExpired = false;
                final InventoryManager im = user.getInventoryManager();
                for (InventoryType inventoryType : List.of(InventoryType.EQUIPPED, InventoryType.EQUIP, InventoryType.CONSUME, InventoryType.INSTALL, InventoryType.ETC)) {
                    final var iter = im.getInventoryByType(inventoryType).getItems().entrySet().iterator();
                    while (iter.hasNext()) {
                        final var entry = iter.next();
                        final int position = entry.getKey();
                        final Item item = entry.getValue();
                        if (item.getDateExpire() == null || now.isBefore(item.getDateExpire())) {
                            continue;
                        }
                        // Remove item from inventory
                        iter.remove();
                        user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(
                                inventoryType == InventoryType.EQUIPPED ? InventoryType.EQUIP : inventoryType,
                                inventoryType == InventoryType.EQUIPPED ? -position : position
                        ), false));
                        user.write(MessagePacket.generalItemExpire(item.getItemId()));
                        itemExpired = true;
                    }
                }
                // Expire cash items and pets
                final var iter = im.getCashInventory().getItems().entrySet().iterator();
                while (iter.hasNext()) {
                    final var entry = iter.next();
                    final int position = entry.getKey();
                    final Item item = entry.getValue();
                    if (item.getDateExpire() == null || now.isBefore(item.getDateExpire())) {
                        continue;
                    }
                    if (item.getItemType() == ItemType.PET) {
                        // Set pet as expired - FileTime.DEFAULT_TIME should be encoded to turn them into dolls
                        item.setDateExpire(null);
                        user.write(WvsContext.inventoryOperation(InventoryOperation.newItem(InventoryType.CASH, position, item), false));
                        // Deactivate pet if required
                        final Optional<Integer> petIndexResult = user.getPetIndex(item.getItemSn());
                        if (petIndexResult.isPresent() && user.removePet(petIndexResult.get())) {
                            user.getField().broadcastPacket(PetPacket.petDeactivated(user, petIndexResult.get(), 2)); // The pet's magical time has run out and so it has turned back into a doll.
                        }
                    } else {
                        // Remove item from inventory
                        iter.remove();
                        user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(InventoryType.CASH, position), false));
                        user.write(MessagePacket.cashItemExpire(item.getItemId()));
                    }
                    itemExpired = true;
                }
                // Validate stat
                if (itemExpired) {
                    user.validateStat();
                }
            }
        }
    }


    // CONTROLLER METHODS ----------------------------------------------------------------------------------------------

    public void assignController(ControlledObject controlled) {
        final Optional<User> controllerResult = getNearestUser(controlled); // closest user to controlled object
        if (controllerResult.isEmpty()) {
            controlled.setController(null);
            return;
        }
        setController(controlled, controllerResult.get());
    }

    public void setController(ControlledObject controlled, User controller) {
        controlled.setController(controller);
        controller.write(controlled.changeControllerPacket(true));
        broadcastPacket(controlled.changeControllerPacket(false), controller);
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void broadcastPacket(OutPacket outPacket) {
        broadcastPacket(outPacket, null);
    }

    public void broadcastPacket(OutPacket outPacket, User except) {
        forEach((user) -> {
            if (except != null && user.getCharacterId() == except.getCharacterId()) {
                return;
            }
            user.write(outPacket);
        });
    }

    public Optional<User> getNearestUser(FieldObject fieldObject) {
        return fieldObject.getNearestObject(getObjects());
    }

    public List<User> getPartyMembers(int partyId) {
        if (partyId == 0) {
            return List.of();
        }
        final List<User> members = new ArrayList<>();
        for (User user : getObjects()) {
            if (user.getPartyId() == partyId) {
                members.add(user);
            }
        }
        return members;
    }

    /**
     * Does not include user.
     */
    public void forEachPartyMember(User user, Consumer<User> consumer) {
        for (User member : getPartyMembers(user.getPartyId())) {
            if (member.getCharacterId() != user.getCharacterId()) {
                consumer.accept(member);
            }
        }
    }

    /**
     * Includes Summoned owned by user.
     */
    public void forEachPartySummoned(User user, BiConsumer<User, Summoned> consumer) {
        final int partyId = user.getPartyId();
        if (partyId != 0) {
            forEach((member) -> {
                if (member.getPartyId() == partyId) {
                    member.getSummoned().forEach((id, summonedList) -> {
                        for (Summoned summoned : summonedList) {
                            consumer.accept(member, summoned);
                        }
                    });
                }
            });
        } else {
            user.getSummoned().forEach((id, summonedList) -> {
                for (Summoned summoned : summonedList) {
                    consumer.accept(user, summoned);
                }
            });
        }
    }
}
