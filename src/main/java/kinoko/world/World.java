package kinoko.world;

import lombok.Data;

import java.util.List;

@Data
public final class World {
    private final int id;
    private final String name;
    private final List<Channel> channels;
}
