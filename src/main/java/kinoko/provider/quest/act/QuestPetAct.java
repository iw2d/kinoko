package kinoko.provider.quest.act;

import kinoko.packet.user.QuestPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.util.Locked;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;

import java.util.Optional;

public final class QuestPetAct implements QuestAct {
    private final int tameness;
    private final boolean speed;

    public QuestPetAct(int tameness, boolean speed) {
        this.tameness = tameness;
        this.speed = speed;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        if (user.getPet(0) == null) {
            user.write(QuestPacket.failedUnknown()); // there is a clientside check, should not reach here
            return false;
        }
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        final Pet pet = user.getPet(0); // only applied to the lead pet
        if (pet == null) {
            return false;
        }
        // Resolve pet item
        final InventoryManager im = user.getInventoryManager();
        final Optional<Tuple<Integer, Item>> itemEntryResult = im.getItemBySn(InventoryType.CASH, pet.getItemSn());
        if (itemEntryResult.isEmpty()) {
            throw new IllegalStateException("Could not resolve pet item");
        }
        final int position = itemEntryResult.get().getLeft();
        final Item item = itemEntryResult.get().getRight();
        final PetData petData = item.getPetData();

        // Increase tameness (closeness)
        boolean levelUp = false;
        if (tameness > 0) {
            final int newTameness = Math.min(petData.getTameness() + tameness, GameConstants.PET_TAMENESS_MAX);
            petData.setTameness((short) newTameness);

            // Level up
            while (petData.getLevel() < GameConstants.PET_LEVEL_MAX &&
                    newTameness > GameConstants.getNextLevelPetCloseness(petData.getLevel())) {
                petData.setLevel((byte) (petData.getLevel() + 1));
                levelUp = true;
            }
        }

        // Set pet speed
        if (speed) {
            petData.setPetAttribute((short) (petData.getPetAttribute() | 1));
        }

        // Update pet item
        final Optional<InventoryOperation> updateResult = im.updateItem(position, item);
        if (updateResult.isEmpty()) {
            throw new IllegalStateException("Could not update pet item");
        }

        // Update client
        user.write(WvsContext.inventoryOperation(updateResult.get(), false));
        if (levelUp) {
            user.write(UserLocal.effect(Effect.petLevelUp(pet.getPetIndex())));
            user.getField().broadcastPacket(UserRemote.effect(user, Effect.petLevelUp(0)), user);
        }
        return true;
    }
}
