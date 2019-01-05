package com.cheese.springjpa.common.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeTest {

    @Test
    public void DateTime() {

        // 커버리지 위한 코드
        final DateTime dateTime = new DateTime();
        assertThat(dateTime.getCreatedAt(), is(nullValue()));
        assertThat(dateTime.getUpdatedAt(), is(nullValue()));

    }
}