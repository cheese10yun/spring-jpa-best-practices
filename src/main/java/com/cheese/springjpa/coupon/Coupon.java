package com.cheese.springjpa.coupon;


import com.cheese.springjpa.order.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "discount_amount")
    private double discountAmount;

    @Column(name = "use")
    private boolean use;

    @JsonIgnore
    @OneToOne(mappedBy = "coupon")
    private Order order;

    @Builder
    public Coupon(double discountAmount) {
        this.discountAmount = discountAmount;
        this.use = false;
    }

    public void use(final Order order) {
        this.order = order;
        this.use = true;
    }
}
