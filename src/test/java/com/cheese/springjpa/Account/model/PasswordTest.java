package com.cheese.springjpa.Account.model;

import com.cheese.springjpa.Account.exception.PasswordFailedExceededException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PasswordTest {


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
    public void resetFailedCount() {
        password.increaseFailCount();
        password.increaseFailCount();
        password.increaseFailCount();

        assertThat(password.getFailedCount(), is(3));

        password.resetFailedCount();
        assertThat(password.getFailedCount(), is(0));
    }

    @Test
    public void increaseFailCount() {
        password.increaseFailCount();
        password.increaseFailCount();
        password.increaseFailCount();
        assertThat(password.getFailedCount(), is(3));
    }

    @Test(expected = PasswordFailedExceededException.class)
    public void increaseFailCount_6회이상_PasswordFailedExceededException() {
        password.increaseFailCount();
        password.increaseFailCount();
        password.increaseFailCount();
        password.increaseFailCount();
        password.increaseFailCount();
        password.increaseFailCount();


    }
}