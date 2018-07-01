package com.cheese.springjpa.account.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class EmailTest {

    @Test
    public void email() {

        final String id = "test";
        final String host = "@test.com";
        final Email email = Email.builder()
                .value(id + host)
                .build();

        assertThat(email.getHost(), is(host));
        assertThat(email.getId(), is(id));
    }
}