package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.model.Email;
import com.cheese.springjpa.Account.model.Password;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "address1", nullable = false)
    private String address1;

    @Column(name = "address2", nullable = false)
    private String address2;

    @Column(name = "zip", nullable = false)
    private String zip;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Builder
    public Account(Email email, String fistName, String lastName, Password password, String address1, String address2, String zip) {
        this.email = email;
        this.fistName = fistName;
        this.lastName = lastName;
        this.password = password;
        this.address1 = address1;
        this.address2 = address2;
        this.zip = zip;
    }


    public void updateMyAccount(AccountDto.MyAccountReq dto) {
        this.address1 = dto.getAddress1();
        this.address2 = dto.getAddress2();
        this.zip = dto.getZip();
    }
}
