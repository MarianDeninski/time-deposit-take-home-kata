-- Naming: the requirements use camelCase (timeDeposits, planType, timeDepositId).
-- PostgreSQL folds unquoted identifiers to lowercase, so the database uses snake_case;
-- camelCase is kept in Java and in the API.
--
-- IDs are plain INTEGER without auto-generation because the system has no endpoint
-- that creates records; all rows are provided by migrations.

CREATE TABLE time_deposits (
    id        INTEGER       PRIMARY KEY,
    plan_type VARCHAR(50)   NOT NULL,
    days      INTEGER       NOT NULL,
    balance   NUMERIC(19,2) NOT NULL
);

CREATE TABLE withdrawals (
    id              INTEGER       PRIMARY KEY,
    time_deposit_id INTEGER       NOT NULL REFERENCES time_deposits (id),
    amount          NUMERIC(19,2) NOT NULL,
    date            DATE          NOT NULL
);

CREATE INDEX idx_withdrawals_time_deposit_id ON withdrawals (time_deposit_id);
