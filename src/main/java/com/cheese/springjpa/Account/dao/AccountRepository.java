package com.cheese.springjpa.Account.dao;

import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountSupportRepository {
    Account findByEmail(Email email);

    boolean existsByEmail(Email email);
}
