package org.ikigaidigital.adapter.in.web;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.in.web.model.TimeDepositDto;
import org.ikigaidigital.adapter.in.web.model.WithdrawalDto;
import org.ikigaidigital.application.port.in.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
class TimeDepositWebMapper {

    private static final int BALANCE_SCALE = 2;

    TimeDepositDto toDto(TimeDepositWithWithdrawals timeDepositWithWithdrawals) {
        TimeDeposit deposit = timeDepositWithWithdrawals.timeDeposit();
        List<WithdrawalDto> withdrawals = timeDepositWithWithdrawals.withdrawals().stream()
                .map(this::toDto)
                .toList();
        return new TimeDepositDto(deposit.getId(), deposit.getPlanType(), toMoney(deposit.getBalance()),
                deposit.getDays(), withdrawals);
    }

    private WithdrawalDto toDto(Withdrawal withdrawal) {
        return new WithdrawalDto(withdrawal.id(), withdrawal.amount(), withdrawal.date());
    }

    // TimeDeposit keeps the balance as Double; the API exposes it as a decimal with two places.
    private BigDecimal toMoney(Double balance) {
        return BigDecimal.valueOf(balance).setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
    }
}
