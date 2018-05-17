package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.Account.exception.EmailDuplicationException;
import com.cheese.springjpa.Account.model.Address;
import com.cheese.springjpa.Account.model.Email;
import com.cheese.springjpa.error.ErrorCode;
import com.cheese.springjpa.error.ErrorExceptionController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new ErrorExceptionController())
                .build();
    }

    @Test
    public void signUp() throws Exception {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountService.create(any())).willReturn(dto.toEntity());

        //when
        final ResultActions resultActions = requestSignUp(dto);

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address.address1", is(dto.getAddress().getAddress1())))
                .andExpect(jsonPath("$.address.address2", is(dto.getAddress().getAddress2())))
                .andExpect(jsonPath("$.address.zip", is(dto.getAddress().getZip())))
                .andExpect(jsonPath("$.email.address", is(dto.getEmail().getAddress())))
                .andExpect(jsonPath("$.fistName", is(dto.getFistName())))
                .andExpect(jsonPath("$.lastName", is(dto.getLastName())));
    }

    @Test
    public void signUp_이메일형식_유효하지않을경우_() throws Exception {
        //given
        final AccountDto.SignUpReq dto = AccountDto.SignUpReq.builder()
                .address(buildAddress("서울", "성동구", "052-2344"))
                .email(buildEmail("emailtest.com"))
                .fistName("남윤")
                .lastName("김")
                .password("password111")
                .build();
        given(accountService.create(any())).willReturn(dto.toEntity());

        //when
        final ResultActions resultActions = requestSignUp(dto);

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ErrorCode.INPUT_VALUE_INVALID.getMessage())))
                .andExpect(jsonPath("$.code", is(ErrorCode.INPUT_VALUE_INVALID.getCode())))
                .andExpect(jsonPath("$.status", is(ErrorCode.INPUT_VALUE_INVALID.getStatus())))
                .andExpect(jsonPath("$.errors[0].field", is("email.address")))
                .andExpect(jsonPath("$.errors[0].value", is(dto.getEmail().getAddress())));
    }


    @Test
    public void signUp_이메일형식_이미존재하는경우() throws Exception {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountService.create(any())).willThrow(EmailDuplicationException.class);

        //when
        final ResultActions resultActions = requestSignUp(dto);

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ErrorCode.EMAIL_DUPLICATION.getMessage())))
                .andExpect(jsonPath("$.code", is(ErrorCode.EMAIL_DUPLICATION.getCode())))
                .andExpect(jsonPath("$.status", is(ErrorCode.EMAIL_DUPLICATION.getStatus())));
    }


    @Test
    public void signUp_데이터_무결성_예외() throws Exception {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountService.create(any())).willThrow(DataIntegrityViolationException.class);

        //when
        final ResultActions resultActions = requestSignUp(dto);

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ErrorCode.INPUT_VALUE_INVALID.getMessage())))
                .andExpect(jsonPath("$.code", is(ErrorCode.INPUT_VALUE_INVALID.getCode())))
                .andExpect(jsonPath("$.status", is(ErrorCode.INPUT_VALUE_INVALID.getStatus())));
    }

    @Test
    @Ignore // 임시
    public void signUp_데이터_무결성_예외2() throws Exception {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountService.create(any())).willThrow(ConstraintViolationException.class);

        //when
        final ResultActions resultActions = requestSignUp(dto);

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ErrorCode.INPUT_VALUE_INVALID.getMessage())))
                .andExpect(jsonPath("$.code", is(ErrorCode.INPUT_VALUE_INVALID.getCode())))
                .andExpect(jsonPath("$.status", is(ErrorCode.INPUT_VALUE_INVALID.getStatus())));
    }


    @Test
    public void getUser() throws Exception {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountService.findById(anyLong())).willReturn(dto.toEntity());

        //when
        final ResultActions resultActions = requestGetUser();

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address.address1", is(dto.getAddress().getAddress1())))
                .andExpect(jsonPath("$.address.address2", is(dto.getAddress().getAddress2())))
                .andExpect(jsonPath("$.address.zip", is(dto.getAddress().getZip())))
                .andExpect(jsonPath("$.email.address", is(dto.getEmail().getAddress())))
                .andExpect(jsonPath("$.fistName", is(dto.getFistName())))
                .andExpect(jsonPath("$.lastName", is(dto.getLastName())));
    }

    @Test
    public void getUser_존재하지_않은_경우() throws Exception {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountService.findById(anyLong())).willThrow(AccountNotFoundException.class);

        //when
        final ResultActions resultActions = requestGetUser();

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ErrorCode.ACCOUNT_NOT_FOUND.getMessage())))
                .andExpect(jsonPath("$.code", is(ErrorCode.ACCOUNT_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.status", is(ErrorCode.ACCOUNT_NOT_FOUND.getStatus())))
                .andExpect(jsonPath("$.errors", is(empty())));
    }


    @Test
    public void updateMyAccount() throws Exception {
        //given
        final AccountDto.MyAccountReq dto = buildMyAccountReq();
        final Account account = Account.builder()
                .address(dto.getAddress())
                .build();

        given(accountService.updateMyAccount(anyLong(), any(AccountDto.MyAccountReq.class))).willReturn(account);

        //when
        final ResultActions resultActions = requestMyAccount(dto);

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address.address1", is(dto.getAddress().getAddress1())))
                .andExpect(jsonPath("$.address.address2", is(dto.getAddress().getAddress2())))
                .andExpect(jsonPath("$.address.zip", is(dto.getAddress().getZip())));

    }

    private ResultActions requestMyAccount(AccountDto.MyAccountReq dto) throws Exception {
        return mockMvc.perform(put("/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print());
    }

    private AccountDto.MyAccountReq buildMyAccountReq() {
        return AccountDto.MyAccountReq.builder()
                .address(Address.builder()
                        .address1("주소수정")
                        .address2("주소수정2")
                        .zip("061-233-444")
                        .build())
                .build();
    }

    private ResultActions requestGetUser() throws Exception {
        return mockMvc.perform(get("/accounts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private ResultActions requestSignUp(AccountDto.SignUpReq dto) throws Exception {
        return mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print());
    }

    private AccountDto.SignUpReq buildSignUpReq() {
        return AccountDto.SignUpReq.builder()
                .address(buildAddress("서울", "성동구", "052-2344"))
                .email(buildEmail("email@test.com"))
                .fistName("남윤")
                .lastName("김")
                .password("password111")
                .build();
    }

    private Email buildEmail(final String email) {
        return Email.builder().address(email).build();
    }

    private Address buildAddress(String address1, String address2, String zip) {

        return Address.builder()
                .address1(address1)
                .address2(address2)
                .zip(zip)
                .build();
    }


}