package kinoko.world.social.party;

import kinoko.server.node.RemoteUser;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class PartyResult implements Encodable {
    private final PartyResultType resultType;

    private Party party;
    private RemoteUser member;
    private TownPortal townPortal;
    private int int1;
    private boolean bool1;
    private boolean bool2;
    private String string1;

    public PartyResult(PartyResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case INVITE_PARTY -> {
                outPacket.encodeInt(member.getCharacterId()); // dwInviterID
                outPacket.encodeString(member.getCharacterName()); // sInviter
                outPacket.encodeInt(member.getLevel()); // nLevel
                outPacket.encodeInt(member.getJob()); // nJobCode
                outPacket.encodeByte(0); // not sure, related to party search
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
                outPacket.encodeInt(member.getCharacterId());
                outPacket.encodeByte(bool1); // if false, party disbanded
                if (bool1) {
                    outPacket.encodeByte(bool2); // bool2 ? expelled : left
                    outPacket.encodeString(member.getCharacterName());
                    party.encode(outPacket); // PARTYDATA::Decode
                }
            }
            case JOIN_PARTY_DONE -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                outPacket.encodeString(string1);
                party.encode(outPacket); // PARTYDATA::Decode
            }
            case INVITE_PARTY_SENT -> {
                outPacket.encodeString(string1);
            }
            case CHANGE_PARTY_BOSS_DONE -> {
                outPacket.encodeInt(member.getCharacterId());
                outPacket.encodeByte(bool1); // from disconnecting
            }
            case CHANGE_LEVEL_OR_JOB -> {
                outPacket.encodeInt(member.getCharacterId());
                outPacket.encodeInt(member.getLevel());
                outPacket.encodeInt(member.getJob());
            }
            case SUCCESS_TO_SELECT_PQ_REWARD -> {
                outPacket.encodeInt(member.getCharacterId());
                outPacket.encodeString(member.getCharacterName());
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
                outPacket.encodeByte(string1 != null); // Your request for a party didn't work due to an unexpected error.
                if (string1 != null) {
                    outPacket.encodeString(string1);
                }
            }
            case TOWN_PORTAL_CHANGED -> {
                outPacket.encodeInt(int1); // member index
                townPortal.encode(outPacket); // PARTYDATA::TOWNPORTAL
            }
            case ADVER_APPLY -> {
                outPacket.encodeInt(member.getCharacterId()); // dwApplierId
                outPacket.encodeString(member.getCharacterName()); // sApplierName
                outPacket.encodeInt(member.getLevel()); // nLevel
                outPacket.encodeInt(member.getJob()); // nJobCode
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

    public static PartyResult of(PartyResultType resultType) {
        return new PartyResult(resultType);
    }

    public static PartyResult load(Party party) {
        final PartyResult result = new PartyResult(PartyResultType.LOAD_PARTY_DONE);
        result.party = party;
        return result;
    }

    public static PartyResult update(RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.CHANGE_LEVEL_OR_JOB);
        result.member = member;
        return result;
    }

    public static PartyResult create(Party party) {
        final PartyResult result = new PartyResult(PartyResultType.CREATE_NEW_PARTY_DONE);
        result.party = party;
        result.townPortal = TownPortal.EMPTY_PORTAL;
        return result;
    }

    public static PartyResult disband(Party party, RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.WITHDRAW_PARTY_DONE);
        result.party = party;
        result.member = member;
        result.bool1 = false; // disband
        return result;
    }


    public static PartyResult leave(Party party, RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.WITHDRAW_PARTY_DONE);
        result.party = party;
        result.member = member;
        result.bool1 = true; // not disband
        result.bool2 = false; // not expelled
        return result;
    }

    public static PartyResult message(String message) {
        final PartyResult result = new PartyResult(PartyResultType.SERVER_MSG);
        result.string1 = message;
        return result;
    }
}
