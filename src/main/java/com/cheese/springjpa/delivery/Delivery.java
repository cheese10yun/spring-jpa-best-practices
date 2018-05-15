package com.cheese.springjpa.delivery;

import com.cheese.springjpa.Account.model.Address;
import com.cheese.springjpa.common.model.DateTime;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Delivery {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeliveryLog> logs = new ArrayList<>();

    @Embedded
    private DateTime dateTime;


    @Builder
    public Delivery(Address address) {
        this.address = address;
    }


    public void addLog(DeliveryStatus status) {
        logs.add(DeliveryLog.builder()
                .status(status)
                .delivery(this)
                .build());
    }

    private void buildLog(DeliveryStatus status) {
        DeliveryLog.builder()
                .status(status)
                .delivery(this)
                .build();
    }
}
