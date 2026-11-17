package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterestRuleExtensionTest {

    @Test
    void updateBalance_appliesNewPlanRule_whenRuleIsAddedWithoutChangingCalculator() {
        List<InterestRule> rules = new ArrayList<>(DefaultInterestRules.all());
        rules.add(new FixedRateInterestRule("gold", 0.12, days -> days > 0));
        TimeDepositCalculator calculator = new TimeDepositCalculator(rules);
        TimeDeposit gold = new TimeDeposit(1, "gold", 10000.00, 10);
        TimeDeposit basic = new TimeDeposit(2, "basic", 10000.00, 31);

        calculator.updateBalance(List.of(gold, basic));

        assertThat(gold.getBalance()).isEqualTo(10100.00);
        assertThat(basic.getBalance()).isEqualTo(10008.33);
    }

    @Test
    void updateBalance_usesCustomRuleLogic_whenRuleIsNotFixedRate() {
        InterestRule flatBonus = new InterestRule() {
            @Override
            public String planType() {
                return "bonus";
            }

            @Override
            public double monthlyInterest(TimeDeposit timeDeposit) {
                return 5.00;
            }
        };
        TimeDeposit bonus = new TimeDeposit(1, "bonus", 1000.00, 1);

        new TimeDepositCalculator(List.of(flatBonus)).updateBalance(List.of(bonus));

        assertThat(bonus.getBalance()).isEqualTo(1005.00);
    }
}
