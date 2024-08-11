package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.server.party.Party;
import kinoko.server.party.PartyResultType;
import kinoko.server.user.RemoteTownPortal;
import kinoko.server.user.RemoteUser;

public final class PartyPacket {
    // CWvsContext::OnPartyResult --------------------------------------------------------------------------------------

    public static OutPacket inviteParty(RemoteUser remoteUser) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.InviteParty);
        outPacket.encodeInt(remoteUser.getCharacterId()); // dwInviterID
        outPacket.encodeString(remoteUser.getCharacterName()); // sInviter
        outPacket.encodeInt(remoteUser.getLevel()); // nLevel
        outPacket.encodeInt(remoteUser.getJob()); // nJobCode
        outPacket.encodeByte(0); // not sure, related to party search
        return outPacket;
    }

    public static OutPacket loadPartyDone(Party party) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.LoadParty_Done);
        outPacket.encodeInt(party.getPartyId()); // nPartyID
        party.encode(outPacket); // PARTYDATA::Decode
        return outPacket;
    }

    public static OutPacket createNewPartyDone(Party party, RemoteTownPortal remoteTownPortal) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.CreateNewParty_Done);
        outPacket.encodeInt(party.getPartyId()); // nPartyID
        remoteTownPortal.encodeForPartyResult(outPacket);
        return outPacket;
    }

    public static OutPacket withdrawPartyDone(Party party, RemoteUser member, boolean disband, boolean kick) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.WithdrawParty_Done);
        outPacket.encodeInt(party.getPartyId()); // nPartyID
        outPacket.encodeInt(member.getCharacterId());
        outPacket.encodeByte(!disband); // if false, party disbanded
        if (!disband) {
            outPacket.encodeByte(kick); // bool2 ? expelled : left
            outPacket.encodeString(member.getCharacterName());
            party.encode(outPacket); // PARTYDATA::Decode
        }
        return outPacket;
    }

    public static OutPacket joinPartyDone(Party party, RemoteUser member) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.JoinParty_Done);
        outPacket.encodeInt(party.getPartyId()); // nPartyID
        outPacket.encodeString(member.getCharacterName());
        party.encode(outPacket); // PARTYDATA::Decode
        return outPacket;
    }

    public static OutPacket changePartyBossDone(int newBossId, boolean isDisconnect) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.ChangePartyBoss_Done);
        outPacket.encodeInt(newBossId);
        outPacket.encodeByte(isDisconnect);
        return outPacket;
    }

    public static OutPacket changeLevelOrJob(RemoteUser member) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.ChangeLevelOrJob);
        outPacket.encodeInt(member.getCharacterId());
        outPacket.encodeInt(member.getLevel());
        outPacket.encodeInt(member.getJob());
        return outPacket;
    }

    public static OutPacket serverMsg(String message) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.ServerMsg);
        outPacket.encodeByte(message != null); // Your request for a party didn't work due to an unexpected error.
        if (message != null) {
            outPacket.encodeString(message);
        }
        return outPacket;
    }

    public static OutPacket townPortalChanged(int memberIndex, RemoteTownPortal remoteTownPortal) {
        final OutPacket outPacket = PartyPacket.of(PartyResultType.TownPortalChanged);
        outPacket.encodeInt(memberIndex); // member index
        remoteTownPortal.encodeForPartyResult(outPacket); // PARTYDATA::TOWNPORTAL
        return outPacket;
    }

    public static OutPacket of(PartyResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PartyResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }
}
