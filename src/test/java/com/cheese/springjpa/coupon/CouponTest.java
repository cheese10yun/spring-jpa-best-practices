package com.cheese.springjpa.coupon;


import static org.assertj.core.api.Java6Assertions.assertThat;

import org.junit.Test;

public class CouponTest {

  @Test
  public void builder_test() {

    final Coupon coupon = Coupon.builder()
        .discountAmount(10)
        .build();

    assertThat(coupon.getDiscountAmount()).isEqualTo(10);
    assertThat(coupon.isUse()).isFalse();


  }
}