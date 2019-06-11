package com.cheese.springjpa.delivery;

import com.cheese.springjpa.Account.domain.Address;
import com.cheese.springjpa.common.model.DateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        this.logs.add(buildLog(status));
    }

    private DeliveryLog buildLog(DeliveryStatus status) {
        return DeliveryLog.builder()
                .status(status)
                .delivery(this)
                .build();
    }
}
