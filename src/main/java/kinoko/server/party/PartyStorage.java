package kinoko.server.party;

import kinoko.world.social.party.Party;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class PartyStorage {
    private static final AtomicInteger partyIdCounter = new AtomicInteger(1);
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

    public Optional<Party> getPartyByCharacterId(int characterId) {
        for (Party party : partyMap.values()) {
            if (party.hasMember(characterId)) {
                return Optional.of(party);
            }
        }
        return Optional.empty();
    }

    public int getNewPartyId() {
        return partyIdCounter.getAndIncrement();
    }
}
