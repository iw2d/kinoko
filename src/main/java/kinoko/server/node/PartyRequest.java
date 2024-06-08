package kinoko.server.node;

import kinoko.server.header.CentralHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.social.party.PartyRequestType;

/**
 * Utility class for {@link CentralHeader#PartyRequest}
 */
public final class PartyRequest implements Encodable {
    private final PartyRequestType requestType;
    private int characterId;
    private String characterName;

    public PartyRequest(PartyRequestType requestType) {
        this.requestType = requestType;
    }

    public PartyRequestType getRequestType() {
        return requestType;
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case LoadParty, CreateNewParty, WithdrawParty -> {
                // no encodes
            }
            case JoinParty, KickParty, ChangePartyBoss -> {
                outPacket.encodeInt(characterId);
            }
            case InviteParty -> {
                outPacket.encodeString(characterName);
            }
        }
    }

    public static PartyRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final PartyRequest partyRequest = new PartyRequest(PartyRequestType.getByValue(type));
        switch (partyRequest.getRequestType()) {
            case LoadParty, CreateNewParty, WithdrawParty -> {
                // no decodes
            }
            case JoinParty, KickParty, ChangePartyBoss -> {
                partyRequest.characterId = inPacket.decodeInt();
            }
            case InviteParty -> {
                partyRequest.characterName = inPacket.decodeString();
            }
            case null -> {
                throw new IllegalStateException(String.format("Unknown party request type %d", type));
            }
            default -> {
                throw new IllegalStateException(String.format("Unhandled party request type %d", type));
            }
        }
        return partyRequest;
    }

    public static PartyRequest of(PartyRequestType requestType) {
        return new PartyRequest(requestType);
    }

    public static PartyRequest of(PartyRequestType requestType, int characterId) {
        final PartyRequest request = new PartyRequest(requestType);
        request.characterId = characterId;
        return request;
    }

    public static PartyRequest invite(String characterName) {
        final PartyRequest request = new PartyRequest(PartyRequestType.InviteParty);
        request.characterName = characterName;
        return request;
    }
}
