package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.user.Pet;
import kinoko.world.user.User;

import java.util.List;

public final class PetPacket {
    // CUser::OnPetPacket ----------------------------------------------------------------------------------------------

    public static OutPacket petActivated(User user, Pet pet) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_ACTIVATED);
        outPacket.encodeInt(user.getCharacterId());
        // this->OnPetActivated
        outPacket.encodeByte(pet.getPetIndex()); // nIdx
        outPacket.encodeByte(true); // activate
        outPacket.encodeByte(true); // replace?
        pet.encode(outPacket);
        return outPacket;
    }

    public static OutPacket petDeactivated(User user, int petIndex, int reason) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_ACTIVATED);
        outPacket.encodeInt(user.getCharacterId());
        // this->OnPetActivated
        outPacket.encodeByte(petIndex);
        outPacket.encodeByte(false); // activate
        outPacket.encodeByte(reason);
        // 1 : The pet went back home because it's hungry.
        // 2 : The pet's magical time has run out%2C and so it has turned back into a doll.
        // 3 : You cannot use a pet in this location.
        // 4 : Cannot summon pet while Following.
        return outPacket;
    }

    public static OutPacket move(User user, int petIndex, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_MOVE);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(petIndex);
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket action(User user, int petIndex, int type, int action, String chat) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_ACTION);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(petIndex);
        outPacket.encodeByte(type);
        outPacket.encodeByte(action);
        outPacket.encodeString(chat);
        return outPacket;
    }

    public static OutPacket nameChanged(User user, int petIndex, String name) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_NAME_CHANGED);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(petIndex);
        outPacket.encodeString(name); // sName
        outPacket.encodeByte(false); // nNameTag
        return outPacket;
    }

    public static OutPacket loadExceptionList(User user, int petIndex, long petSn, List<Integer> exceptionList) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_LOAD_EXCEPTION_LIST);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(petIndex);
        outPacket.encodeLong(petSn); // liPetSN
        outPacket.encodeByte(exceptionList.size());
        exceptionList.forEach(outPacket::encodeInt);
        return outPacket;
    }

    public static OutPacket actionInteract(User user, int petIndex, int action, boolean success, boolean chatBalloon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_ACTION_COMMAND);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(petIndex);
        outPacket.encodeByte(PetActionType.INTERACT.getValue());
        outPacket.encodeByte(action); // this->pTemplate->m_aInteractiona[byte]
        outPacket.encodeByte(success);
        outPacket.encodeByte(chatBalloon); // bChatBalloon
        return outPacket;
    }

    public static OutPacket actionFeed(User user, int petIndex, boolean success, boolean chatBalloon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_ACTION_COMMAND);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(petIndex);
        outPacket.encodeByte(PetActionType.FEED.getValue());
        outPacket.encodeByte(success);
        outPacket.encodeByte(chatBalloon);
        return outPacket;
    }
}
