package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.model.Address;
import com.cheese.springjpa.Account.model.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class AccountServiceJUnit5Test {


    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    @DisplayName("findById_존재하는경우_회원리턴")
    public void findBy_not_existed_test() {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountRepository.findById(anyLong())).willReturn(Optional.of(dto.toEntity()));

        //when
        final Account account = accountService.findById(anyLong());

        //then
        verify(accountRepository, atLeastOnce()).findById(anyLong());
        assertThatEqual(dto, account);
    }

    private void assertThatEqual(AccountDto.SignUpReq signUpReq, Account account) {
        assertThat(signUpReq.getAddress().getAddress1(), is(account.getAddress().getAddress1()));
        assertThat(signUpReq.getAddress().getAddress2(), is(account.getAddress().getAddress2()));
        assertThat(signUpReq.getAddress().getZip(), is(account.getAddress().getZip()));
        assertThat(signUpReq.getEmail(), is(account.getEmail()));
        assertThat(signUpReq.getFistName(), is(account.getFirstName()));
        assertThat(signUpReq.getLastName(), is(account.getLastName()));
    }

    private AccountDto.SignUpReq buildSignUpReq() {
        return AccountDto.SignUpReq.builder()
                .address(buildAddress("서울", "성동구", "052-2344"))
                .email(buildEmail("email"))
                .fistName("남윤")
                .lastName("김")
                .password("password111")
                .build();
    }

    private Email buildEmail(final String email) {
        return Email.builder().value(email).build();
    }

    private Address buildAddress(String address1, String address2, String zip) {
        return Address.builder()
                .address1(address1)
                .address2(address2)
                .zip(zip)
                .build();

    }


}