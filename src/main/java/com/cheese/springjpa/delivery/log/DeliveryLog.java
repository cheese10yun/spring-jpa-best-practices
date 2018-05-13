package com.cheese.springjpa.delivery.log;

import com.cheese.springjpa.common.model.DateTime;
import com.cheese.springjpa.delivery.Delivery;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "order_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryLog {

    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = false)
    private DeliveryStatus status;

    @Embedded
    private DateTime dateTime;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Delivery delivery;

    @Builder
    public DeliveryLog(DeliveryStatus status, Delivery delivery) {
        this.status = status;
        this.delivery = delivery;
    }

}
