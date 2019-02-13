package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.model.Email;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;


@DataJpaTest
@RunWith(SpringRunner.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void findByEmail_test() {
        final String email = "test001@test.com";
        final Account account = accountRepository.findByEmail(Email.of(email));
        assertThat(account.getEmail().getValue()).isEqualTo(email);
    }

    @Test
    public void findById_test() {
        final Optional<Account> optionalAccount = accountRepository.findById(1L);
        final Account account = optionalAccount.get();
        assertThat(account.getId()).isEqualTo(1L);
    }

    @Test
    public void isExistedEmail_test() {
        final String email = "test001@test.com";
        final boolean existsByEmail = accountRepository.existsByEmail(Email.of(email));
        assertThat(existsByEmail).isTrue();
    }

    @Test
    public void findRecentlyRegistered_test() {
        final List<Account> accounts = accountRepository.findRecentlyRegistered(10);
        assertThat(accounts.size()).isLessThan(11);
    }
}