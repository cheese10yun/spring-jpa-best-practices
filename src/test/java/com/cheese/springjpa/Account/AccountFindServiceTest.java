package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.Account.model.Email;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccountFindServiceTest {

    @InjectMocks
    private AccountFindService accountFindService;

    @Mock
    private AccountRepository accountRepository;
    private Account account;
    private Email email;

    @Before
    public void setUp() throws Exception {
        email = Email.of("yun@asd.com");
        account = Account.builder()
                .email(email)
                .build();
    }

    @Test
    public void findById_존재하는경우() {
        //given
        given(accountRepository.findById(any())).willReturn(Optional.of(account));

        //when
        final Account findAccount = accountFindService.findById(1L);

        //then
        assertThat(findAccount.getEmail().getValue()).isEqualTo(account.getEmail().getValue());
    }

    @Test(expected = AccountNotFoundException.class)
    public void findById_없는경우() {
        //given
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        //when
        accountFindService.findById(1L);

        //then
    }

    @Test
    public void findByEmail_존재하는경우() {
        //given
        given(accountRepository.findByEmail(any())).willReturn(account);

        //when
        final Account findAccount = accountFindService.findByEmail(email);

        //then
        assertThat(findAccount.getEmail().getValue()).isEqualTo(account.getEmail().getValue());
    }

    @Test(expected = AccountNotFoundException.class)
    public void findByEmail_없는경우() {
        //given

        given(accountRepository.findByEmail(any())).willReturn(null);

        //when
        accountFindService.findByEmail(email);

        //then
    }

    @Test
    public void isExistedEmail_이메일_있으면_true() {
        //given
        given(accountRepository.existsByEmail(email)).willReturn(true);

        //when
        final boolean existedEmail = accountFindService.isExistedEmail(email);

        //then
        assertThat(existedEmail).isTrue();
    }

    @Test
    public void isExistedEmail_이메일_없으면_false() {
        //given
        given(accountRepository.existsByEmail(email)).willReturn(false);

        //when
        final boolean existedEmail = accountFindService.isExistedEmail(email);

        //then
        assertThat(existedEmail).isFalse();
    }
}