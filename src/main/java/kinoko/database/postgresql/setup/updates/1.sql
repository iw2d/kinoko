-- 1.sql
-- Migration: Create the account.bans table to store temporary and permanent bans
-- Each row represents a banned account. No row exists if the account is not banned.


BEGIN;

CREATE TABLE account.bans (
    account_id      INT NOT NULL
                    REFERENCES account.accounts(id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,
    reason          TEXT NOT NULL,
    temp_ban_until  TIMESTAMP WITH TIME ZONE,  -- UTC timestamp, null if permanent ban
    PRIMARY KEY (account_id)
);

COMMIT;
