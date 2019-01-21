package com.cheese.springjpa.Account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class AccountIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void account_name_조회() throws Exception {
        final AccountSearchType type = AccountSearchType.NAME;
        final String value = "yun";

        final ResultActions resultActions = requestSearchPaging(type, value);

        resultActions
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void account_email_조회() throws Exception {
        final AccountSearchType type = AccountSearchType.EMAIL;
        final String value = "test";

        final ResultActions resultActions = requestSearchPaging(type, value);

        resultActions
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void account_ALL_조회() throws Exception {
        final AccountSearchType type = AccountSearchType.ALL;
        final String value = "";

        final ResultActions resultActions = requestSearchPaging(type, value);

        resultActions
                .andExpect(status().isOk())
        ;
    }

    private ResultActions requestSearchPaging(AccountSearchType type, String value) throws Exception {
        return mvc.perform(get("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "1")
                .param("size", "20")
                .param("direction", "ASC")
                .param("type", type.name())
                .param("value", value))
                .andDo(print());
    }
}
