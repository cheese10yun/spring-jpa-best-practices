package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.model.Address;
import com.cheese.springjpa.Account.model.Email;
import com.cheese.springjpa.Account.model.Password;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private com.cheese.springjpa.Account.model.Email email;

    @Column(name = "first_name", nullable = false)
    private String fistName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Embedded
    private Password password;

    @Embedded
    private Address address;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "update_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Email email, String fistName, String lastName, Password password, Address address) {
        this.email = email;
        this.fistName = fistName;
        this.lastName = lastName;
        this.password = password;
        this.address = address;
    }


    public void updateMyAccount(AccountDto.MyAccountReq dto) {
        this.address = dto.getAddress();
    }
}
