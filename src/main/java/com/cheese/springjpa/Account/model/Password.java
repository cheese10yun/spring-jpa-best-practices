package com.cheese.springjpa.Account.model;

import com.cheese.springjpa.Account.exception.PasswordFailedExceededException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    @Column(name = "password_expiration_date")
    private Timestamp expirationDate;

    @Column(name = "password_failed_count", nullable = false)
    private int failedCount;

    @Builder
    public Password(final String value) {
        this.value = encodePassword(value);
        this.expirationDate = calculateExpirationDate(3);
    }

    public boolean isMatched(final String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, this.value);
    }

    public boolean isExpiration() {
        return System.currentTimeMillis() > expirationDate.getTime();
    }

    public void resetFailedCount() {
        this.failedCount = 0;
    }

    public void increaseFailCount() {
        this.failedCount++;

        if (failedCount > 5)
            throw new PasswordFailedExceededException();
    }

    private Timestamp calculateExpirationDate(int plusMonth) {
        return Timestamp.valueOf(LocalDateTime.now().plusMonths(plusMonth));
    }


    private String encodePassword(final String password) {
        return new BCryptPasswordEncoder().encode(password);
    }


}
