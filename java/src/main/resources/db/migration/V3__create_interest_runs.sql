-- Records each period (calendar month, YYYY-MM) in which interest was applied.
-- The UNIQUE constraint on period is what guarantees interest is applied at most once per month,
-- also under concurrent calls; the time_deposits and withdrawals schemas stay as specified.
CREATE TABLE interest_runs (
    id          BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    period      VARCHAR(7)  NOT NULL UNIQUE,
    executed_at TIMESTAMPTZ NOT NULL
);
