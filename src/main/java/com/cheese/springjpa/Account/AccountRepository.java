package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustomRepository {
    Account findByEmail(Email email);

    boolean existsByEmail(Email email);

}
