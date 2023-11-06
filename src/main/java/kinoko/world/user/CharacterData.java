package kinoko.world.user;

import kinoko.server.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.Encodable;
import lombok.Data;

@Data
public final class CharacterData implements Encodable {
    private final int id;
    private String name;
    private CharacterStat characterStat;
    private CharacterInventory characterInventory;
    private byte friendMax;

    @Override
    public void encode(OutPacket outPacket) {
        encodeCharacterData(DBChar.ALL, outPacket);
    }

    public void encodeAvatarData(OutPacket outPacket) {
        final CharacterStat cs = getCharacterStat();
        cs.encode(outPacket);

        // AvatarLook
        outPacket.encodeByte(cs.getGender()); // nGender
        outPacket.encodeByte(cs.getSkin()); // nSkin
        outPacket.encodeInt(cs.getFace()); // nFace

        // anHairEquip
        outPacket.encodeByte(0);
        outPacket.encodeInt(cs.getHair()); // nHair
        outPacket.encodeByte(-1);
        // anUnseenEquip
        outPacket.encodeByte(-1);

        outPacket.encodeInt(0); // nWeaponStickerID
        // anPetID
        for (int i = 0; i < 3; i++) {
            outPacket.encodeInt(0);
        }
        // ~AvatarLook
    }

    public void encodeCharacterData(DBChar flag, OutPacket outPacket) {
        outPacket.encodeLong(flag.getValue());
        outPacket.encodeByte(0); // nCombatOrders
        outPacket.encodeByte(false); // bool -> byte, int * FT, int * FT

        if (flag.hasFlag(DBChar.CHARACTER)) {
            getCharacterStat().encode(outPacket);
            outPacket.encodeByte(getFriendMax()); // nFriendMax
            outPacket.encodeByte(false); // sLinkedCharacter: bool -> str
        }
        if (flag.hasFlag(DBChar.MONEY)) {
            outPacket.encodeInt(getCharacterInventory().getMoney());
        }
        if (flag.hasFlag(DBChar.INVENTORY_SIZE)) {
            getCharacterInventory().encodeSize(outPacket);
        }
        if (flag.hasFlag(DBChar.EQUIP_EXT_EXPIRE)) {
            outPacket.encodeFT(FileTime.MAX_TIME); // aEquipExtExpire
        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_EQUIP)) {

        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_CONSUME)) {

        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_INSTALL)) {

        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_ETC)) {

        }
        if (flag.hasFlag(DBChar.ITEM_SLOT_CASH)) {

        }
        if (flag.hasFlag(DBChar.SKILL_RECORD)) {

        }
    }
}
