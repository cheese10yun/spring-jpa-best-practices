package com.cheese.springjpa.common.model;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PageRequestTest {

    @Test
    public void setSizeTest() {

        final PageRequest page = new PageRequest();

        page.setSize(10);
        assertThat(page.getSize(), is(10));

        // 50이 넘어가면 기본 사이즈 10
        page.setSize(51);
        assertThat(page.getSize(), is(10));
    }

    @Test
    public void setPageTest() {
        final PageRequest page = new PageRequest();

        page.setPage(10);
        assertThat(page.getPage(), is(10));


        // 페이지 설정이 0이면 기본 페이지 1로 설정
        page.setPage(0);
        assertThat(page.getPage(), is(1));
    }

    @Test
    public void setDirectionTest() {
        final PageRequest page = new PageRequest();
        page.setDirection(Sort.Direction.ASC);
        assertThat(page.getDirection(), is(Sort.Direction.ASC));
    }

    @Test
    public void ofTest() {
        final PageRequest page = new PageRequest();
        page.setPage(1);
        page.setSize(10);
        page.setDirection(Sort.Direction.ASC);

        final org.springframework.data.domain.PageRequest pageRequest = page.of();

        assertThat(pageRequest.getPageSize(), is(10));
        assertThat(pageRequest.getOffset(), is(0L));

    }
}