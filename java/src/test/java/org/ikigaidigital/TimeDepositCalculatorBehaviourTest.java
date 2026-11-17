package org.ikigaidigital;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins the behaviour of TimeDepositCalculator.updateBalance so it can be refactored safely:
 * the README states the existing method works correctly and its behaviour must not change.
 */
class TimeDepositCalculatorBehaviourTest {

    private static final List<String> PLAN_TYPES = List.of("basic", "student", "premium", "unknown");
    private static final List<Integer> DAYS = List.of(0, 1, 15, 30, 31, 45, 46, 100, 365, 366, 400, 1000);
    private static final List<Double> BALANCES = List.of(0.0, 0.01, 1.0, 999.99, 1000.0, 1234567.0, 12345.67, 10000.0);

    private final TimeDepositCalculator calculator = new TimeDepositCalculator();

    @ParameterizedTest(name = "{0}, {1} days, {2} -> {3}")
    @CsvSource({
            "basic,   30,  10000.00, 10000.00",
            "basic,   31,  10000.00, 10008.33",
            "basic,   400, 10000.00, 10008.33",
            "student, 30,  10000.00, 10000.00",
            "student, 31,  10000.00, 10025.00",
            "student, 365, 10000.00, 10025.00",
            "student, 366, 10000.00, 10000.00",
            "premium, 31,  10000.00, 10000.00",
            "premium, 45,  10000.00, 10000.00",
            "premium, 46,  10000.00, 10041.67",
            "unknown, 400, 10000.00, 10000.00"
    })
    void updateBalance_appliesMonthlyInterest_atPlanBoundaries(String planType, int days, double balance, double expected) {
        TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0}, {1} days, {2}")
    @MethodSource("allCombinations")
    void updateBalance_matchesOriginalImplementation_forAllCombinations(String planType, int days, double balance) {
        TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(originalUpdatedBalance(planType, days, balance));
    }

    static Stream<Arguments> allCombinations() {
        return PLAN_TYPES.stream().flatMap(planType -> DAYS.stream().flatMap(days -> BALANCES.stream()
                .map(balance -> Arguments.of(planType, days, balance))));
    }

    // Frozen copy of the original updateBalance arithmetic, used as the reference result.
    private static double originalUpdatedBalance(String planType, int days, double balance) {
        double interest = 0;
        if (days > 30) {
            if (planType.equals("student")) {
                if (days < 366) {
                    interest += balance * 0.03 / 12;
                }
            } else if (planType.equals("premium")) {
                if (days > 45) {
                    interest += balance * 0.05 / 12;
                }
            } else if (planType.equals("basic")) {
                interest += balance * 0.01 / 12;
            }
        }
        return balance + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
    }
}
