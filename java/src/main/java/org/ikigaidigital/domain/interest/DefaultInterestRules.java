package org.ikigaidigital.domain.interest;

import java.util.List;
import java.util.function.IntPredicate;

/** The interest rules of the current plans, as specified in the requirements. */
public final class DefaultInterestRules {

    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int STUDENT_MAX_DAYS = 365;
    private static final int PREMIUM_START_AFTER_DAYS = 45;

    // No interest is applied for the first 30 days for any existing plan.
    private static final IntPredicate PAST_GRACE_PERIOD = days -> days > GRACE_PERIOD_DAYS;

    private DefaultInterestRules() {
    }

    public static List<InterestRule> all() {
        return List.of(
                new FixedRateInterestRule("basic", 0.01, PAST_GRACE_PERIOD),
                new FixedRateInterestRule("student", 0.03, PAST_GRACE_PERIOD.and(days -> days <= STUDENT_MAX_DAYS)),
                new FixedRateInterestRule("premium", 0.05, PAST_GRACE_PERIOD.and(days -> days > PREMIUM_START_AFTER_DAYS)));
    }
}
