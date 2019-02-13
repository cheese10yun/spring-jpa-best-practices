package com.cheese.springjpa.Account;

import java.util.List;

public interface AccountCustomRepository {

    List<Account> findRecentlyRegistered(int limit);

}
