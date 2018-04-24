package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;


    @Test
    public void create_회원가입_성공() {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountRepository.save(any(Account.class))).willReturn(dto.toEntity());

        //when
        final Account account = accountService.create(dto);

        //then
        verify(accountRepository, atLeastOnce()).save(any(Account.class));
        assertThatEqual(dto, account);
    }

    @Test
    public void findById_존재하는경우_회원리턴() {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountRepository.findOne(anyLong())).willReturn(dto.toEntity());

        //when
        final Account account = accountService.findById(anyLong());

        //then
        verify(accountRepository, atLeastOnce()).findOne(anyLong());
        assertThatEqual(dto, account);
    }

    @Test(expected = AccountNotFoundException.class)
    public void findById_존재하지않은경우_AccountNotFoundException() {
        //given
        given(accountRepository.findOne(anyLong())).willReturn(null);

        //when
        accountService.findById(anyLong());
    }

    @Test
    public void updateMyAccount() {
        //given
        final AccountDto.SignUpReq signUpReq = buildSignUpReq();
        final AccountDto.MyAccountReq dto = buildMyAccountReq();
        given(accountRepository.findOne(anyLong())).willReturn(signUpReq.toEntity());

        //when
        final Account account = accountService.updateMyAccount(anyLong(), dto);

        //then
        assertThat(dto.getAddress1(), is(account.getAddress1()));
        assertThat(dto.getAddress2(), is(account.getAddress2()));
        assertThat(dto.getZip(), is(account.getZip()));
    }



    private AccountDto.MyAccountReq buildMyAccountReq() {
        return AccountDto.MyAccountReq.builder()
                .address1("주소수정")
                .address2("주소수정2")
                .zip("061-233-444")
                .build();
    }

    private void assertThatEqual(AccountDto.SignUpReq signUpReq, Account account) {
        assertThat(signUpReq.getAddress1(), is(account.getAddress1()));
        assertThat(signUpReq.getAddress2(), is(account.getAddress2()));
        assertThat(signUpReq.getZip(), is(account.getZip()));
        assertThat(signUpReq.getEmail(), is(account.getEmail()));
        assertThat(signUpReq.getFistName(), is(account.getFistName()));
        assertThat(signUpReq.getLastName(), is(account.getLastName()));
        assertThat(signUpReq.getPassword(), is(account.getPassword()));
    }

    private AccountDto.SignUpReq buildSignUpReq() {
        return AccountDto.SignUpReq.builder()
                .address1("서울")
                .address2("성동구")
                .zip("052-2344")
                .email("email")
                .fistName("남윤")
                .lastName("김")
                .password("password111")
                .build();
    }
}