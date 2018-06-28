package com.cheese.springjpa.coupon;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon findById(long id) {
        return couponRepository.findOne(id);
    }
}
