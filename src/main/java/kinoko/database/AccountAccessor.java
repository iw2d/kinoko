package kinoko.database;

import kinoko.world.user.Account;

import java.util.Optional;

public interface AccountAccessor {
    Optional<Account> getAccountById(int accountId);

    Optional<Account> getAccountByUsername(String username);

    boolean checkPassword(Account account, String password, boolean secondary);

    boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary);

    boolean newAccount(String username, String password);

    boolean saveAccount(Account account);

    Optional<String> getPinCode(int accountId);

    boolean savePinCode(int accountId, String pinCode);
}
