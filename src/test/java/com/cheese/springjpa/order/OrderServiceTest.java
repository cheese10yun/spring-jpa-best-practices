package com.cheese.springjpa.order;

import com.cheese.springjpa.coupon.Coupon;
import com.cheese.springjpa.coupon.CouponService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @Test
    public void order_쿠폰할인적용() {
        final Order order = orderService.order();

        assertThat(order.getPrice(), is(9_000D)); // 1,000 할인 적용 확인
        assertThat(order.getId(), is(notNullValue())); // 1,000 할인 적용 확인
        assertThat(order.getCoupon(), is(notNullValue())); // 1,000 할인 적용 확인

        final Order findOrder = orderService.findById(order.getId());
        System.out.println("couponId : " + findOrder.getCoupon().getId()); // couponId : 1 (coupon_id 외래 키를 저장 완료)

        final Coupon coupon = couponService.findById(1);
        assertThat(coupon.isUse(), is(true));
        assertThat(coupon.getId(), is(notNullValue()));
        assertThat(coupon.getDiscountAmount(), is(notNullValue()));
    }

    @Test
    public void use_메서드에_order_set_필요이유() {
        final Order order = orderService.order();
        assertThat(order.getPrice(), is(9_000D)); // 1,000 할인 적용 확인
        final Coupon coupon = order.getCoupon();
        assertThat(coupon.getOrder(), is(notNullValue()));
    }
}