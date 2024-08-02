package kinoko.server.party;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class PartyStorage {
    private final ConcurrentHashMap<Integer, Party> partyMap = new ConcurrentHashMap<>();

    public void addParty(Party party) {
        partyMap.put(party.getPartyId(), party);
    }

    public boolean removeParty(Party party) {
        return partyMap.remove(party.getPartyId(), party);
    }

    public Optional<Party> getPartyById(int partyId) {
        return Optional.ofNullable(partyMap.get(partyId));
    }
}
