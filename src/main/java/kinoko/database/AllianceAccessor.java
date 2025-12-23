package kinoko.database;

import kinoko.server.alliance.Alliance;

import java.util.Optional;

public interface AllianceAccessor {
    Optional<Alliance> getAllianceById(int allianceId);

    boolean checkAllianceNameAvailable(String name);

    boolean newAlliance(Alliance alliance);

    boolean saveAlliance(Alliance alliance);

    boolean deleteAlliance(int allianceId);

}