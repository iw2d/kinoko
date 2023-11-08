package kinoko.database.cassandra.model;

public enum PetInfoModel {
    PET_NAME("pet_name"),
    LEVEL("level"),
    FULLNESS("fullness"),
    TAMENESS("tameness"),
    PET_SKILL("pet_skill"),
    PET_ATTRIBUTE("pet_attribute"),
    REMAIN_LIFE("remain_life");

    public static final String TYPE_NAME = "pet_info_type";
    private final String name;

    PetInfoModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
