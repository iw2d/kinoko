package kinoko.world.user;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.BodyPart;
import kinoko.world.item.Inventory;
import kinoko.world.item.Item;
import kinoko.world.user.stat.CharacterStat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class AvatarLook implements Encodable {
    private final byte gender;
    private final byte skin;
    private final int hair;
    private final int face;
    private final Map<Integer, Integer> hairEquip;
    private final Map<Integer, Integer> unseenEquip;
    private final int weaponStickerId;
    private final int[] petIds;

    public AvatarLook(byte gender, byte skin, int hair, int face, Map<Integer, Integer> hairEquip, Map<Integer, Integer> unseenEquip, int weaponStickerId, int[] petIds) {
        this.gender = gender;
        this.skin = skin;
        this.hair = hair;
        this.face = face;
        this.hairEquip = hairEquip;
        this.unseenEquip = unseenEquip;
        this.weaponStickerId = weaponStickerId;
        this.petIds = petIds;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(gender); // nGender
        outPacket.encodeByte(skin); // nSkin
        outPacket.encodeInt(face); // nFace
        // anHairEquip
        outPacket.encodeByte(0);
        outPacket.encodeInt(hair); // nHair
        for (var entry : hairEquip.entrySet()) {
            outPacket.encodeByte(entry.getKey());
            outPacket.encodeInt(entry.getValue());
        }
        outPacket.encodeByte(-1);
        // anUnseenEquip
        for (var entry : unseenEquip.entrySet()) {
            outPacket.encodeByte(entry.getKey());
            outPacket.encodeInt(entry.getValue());
        }
        outPacket.encodeByte(-1);
        outPacket.encodeInt(weaponStickerId); // nWeaponStickerID
        for (int petId : petIds) {
            outPacket.encodeInt(petId); // anPetID
        }
    }

    public static AvatarLook decode(InPacket inPacket) {
        final byte gender = inPacket.decodeByte(); // nGender
        final byte skin = inPacket.decodeByte(); // nSkin
        final int face = inPacket.decodeInt(); // nFace
        // anHairEquip
        inPacket.decodeByte(); // 0
        final int hair = inPacket.decodeInt(); // nHair
        final Map<Integer, Integer> hairEquip = new HashMap<>();
        int bodyPart = inPacket.decodeByte();
        while (bodyPart != -1) {
            final int itemId = inPacket.decodeInt();
            hairEquip.put(bodyPart, itemId);
            bodyPart = inPacket.decodeByte();
        }
        // anUnseenEquip
        final Map<Integer, Integer> unseenEquip = new HashMap<>();
        int unseenPart = inPacket.decodeByte();
        while (unseenPart != -1) {
            final int itemId = inPacket.decodeInt();
            unseenEquip.put(unseenPart, itemId);
            unseenPart = inPacket.decodeByte();
        }
        final int weaponStickerId = inPacket.decodeInt(); // nWeaponStickerID
        final int[] petIds = {
                inPacket.decodeInt(),
                inPacket.decodeInt(),
                inPacket.decodeInt()
        }; // anPetID
        return new AvatarLook(gender, skin, hair, face, hairEquip, unseenEquip, weaponStickerId, petIds);
    }

    public static AvatarLook from(CharacterStat characterStat, Inventory equipped, Inventory cashInventory) {
        // Equips
        final Map<Integer, Integer> hairEquip = getHairEquip(equipped);
        final Map<Integer, Integer> unseenEquip = getUnseenEquip(equipped, hairEquip);
        // Cash weapon
        final Item cashWeapon = equipped.getItems().get(BodyPart.CASH_WEAPON.getValue());
        final int weaponStickerId = cashWeapon != null ? cashWeapon.getItemId() : 0;
        // Pet IDs
        final int[] petIds = new int[]{
                getPetId(cashInventory, characterStat.getPetSn1()),
                getPetId(cashInventory, characterStat.getPetSn2()),
                getPetId(cashInventory, characterStat.getPetSn3())
        };
        return new AvatarLook(
                characterStat.getGender(),
                characterStat.getSkin(),
                characterStat.getHair(),
                characterStat.getFace(),
                hairEquip,
                unseenEquip,
                weaponStickerId,
                petIds
        );
    }

    private static Map<Integer, Integer> getHairEquip(Inventory equipped) {
        final Map<Integer, Integer> hairEquip = new HashMap<>();
        for (var entry : equipped.getItems().entrySet()) {
            final int bodyPart = entry.getKey();
            final int itemId = entry.getValue().getItemId();
            if (bodyPart > BodyPart.HAIR.getValue() && bodyPart < BodyPart.EQUIPPED_END.getValue()) {
                hairEquip.put(bodyPart, itemId);
            } else if (bodyPart >= BodyPart.CASH_BASE.getValue() && bodyPart < BodyPart.CASH_END.getValue()) {
                // Cash Equips (overwrite), sorted map gives us entries in ascending order
                hairEquip.put(bodyPart - BodyPart.CASH_BASE.getValue(), itemId);
            }
        }
        return hairEquip;
    }

    private static Map<Integer, Integer> getUnseenEquip(Inventory equipped, Map<Integer, Integer> hairEquip) {
        final Map<Integer, Integer> unseenEquip = new HashMap<>();
        for (var entry : equipped.getItems().entrySet()) {
            final int bodyPart = entry.getKey();
            if (bodyPart > BodyPart.HAIR.getValue() && bodyPart < BodyPart.EQUIPPED_END.getValue()) {
                final int itemId = entry.getValue().getItemId();
                if (hairEquip.containsKey(bodyPart) && hairEquip.get(bodyPart) != itemId) {
                    unseenEquip.put(bodyPart, itemId);
                }
            }
        }
        return unseenEquip;
    }

    private static int getPetId(Inventory cashInventory, long petSn) {
        if (petSn == 0) {
            return 0;
        }
        final Optional<Map.Entry<Integer, Item>> itemEntryResult = cashInventory.getItems().entrySet().stream()
                .filter((entry) -> entry.getValue().getItemSn() == petSn)
                .findFirst();
        return itemEntryResult.map((entry) -> entry.getValue().getItemId()).orElse(0);
    }
}
