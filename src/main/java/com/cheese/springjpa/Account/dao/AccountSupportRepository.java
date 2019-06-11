package com.cheese.springjpa.Account.dao;

import com.cheese.springjpa.Account.domain.Account;
import java.util.List;

public interface AccountSupportRepository {

    List<Account> findRecentlyRegistered(int limit);

}
