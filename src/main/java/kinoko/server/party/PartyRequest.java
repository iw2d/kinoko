package kinoko.server.party;

import kinoko.server.header.CentralHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

/**
 * Utility class for {@link CentralHeader#PartyRequest}
 */
public final class PartyRequest implements Encodable {
    private final PartyRequestType requestType;
    private int partyId;
    private int characterId;
    private String characterName;
    private boolean isDisconnect;

    PartyRequest(PartyRequestType requestType) {
        this.requestType = requestType;
    }

    public PartyRequestType getRequestType() {
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

    public static PartyRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final PartyRequest request = new PartyRequest(PartyRequestType.getByValue(type));
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

    public static PartyRequest loadParty(int partyId) {
        final PartyRequest request = new PartyRequest(PartyRequestType.LoadParty);
        request.partyId = partyId;
        return request;
    }

    public static PartyRequest createNewParty() {
        return new PartyRequest(PartyRequestType.CreateNewParty);
    }

    public static PartyRequest withdrawParty() {
        return new PartyRequest(PartyRequestType.WithdrawParty);
    }

    public static PartyRequest joinParty(int inviterId) {
        final PartyRequest request = new PartyRequest(PartyRequestType.JoinParty);
        request.characterId = inviterId;
        return request;
    }

    public static PartyRequest invite(String characterName) {
        final PartyRequest request = new PartyRequest(PartyRequestType.InviteParty);
        request.characterName = characterName;
        return request;
    }

    public static PartyRequest kickParty(int targetId) {
        final PartyRequest request = new PartyRequest(PartyRequestType.KickParty);
        request.characterId = targetId;
        return request;
    }

    public static PartyRequest changePartyBoss(int targetId, boolean isDisconnect) {
        final PartyRequest request = new PartyRequest(PartyRequestType.ChangePartyBoss);
        request.characterId = targetId;
        request.isDisconnect = isDisconnect;
        return request;
    }
}
