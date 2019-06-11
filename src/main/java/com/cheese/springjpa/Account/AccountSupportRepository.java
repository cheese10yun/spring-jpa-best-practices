package com.cheese.springjpa.Account;

import java.util.List;

public interface AccountSupportRepository {

    List<Account> findRecentlyRegistered(int limit);

}
