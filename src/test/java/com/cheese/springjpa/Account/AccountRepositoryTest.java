package com.cheese.springjpa.Account;

import static org.assertj.core.api.Java6Assertions.assertThat;

import com.cheese.springjpa.Account.dao.AccountRepository;
import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.Email;
import com.cheese.springjpa.Account.domain.QAccount;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;


@DataJpaTest
@RunWith(SpringRunner.class)
public class AccountRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;

  private final QAccount qAccount = QAccount.account;

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

  @Test
  public void predicate_test_001() {
    //given
    final Predicate predicate = qAccount.email.eq(Email.of("test001@test.com"));

    //when
    final boolean exists = accountRepository.exists(predicate);

    //then
    assertThat(exists).isTrue();
  }

  @Test
  public void predicate_test_002() {
    //given
    final Predicate predicate = qAccount.firstName.eq("test");

    //when
    final boolean exists = accountRepository.exists(predicate);

    //then
    assertThat(exists).isFalse();
  }

  @Test
  public void predicate_test_003() {
    //given
    final Predicate predicate = qAccount.email.value.like("test%");

    //when
    final long count = accountRepository.count(predicate);

    //then
    assertThat(count).isGreaterThan(1);
  }


}