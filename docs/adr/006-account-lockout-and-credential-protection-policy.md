# ADR 006: Account Lockout and Brute-Force Credential Protection Policy

## Status
Accepted

## Context
In a multi-tenant enterprise system, authentication endpoints are primary targets for automated credential stuffing and brute-force attacks. To mitigate unauthorized access while ensuring legitimate users can recover after a lockout period, the identity layer requires an explicit Account Lockout Policy.

## Decision
1. **Consecutive Failed Attempt Tracking**:
   - `user_accounts` table stores `failed_login_attempts INT NOT NULL DEFAULT 0` and `locked_until TIMESTAMP WITH TIME ZONE`.
   - Domain aggregate `UserAccount` encapsulates `recordFailedLogin(now, maxAttempts, lockoutDurationSeconds)`, `recordSuccessfulLogin(now)`, `isLocked(now)`, and `unlock(now)`.
2. **Thresholds and Durations**:
   - `DEFAULT_MAX_FAILED_ATTEMPTS`: 5 consecutive failures.
   - `DEFAULT_LOCKOUT_DURATION_SECONDS`: 900 seconds (15 minutes).
3. **Behavior**:
   - When `failed_login_attempts >= 5`, `locked_until` is set to `now + 15 minutes`.
   - Any login attempt while `now < locked_until` fails immediately with `400 Bad Request` (`Account Locked`).
   - Successful authentication resets `failed_login_attempts = 0` and clears `locked_until`.
4. **Flyway Migration**:
   - Implemented in `V68__user_account_lockout_schema.sql` with an index on `locked_until`.

## Consequences
- Protects user credentials from sustained automated brute-force attacks.
- Provides immediate security feedback without leaking detailed internal password hash states.
- Allows automatic recovery after the lockout window expires without requiring administrator intervention.
