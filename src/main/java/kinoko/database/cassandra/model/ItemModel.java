package kinoko.database.cassandra.model;

public enum ItemModel {
    ITEM_SN("item_sn"),
    ITEM_ID("item_id"),
    ITEM_TYPE("item_type"),
    CASH("cash"),
    QUANTITY("quantity"),
    ATTRIBUTE("attribute"),
    TITLE("title"),
    EQUIP_INFO("equip_info"),
    PET_INFO("pet_info");

    public static final String TYPE_NAME = "item_type";
    private final String name;

    ItemModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
