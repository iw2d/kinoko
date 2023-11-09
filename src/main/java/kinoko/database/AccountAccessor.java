package kinoko.database;

import kinoko.world.Account;

import java.util.Optional;

public interface AccountAccessor {
    Optional<Account> getAccountByUsername(String username);

    boolean checkPassword(Account account, String password, boolean secondary);

    boolean savePassword(Account account, String oldPassword, String newPassword, boolean secondary);

    boolean newAccount(String username, String password);

    boolean saveAccount(Account account);
}
