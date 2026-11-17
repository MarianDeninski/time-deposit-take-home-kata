-- Plan types use the exact lowercase strings TimeDepositCalculator compares against.
-- No plan earns interest for the first 30 days (days > 30 is required).

-- basic (1% p.a.): 15 days = no interest yet; 31 and 400 days = interest, no upper limit
INSERT INTO time_deposits (id, plan_type, days, balance) VALUES
    (1, 'basic', 15,  1000.00),
    (2, 'basic', 31,  1000.00),
    (3, 'basic', 400, 10000.00);

-- student (3% p.a.): 15 days = no interest yet; 31 and 365 days = interest;
-- 366 days = no interest after one year
INSERT INTO time_deposits (id, plan_type, days, balance) VALUES
    (4, 'student', 15,  1000.00),
    (5, 'student', 31,  1000.00),
    (6, 'student', 365, 10000.00),
    (7, 'student', 366, 10000.00);

-- premium (5% p.a.): 15, 31 and 45 days = no interest yet; 46 days = first day with interest
INSERT INTO time_deposits (id, plan_type, days, balance) VALUES
    (8,  'premium', 15, 1000.00),
    (9,  'premium', 31, 1000.00),
    (10, 'premium', 45, 10000.00),
    (11, 'premium', 46, 10000.00);

-- Withdrawals: deposits 1, 2, 4, 5, 7, 8, 9, 10 have none; 3 and 6 have one; 11 has several
INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES
    (1, 3,  100.00, '2026-01-15'),
    (2, 6,  250.00, '2026-03-10'),
    (3, 11, 500.00, '2026-02-01'),
    (4, 11, 200.00, '2026-05-20'),
    (5, 11, 50.00,  '2026-08-05');
