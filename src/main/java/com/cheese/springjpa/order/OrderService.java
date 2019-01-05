package com.cheese.springjpa.order;

import com.cheese.springjpa.coupon.Coupon;
import com.cheese.springjpa.coupon.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CouponService couponService;


    public Order order() {
        final Order order = Order.builder().price(1_0000).build(); // 10,000 상품주문
        Coupon coupon = couponService.findById(1); // 1,000 할인 쿠폰
        order.applyCoupon(coupon);
        return orderRepository.save(order);
    }

    public Order findById(long id) {
        return orderRepository.findById(id).get();
    }
}
