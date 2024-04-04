package kinoko.world.social.party;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

/**
 * Utility class for {@link kinoko.server.netty.CentralPacketHeader#PARTY_REQUEST}
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
            case LOAD_PARTY, CREATE_NEW_PARTY, WITHDRAW_PARTY -> {
                // no encodes
            }
            case JOIN_PARTY, KICK_PARTY, CHANGE_PARTY_BOSS -> {
                outPacket.encodeInt(characterId);
            }
            case INVITE_PARTY -> {
                outPacket.encodeString(characterName);
            }
        }
    }

    public static PartyRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final PartyRequest partyRequest = new PartyRequest(PartyRequestType.getByValue(type));
        switch (partyRequest.getRequestType()) {
            case LOAD_PARTY, CREATE_NEW_PARTY, WITHDRAW_PARTY -> {
                // no decodes
            }
            case JOIN_PARTY, KICK_PARTY, CHANGE_PARTY_BOSS -> {
                partyRequest.characterId = inPacket.decodeInt();
            }
            case INVITE_PARTY -> {
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
        final PartyRequest request = new PartyRequest(PartyRequestType.INVITE_PARTY);
        request.characterName = characterName;
        return request;
    }
}
