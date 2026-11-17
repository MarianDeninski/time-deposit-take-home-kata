package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

/**
 * Monthly interest rule for one plan type. New plans or more complex rules (tiers, withdrawals,
 * promotions) are added as new implementations without changing TimeDepositCalculator.
 */
public interface InterestRule {

    String planType();

    // Unrounded monthly interest; the calculator rounds it to cents.
    double monthlyInterest(TimeDeposit timeDeposit);
}
