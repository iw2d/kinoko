package kinoko.world.social.party;

import kinoko.server.node.RemoteTownPortal;
import kinoko.server.node.RemoteUser;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;
import kinoko.world.field.TownPortal;

public final class PartyResult implements Encodable {
    private final PartyResultType resultType;

    private Party party;
    private RemoteUser member;
    private RemoteTownPortal townPortal;
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
            case InviteParty -> {
                outPacket.encodeInt(member.getCharacterId()); // dwInviterID
                outPacket.encodeString(member.getCharacterName()); // sInviter
                outPacket.encodeInt(member.getLevel()); // nLevel
                outPacket.encodeInt(member.getJob()); // nJobCode
                outPacket.encodeByte(0); // not sure, related to party search
            }
            case LoadParty_Done, UserMigration -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                party.encode(outPacket); // PARTYDATA::Decode
            }
            case CreateNewParty_Done -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                townPortal.encodeForPartyResult(outPacket);
            }
            case WithdrawParty_Done -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                outPacket.encodeInt(member.getCharacterId());
                outPacket.encodeByte(bool1); // if false, party disbanded
                if (bool1) {
                    outPacket.encodeByte(bool2); // bool2 ? expelled : left
                    outPacket.encodeString(member.getCharacterName());
                    party.encode(outPacket); // PARTYDATA::Decode
                }
            }
            case JoinParty_Done -> {
                outPacket.encodeInt(party.getPartyId()); // nPartyID
                outPacket.encodeString(member.getCharacterName());
                party.encode(outPacket); // PARTYDATA::Decode
            }
            case InviteParty_Sent -> {
                outPacket.encodeString(string1);
            }
            case ChangePartyBoss_Done -> {
                outPacket.encodeInt(int1); // new boss id
                outPacket.encodeByte(bool1); // from disconnecting
            }
            case ChangeLevelOrJob -> {
                outPacket.encodeInt(member.getCharacterId());
                outPacket.encodeInt(member.getLevel());
                outPacket.encodeInt(member.getJob());
            }
            case SuccessToSelectPQReward -> {
                outPacket.encodeInt(member.getCharacterId());
                outPacket.encodeString(member.getCharacterName());
                outPacket.encodeByte(int1); // nSelectedIdx
            }
            case FailToSelectPQReward -> {
                outPacket.encodeByte(int1); // nRetCode
            }
            case ReceivePQReward -> {
                // CUIPQReward::OnReceiveReward
                throw new IllegalStateException("Tried to encode unsupported party result type");
            }
            case ServerMsg -> {
                outPacket.encodeByte(string1 != null); // Your request for a party didn't work due to an unexpected error.
                if (string1 != null) {
                    outPacket.encodeString(string1);
                }
            }
            case TownPortalChanged -> {
                outPacket.encodeInt(int1); // member index
                townPortal.encodeForPartyResult(outPacket); // PARTYDATA::TOWNPORTAL
            }
//            case ADVER_APPLY -> {
//                outPacket.encodeInt(member.getCharacterId()); // dwApplierId
//                outPacket.encodeString(member.getCharacterName()); // sApplierName
//                outPacket.encodeInt(member.getLevel()); // nLevel
//                outPacket.encodeInt(member.getJob()); // nJobCode
//            }
            case CreateNewParty_AlreadyJoined, CreateNewParty_Beginner, WithdrawParty_NotJoined,
                    JoinParty_Done2, JoinParty_AlreadyJoined, JoinParty_AlreadyFull, JoinParty_OverDesiredSize,
                    KickParty_FieldLimit, ChangePartyBoss_NotSameField, ChangePartyBoss_NoMemberInSameField,
                    ChangePartyBoss_NotSameChannel, AdminCannotCreate, FailToRequestPQReward,
                    CanNotInThisField -> {
                // no encodes
            }
            default -> {
                // Your request for a party didn't work due to an unexpected error.
            }
        }
    }

    public static void encodeTownPortal(OutPacket outPacket, TownPortal townPortal) {
        outPacket.encodeInt(GameConstants.UNDEFINED_FIELD_ID);
        outPacket.encodeInt(GameConstants.UNDEFINED_FIELD_ID);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
    }

    public static PartyResult of(PartyResultType resultType) {
        return new PartyResult(resultType);
    }

    public static PartyResult invite(RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.InviteParty);
        result.member = member;
        return result;
    }

    public static PartyResult load(Party party) {
        final PartyResult result = new PartyResult(PartyResultType.LoadParty_Done);
        result.party = party;
        return result;
    }

    public static PartyResult update(RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.ChangeLevelOrJob);
        result.member = member;
        return result;
    }

    public static PartyResult create(Party party, RemoteTownPortal townPortal) {
        final PartyResult result = new PartyResult(PartyResultType.CreateNewParty_Done);
        result.party = party;
        result.townPortal = townPortal;
        return result;
    }

    public static PartyResult disband(Party party, RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.WithdrawParty_Done);
        result.party = party;
        result.member = member;
        result.bool1 = false; // disband
        return result;
    }

    public static PartyResult leave(Party party, RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.WithdrawParty_Done);
        result.party = party;
        result.member = member;
        result.bool1 = true; // not disband
        result.bool2 = false; // not expelled
        return result;
    }

    public static PartyResult kick(Party party, RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.WithdrawParty_Done);
        result.party = party;
        result.member = member;
        result.bool1 = true; // not disband
        result.bool2 = true; // not expelled
        return result;
    }

    public static PartyResult join(Party party, RemoteUser member) {
        final PartyResult result = new PartyResult(PartyResultType.JoinParty_Done);
        result.party = party;
        result.member = member;
        return result;
    }

    public static PartyResult changeBoss(int newBossId, boolean isFromDisconnect) {
        final PartyResult result = new PartyResult(PartyResultType.ChangePartyBoss_Done);
        result.int1 = newBossId;
        result.bool1 = isFromDisconnect;
        return result;
    }

    public static PartyResult message(String message) {
        final PartyResult result = new PartyResult(PartyResultType.ServerMsg);
        result.string1 = message;
        return result;
    }
}
