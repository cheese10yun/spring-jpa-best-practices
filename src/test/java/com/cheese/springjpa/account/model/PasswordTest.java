package com.cheese.springjpa.account.model;

import com.cheese.springjpa.account.exception.PasswordFailedExceededException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class PasswordTest {


    private final long TTL = 1209_604L;
    private String passwordValue;
    private Password password;

    @Before
    public void setUp() {
        passwordValue = "password001";
        password = Password.builder().value(passwordValue).build();
    }

    @Test
    public void testPassword() {
        assertThat(password.isMatched(passwordValue), is(true));
        assertThat(password.isExpiration(), is(false));
        assertThat(password.getFailedCount(), is(0));
        assertThat(password.getValue(), is(notNullValue()));
        assertThat(password.getExpirationDate(), is(notNullValue()));
    }

    @Test
    public void resetFailedCount_비밀번호가일치하는경우_실패카운트가초기화된다() {

        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");

        assertThat(password.getFailedCount(), is(3));

        password.isMatched(passwordValue);
        assertThat(password.getFailedCount(), is(0));
    }

    @Test
    public void increaseFailCount_비밀번호가일치하지않는경우_실패카운트가증가한다() {
        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");
        assertThat(password.getFailedCount(), is(3));
    }

    @Test(expected = PasswordFailedExceededException.class)
    public void increaseFailCount_6회이상_PasswordFailedExceededException() {
        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");
        password.isMatched("notMatchedPassword");

        password.isMatched("notMatchedPassword");
    }

    @Test
    public void changePassword_비밀번호_일치하는경우_비밀번호가변경된다() {

        final String newPassword = "newPassword";
        password.changePassword(newPassword, passwordValue);


        assertThat(password.isMatched(newPassword), is((true)));
        assertThat(password.getFailedCount(), is(0));
        assertThat(password.getTtl(), is(TTL));
        assertThat(password.getExpirationDate().isAfter(LocalDateTime.now().plusDays(14)), is(true));

    }

    @Test
    public void changePassword_실패카운트가4일경우_일치하는경우_비밀번호가변경되며_실패카운트가초기회된다() {
        password.isMatched("netMatchedPassword");
        password.isMatched("netMatchedPassword");

        final String newPassword = "newPassword";
        password.changePassword(newPassword, passwordValue);
        assertThat(password.getFailedCount(), is(0));
    }

    @Test(expected = PasswordFailedExceededException.class)
    public void changePassword_비밀번호변경이_5회이상일치하지않으면_PasswordFailedExceededException() {

        final String newPassword = "newPassword";
        final String oldPassword = "oldPassword";

        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, oldPassword);

    }

    @Test
    public void changePassword_비밀번호변경이_4회일치하지않더라도_5회에서일치하면_실패카운트초기화() {

        final String newPassword = "newPassword";
        final String oldPassword = "oldPassword";

        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, oldPassword);
        password.changePassword(newPassword, passwordValue);


        assertThat(password.isMatched(newPassword), is(true));
        assertThat(password.getFailedCount(), is(0));
        assertThat(password.getTtl(), is(TTL));

    }
}