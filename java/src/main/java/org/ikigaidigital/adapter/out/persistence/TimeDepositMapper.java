package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.TimeDeposit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * TimeDeposit stores the balance as Double and must not be changed (shared class used by
 * TimeDepositCalculator), while the database uses NUMERIC(19,2). The conversion happens only here:
 * BigDecimal.valueOf avoids binary floating-point artifacts, and scale 2 HALF_UP matches the column.
 */
@Component
class TimeDepositMapper {

    private static final int BALANCE_SCALE = 2;

    TimeDeposit toDomain(TimeDepositEntity entity) {
        return new TimeDeposit(entity.getId(), entity.getPlanType(), entity.getBalance().doubleValue(), entity.getDays());
    }

    BigDecimal toEntityBalance(Double balance) {
        return BigDecimal.valueOf(balance).setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
    }
}
