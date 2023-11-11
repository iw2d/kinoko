package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Inventory;

import java.util.HashMap;
import java.util.Map;

public final class AvatarLook implements Encodable {
    private final CharacterStat characterStat;
    private final Inventory equipped;

    public AvatarLook(CharacterStat characterStat, Inventory equipped) {
        this.characterStat = characterStat;
        this.equipped = equipped;
    }

    private Map<Integer, Integer> getHairEquip() {
        final Map<Integer, Integer> hairEquip = new HashMap<>();
        // Equips
        for (var entry : equipped.getItems().entrySet()) {
            final int bodyPart = entry.getKey();
            if (bodyPart > BodyPart.HAIR.getValue() && bodyPart < BodyPart.EQUIPPED_END.getValue()) {
                hairEquip.put(bodyPart, entry.getValue().getItemId());
            } else if (bodyPart >= BodyPart.CASH_BASE.getValue() && bodyPart < BodyPart.CASH_END.getValue()) {
                // Cash Equips (overwrite), sorted map gives us entries in ascending order
                hairEquip.put(bodyPart - BodyPart.CASH_BASE.getValue(), entry.getValue().getItemId());
            }
        }
        return hairEquip;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(characterStat.getGender()); // nGender
        outPacket.encodeByte(characterStat.getSkin()); // nSkin
        outPacket.encodeInt(characterStat.getFace()); // nFace

        // anHairEquip
        outPacket.encodeByte(0);
        outPacket.encodeInt(characterStat.getHair()); // nHair
        for (var entry : getHairEquip().entrySet()) {
            outPacket.encodeByte(entry.getKey());
            outPacket.encodeInt(entry.getValue());
        }
        outPacket.encodeByte(-1);

        // anUnseenEquip
        outPacket.encodeByte(-1);

        // nWeaponStickerID
        if (equipped.getItems().containsKey(BodyPart.CASH_WEAPON.getValue())) {
            outPacket.encodeInt(equipped.getItems().get(BodyPart.CASH_WEAPON.getValue()).getItemId());
        } else {
            outPacket.encodeInt(0);
        }

        // anPetID
        for (int i = 0; i < 3; i++) {
            outPacket.encodeInt(0);
        }
    }

    public static AvatarLook from(CharacterStat characterStat, Inventory equipped) {
        return new AvatarLook(characterStat, equipped);
    }
}
