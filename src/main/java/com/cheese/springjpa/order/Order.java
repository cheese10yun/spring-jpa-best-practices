package com.cheese.springjpa.order;

import com.cheese.springjpa.coupon.Coupon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "price")
    private double price;

    @OneToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "id", nullable = false)
    private Coupon coupon;

//    @OneToOne(mappedBy = "order")
//    private Coupon coupon;

    @Builder
    public Order(double price) {
        this.price = price;
    }

    public void applyCoupon(final Coupon coupon) {
        this.coupon = coupon;
        coupon.use(this);
        price -= coupon.getDiscountAmount();
    }

}
