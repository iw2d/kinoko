package kinoko.database.cassandra.model;

public enum InventoryModel {
    ITEMS("items"),
    SIZE("size");


    public static final String TYPE_NAME = "inventory_type";
    private final String name;

    InventoryModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
