package kinoko.world.social.party;

import kinoko.server.node.RemoteUser;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class PartyResult implements Encodable {
    private final PartyResultType resultType;

    private Party party;
    private RemoteUser user;
    private TownPortal townPortal;
    private int int1;
    private boolean bool1;
    private boolean bool2;

    public PartyResult(PartyResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case INVITE_PARTY -> {
                outPacket.encodeInt(party.getPartyId()); // partyId
                outPacket.encodeString(user.getCharacterName()); // sInviter
                outPacket.encodeInt(user.getLevel()); // nLevel
                outPacket.encodeInt(user.getJob()); // nJobCode
                outPacket.encodeByte(0); // not sure
            }
            case LOAD_PARTY_DONE, USER_MIGRATION -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                party.encode(outPacket); // PARTYDATA::Decode
            }
            case CREATE_NEW_PARTY_DONE -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                townPortal.encode(outPacket); // PARTYDATA::TOWNPORTAL
            }
            case WITHDRAW_PARTY_DONE -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                outPacket.encodeInt(user.getCharacterId());
                outPacket.encodeByte(bool1); // if false, party disbanded
                if (bool1) {
                    outPacket.encodeByte(bool2); // bool2 ? expelled : left
                    outPacket.encodeString(user.getCharacterName());
                    party.encode(outPacket); // PARTYDATA::Decode
                }
            }
            case JOIN_PARTY_DONE -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                outPacket.encodeString(user.getCharacterName());
                party.encode(outPacket); // PARTYDATA::Decode
            }
            case INVITE_PARTY_SENT -> {
                outPacket.encodeString(user.getCharacterName());
            }
            case CHANGE_PARTY_BOSS_DONE -> {
                outPacket.encodeInt(user.getCharacterId());
                outPacket.encodeByte(bool1); // from disconnecting
            }
            case CHANGE_LEVEL_OR_JOB -> {
                outPacket.encodeInt(user.getCharacterId());
                outPacket.encodeInt(user.getLevel());
                outPacket.encodeInt(user.getJob());
            }
            case SUCCESS_TO_SELECT_PQ_REWARD -> {
                outPacket.encodeInt(user.getCharacterId());
                outPacket.encodeString(user.getCharacterName());
                outPacket.encodeByte(int1); // nSelectedIdx
            }
            case FAIL_TO_SELECT_PQ_REWARD -> {
                outPacket.encodeByte(int1); // nRetCode
            }
            case RECEIVE_PQ_REWARD -> {
                // CUIPQReward::OnReceiveReward
                throw new IllegalStateException("Tried to encode unsupported party result type");
            }
            case SERVER_MSG -> {
                outPacket.encodeByte(false); // Your request for a party didn't work due to an unexpected error.
            }
            case TOWN_PORTAL_CHANGED -> {
                outPacket.encodeInt(int1); // member index
                townPortal.encode(outPacket); // PARTYDATA::TOWNPORTAL
            }
            case ADVER_APPLY -> {
                outPacket.encodeInt(user.getCharacterId()); // dwApplierId
                outPacket.encodeString(user.getCharacterName()); // sApplierName
                outPacket.encodeInt(user.getLevel()); // nLevel
                outPacket.encodeInt(user.getJob()); // nJobCode
            }
            case CREATE_NEW_PARTY_ALREADY_JOINED, CREATE_NEW_PARTY_BEGINNER, WITHDRAW_PARTY_NOT_JOINED,
                    JOIN_PARTY_DONE_2, JOIN_PARTY_ALREADY_JOINED, JOIN_PARTY_ALREADY_FULL, JOIN_PARTY_OVER_DESIRED_SIZE,
                    KICK_PARTY_FIELD_LIMIT, CHANGE_PARTY_BOSS_NOT_SAME_FIELD, CHANGE_PARTY_BOSS_NO_MEMBER_IN_SAME_FIELD,
                    CHANGE_PARTY_BOSS_NOT_SAME_CHANNEL, ADMIN_CANNOT_CREATE, FAIL_TO_REQUEST_PQ_REWARD,
                    CAN_NOT_IN_THIS_FIELD -> {
                // no encodes
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported party result type");
            }
        }
    }
}
