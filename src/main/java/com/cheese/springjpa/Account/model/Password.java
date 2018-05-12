package com.cheese.springjpa.Account.model;

import com.cheese.springjpa.Account.exception.PasswordFailedExceededException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;


@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    @Column(name = "password_expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "password_failed_count", nullable = false)
    private int failedCount;

    @Column(name = "password_ttl")
    private long ttl;

    @Builder
    public Password(final String value) {
        this.ttl = 1209_604; // 1209_604 is 14 days
        this.value = encodePassword(value);
        this.expirationDate = extendExpirationDate();
    }

    public boolean isMatched(final String rawPassword) {
        if (failedCount >= 5)
            throw new PasswordFailedExceededException();

        final boolean matches = isMatches(rawPassword);
        updateFailedCount(matches);
        return matches;
    }

    public void changePassword(final String newPassword, final String oldPassword) {
        if (isMatched(oldPassword)) {
            value = encodePassword(newPassword);
            extendExpirationDate();
        }
    }


    public boolean isExpiration() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

    private LocalDateTime extendExpirationDate() {
        return LocalDateTime.now().plusSeconds(ttl);
    }

    private String encodePassword(final String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    private void updateFailedCount(boolean matches) {
        if (matches)
            resetFailedCount();
        else
            increaseFailCount();
    }

    private void resetFailedCount() {
        this.failedCount = 0;
    }

    private void increaseFailCount() {
        this.failedCount++;
    }

    private boolean isMatches(String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, this.value);
    }

}
