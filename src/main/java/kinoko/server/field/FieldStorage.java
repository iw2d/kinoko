package kinoko.server.field;

import kinoko.world.field.Field;

import java.util.Optional;

public interface FieldStorage {
    Optional<Field> getFieldById(int mapId);
}
