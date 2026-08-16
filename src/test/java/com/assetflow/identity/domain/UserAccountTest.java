package com.assetflow.identity.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserAccountTest {

    @Test
    void shouldInitializeEmployeeWithZeroFailedAttemptsAndNotLocked() {
        UserAccount user = UserAccount.createEmployee("test@example.com", "hash", UUID.randomUUID());

        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.getLockedUntil()).isNull();
        assertThat(user.isLocked(Instant.now())).isFalse();
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void shouldIncrementFailedAttemptsAndLockAccountWhenThresholdReached() {
        UserAccount user = UserAccount.createEmployee("test@example.com", "hash", UUID.randomUUID());
        Instant now = Instant.now();

        // 4 failed attempts should not lock
        for (int i = 1; i <= 4; i++) {
            user.recordFailedLogin(now, 5, 900);
            assertThat(user.getFailedLoginAttempts()).isEqualTo(i);
            assertThat(user.isLocked(now)).isFalse();
        }

        // 5th failed attempt should lock for 900 seconds
        user.recordFailedLogin(now, 5, 900);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(user.getLockedUntil()).isNotNull();
        assertThat(user.isLocked(now)).isTrue();
        assertThat(user.isLocked(now.plusSeconds(901))).isFalse();
    }

    @Test
    void shouldResetFailedAttemptsAndLockoutOnSuccessfulLogin() {
        UserAccount user = UserAccount.createEmployee("test@example.com", "hash", UUID.randomUUID());
        Instant now = Instant.now();

        user.recordFailedLogin(now, 5, 900);
        user.recordFailedLogin(now, 5, 900);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(2);

        user.recordSuccessfulLogin(now);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.getLockedUntil()).isNull();
    }

    @Test
    void shouldUnlockAccountManually() {
        UserAccount user = UserAccount.createEmployee("test@example.com", "hash", UUID.randomUUID());
        Instant now = Instant.now();

        for (int i = 1; i <= 5; i++) {
            user.recordFailedLogin(now, 5, 900);
        }
        assertThat(user.isLocked(now)).isTrue();

        user.unlock(now);
        assertThat(user.isLocked(now)).isFalse();
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.getLockedUntil()).isNull();
    }
}
