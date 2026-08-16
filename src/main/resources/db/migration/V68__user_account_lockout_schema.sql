-- V68: User Account Lockout & Brute-force Protection Schema

ALTER TABLE user_accounts
ADD COLUMN IF NOT EXISTS failed_login_attempts INT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP WITH TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_user_accounts_locked_until ON user_accounts(locked_until);
