package kinoko.server.family;

import kinoko.server.header.CentralHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

/**
 * Utility class for {@link CentralHeader#PartyRequest}
 */
public final class FamilyRequest implements Encodable {
    private final FamilyRequestType requestType;
    private int partyId;
    private int characterId;
    private String characterName;
    private boolean isDisconnect;

    FamilyRequest(FamilyRequestType requestType) {
        this.requestType = requestType;
    }

    public FamilyRequestType getRequestType() {
        return requestType;
    }

    public int getPartyId() {
        return partyId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public boolean isDisconnect() {
        return isDisconnect;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case LoadParty -> {
                outPacket.encodeInt(partyId);
            }
            case CreateNewParty, WithdrawParty -> {
                // no encodes
            }
            case JoinParty, KickParty -> {
                outPacket.encodeInt(characterId);
            }
            case InviteParty -> {
                outPacket.encodeString(characterName);
            }
            case ChangePartyBoss -> {
                outPacket.encodeInt(characterId);
                outPacket.encodeByte(isDisconnect);
            }
        }
    }

    public static FamilyRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final FamilyRequest request = new FamilyRequest(FamilyRequestType.getByValue(type));
        switch (request.requestType) {
            case LoadParty -> {
                request.partyId = inPacket.decodeInt();
            }
            case CreateNewParty, WithdrawParty -> {
                // no decodes
            }
            case JoinParty, KickParty -> {
                request.characterId = inPacket.decodeInt();
            }
            case InviteParty -> {
                request.characterName = inPacket.decodeString();
            }
            case ChangePartyBoss -> {
                request.characterId = inPacket.decodeInt();
                request.isDisconnect = inPacket.decodeBoolean();
            }
            case null -> {
                throw new IllegalStateException(String.format("Unknown party request type %d", type));
            }
            default -> {
                throw new IllegalStateException(String.format("Unhandled party request type %d", type));
            }
        }
        return request;
    }

    public static FamilyRequest loadParty(int partyId) {
        final FamilyRequest request = new FamilyRequest(FamilyRequestType.LoadParty);
        request.partyId = partyId;
        return request;
    }

    public static FamilyRequest createNewParty() {
        return new FamilyRequest(FamilyRequestType.CreateNewParty);
    }

    public static FamilyRequest withdrawParty() {
        return new FamilyRequest(FamilyRequestType.WithdrawParty);
    }

    public static FamilyRequest joinParty(int inviterId) {
        final FamilyRequest request = new FamilyRequest(FamilyRequestType.JoinParty);
        request.characterId = inviterId;
        return request;
    }

    public static FamilyRequest invite(String characterName) {
        final FamilyRequest request = new FamilyRequest(FamilyRequestType.InviteParty);
        request.characterName = characterName;
        return request;
    }

    public static FamilyRequest kickParty(int targetId) {
        final FamilyRequest request = new FamilyRequest(FamilyRequestType.KickParty);
        request.characterId = targetId;
        return request;
    }

    public static FamilyRequest changePartyBoss(int targetId, boolean isDisconnect) {
        final FamilyRequest request = new FamilyRequest(FamilyRequestType.ChangePartyBoss);
        request.characterId = targetId;
        request.isDisconnect = isDisconnect;
        return request;
    }
}
