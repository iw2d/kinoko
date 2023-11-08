package kinoko.database.cassandra.model;

public enum CharacterDataModel {
    CHARACTER_ID("character_id"),
    ACCOUNT_ID("account_id"),
    CHARACTER_NAME("character_name"),
    CHARACTER_STAT("character_stat"),
    CHARACTER_EQUIPPED("character_equipped"),
    EQUIP_INVENTORY("equip_inventory"),
    CONSUME_INVENTORY("consume_inventory"),
    INSTALL_INVENTORY("install_inventory"),
    ETC_INVENTORY("etc_inventory"),
    CASH_INVENTORY("cash_inventory"),
    MONEY("money"),
    SKILL_RECORDS("skill_records"),
    SKILL_COOLTIMES("skill_cooltimes"),
    QUEST_RECORDS("quest_records"),
    FRIEND_MAX("friend_max");

    private final String name;

    CharacterDataModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
