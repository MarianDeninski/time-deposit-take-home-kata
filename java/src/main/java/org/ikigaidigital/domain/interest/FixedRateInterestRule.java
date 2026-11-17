package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

import java.util.function.IntPredicate;

/**
 * Applies a fixed annual rate, paid monthly, when the deposit's age in days is eligible.
 * The arithmetic (balance * annualRate / 12 in double) matches the original calculator exactly.
 */
public record FixedRateInterestRule(String planType, double annualRate, IntPredicate eligibleDays)
        implements InterestRule {

    private static final int MONTHS_PER_YEAR = 12;

    @Override
    public double monthlyInterest(TimeDeposit timeDeposit) {
        if (!eligibleDays.test(timeDeposit.getDays())) {
            return 0;
        }
        return timeDeposit.getBalance() * annualRate / MONTHS_PER_YEAR;
    }
}
