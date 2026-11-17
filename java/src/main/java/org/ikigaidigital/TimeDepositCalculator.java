package org.ikigaidigital;

import org.ikigaidigital.domain.interest.DefaultInterestRules;
import org.ikigaidigital.domain.interest.InterestRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeDepositCalculator {

    private static final int CENTS_SCALE = 2;
    private static final double NO_INTEREST = 0;

    private final Map<String, InterestRule> rulesByPlanType;

    public TimeDepositCalculator() {
        this(DefaultInterestRules.all());
    }

    public TimeDepositCalculator(List<InterestRule> interestRules) {
        this.rulesByPlanType = interestRules.stream()
                .collect(Collectors.toUnmodifiableMap(InterestRule::planType, Function.identity()));
    }

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit timeDeposit : xs) {
            double interest = monthlyInterest(timeDeposit);
            timeDeposit.setBalance(timeDeposit.getBalance() + roundToCents(interest));
        }
    }

    // Plan types without a rule earn no interest, as in the original implementation.
    private double monthlyInterest(TimeDeposit timeDeposit) {
        return Optional.ofNullable(rulesByPlanType.get(timeDeposit.getPlanType()))
                .map(rule -> rule.monthlyInterest(timeDeposit))
                .orElse(NO_INTEREST);
    }

    // new BigDecimal(double) instead of BigDecimal.valueOf on purpose: it keeps the original rounding exactly.
    private double roundToCents(double interest) {
        return new BigDecimal(interest).setScale(CENTS_SCALE, RoundingMode.HALF_UP).doubleValue();
    }
}
