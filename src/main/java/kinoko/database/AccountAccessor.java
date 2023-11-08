package kinoko.database;

import kinoko.world.Account;

import java.util.Optional;

public interface AccountAccessor {
    Optional<Account> getAccountByUsername(String username);

    Optional<Account> getAccountByPassword(String username, String password);

    boolean newAccount(String username, String password);

    boolean saveAccount(Account account);
}
