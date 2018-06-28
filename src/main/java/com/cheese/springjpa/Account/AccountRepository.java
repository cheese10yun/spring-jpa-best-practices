package com.cheese.springjpa.account;

import com.cheese.springjpa.account.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(Email email);
}
