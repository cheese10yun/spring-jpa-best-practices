package com.cheese.springjpa.Account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        final AccountDto.SignUpReq signUpReq = buildSignUpReq();
        given(accountRepository.save(any())).willReturn(signUpReq.toEntity());

        //when
        final Account account = accountService.create(signUpReq);

        //then
        verify(accountRepository, atLeastOnce()).save(any());
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