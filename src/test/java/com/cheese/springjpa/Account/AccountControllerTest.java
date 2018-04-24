package com.cheese.springjpa.Account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
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
                .andExpect(jsonPath("$.address1", is(dto.getAddress1())))
                .andExpect(jsonPath("$.address2", is(dto.getAddress2())))
                .andExpect(jsonPath("$.zip", is(dto.getZip())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())))
                .andExpect(jsonPath("$.fistName", is(dto.getFistName())))
                .andExpect(jsonPath("$.lastName", is(dto.getLastName())));
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
                .andExpect(jsonPath("$.address1", is(dto.getAddress1())))
                .andExpect(jsonPath("$.address2", is(dto.getAddress2())))
                .andExpect(jsonPath("$.zip", is(dto.getZip())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())))
                .andExpect(jsonPath("$.fistName", is(dto.getFistName())))
                .andExpect(jsonPath("$.lastName", is(dto.getLastName())));

    }

    private AccountDto.MyAccountReq buildMyAccountReq() {
        return AccountDto.MyAccountReq.builder()
                .address1("주소수정")
                .address2("주소수정2")
                .zip("061-233-444")
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