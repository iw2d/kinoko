package kinoko.database.cassandra.model;

public enum AccountModel {
    USERNAME("username"),
    ACCOUNT_ID("account_id"),
    PASSWORD("password"),
    NX_CREDIT("nx_credit"),
    NX_PREPAID("nx_prepaid"),
    MAPLE_POINT("maple_point");

    private final String name;

    AccountModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
