package kinoko.database.cassandra.model;

public enum EquipInfoModel {
    INC_STR("inc_str"),
    INC_DEX("inc_dex"),
    INC_INT("inc_int"),
    INC_LUK("inc_luk"),
    INC_MAX_HP("inc_max_hp"),
    INC_MAX_MP("inc_max_mp"),
    INC_PAD("inc_pad"),
    INC_MAD("inc_mad"),
    INC_PDD("inc_pdd"),
    INC_MDD("inc_mdd"),
    INC_ACC("inc_acc"),
    INC_EVA("inc_eva"),
    INC_CRAFT("inc_craft"),
    INC_SPEED("inc_speed"),
    INC_JUMP("inc_jump"),

    RUC("ruc"),
    CUC("cuc"),
    IUC("iuc"),
    CHUC("chuc"),

    GRADE("grade"),
    OPTION_1("option_1"),
    OPTION_2("option_2"),
    OPTION_3("option_3"),
    SOCKET_1("socket_1"),
    SOCKET_2("socket_2"),

    LEVEL_UP_TYPE("level_up_type"),
    LEVEL("level"),
    EXP("exp"),
    DURABILITY("durability");

    public static final String TYPE_NAME = "equip_info_type";
    private final String name;

    EquipInfoModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
