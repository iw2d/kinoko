package kinoko.server.alliance;

import kinoko.database.DatabaseManager;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class AllianceStorage {
    private final ConcurrentHashMap<Integer, Alliance> allianceMap = new ConcurrentHashMap<>();

    public boolean addAlliance(Alliance alliance) {
        if (allianceMap.containsKey(alliance.getAllianceId())) {
            return false;
        }
        if (!DatabaseManager.allianceAccessor().newAlliance(alliance)) {
            return false;
        }
        allianceMap.put(alliance.getAllianceId(), alliance);
        return true;
    }

    public boolean removeAlliance(Alliance alliance) {
        if (!DatabaseManager.allianceAccessor().deleteAlliance(alliance.getAllianceId())) {
            return false;
        }
        allianceMap.remove(alliance.getAllianceId());
        return true;
    }

    public Optional<Alliance> getAllianceById(int allianceId) {
        if (allianceId == 0) {
            return Optional.empty();
        }
        if (allianceMap.containsKey(allianceId)) {
            return Optional.of(allianceMap.get(allianceId));
        }
        final Optional<Alliance> guildResult = DatabaseManager.allianceAccessor().getAllianceById(allianceId);
        guildResult.ifPresent(guild -> allianceMap.put(allianceId, guild));
        return guildResult;
    }
}
