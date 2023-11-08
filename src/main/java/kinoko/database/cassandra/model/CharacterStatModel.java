package kinoko.database.cassandra.model;

public enum CharacterStatModel {
    GENDER("gender"),
    SKIN("skin"),
    FACE("face"),
    HAIR("hair"),
    LEVEL("level"),
    JOB("job"),
    SUB_JOB("sub_job"),
    BASE_STR("base_str"),
    BASE_DEX("base_dex"),
    BASE_INT("base_int"),
    BASE_LUK("base_luk"),
    HP("hp"),
    MAX_HP("max_hp"),
    MP("mp"),
    MAX_MP("max_mp"),
    AP("ap"),
    SP("sp"),
    EXP("exp"),
    POP("pop"),
    POS_MAP("pos_map"),
    PORTAL("portal");

    public static final String TYPE_NAME = "character_stat_type";
    private final String name;

    CharacterStatModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
