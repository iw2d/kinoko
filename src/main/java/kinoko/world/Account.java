package kinoko.world;

import lombok.Data;

@Data
public final class Account {
    private final int id;
    private final String username;
    private int nxCredit;
    private int nxPrepaid;
    private int maplePoint;
}
