package org.ikigaidigital.application.port.in;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;

import java.util.List;

// Read model that combines a deposit with its withdrawals without changing the shared TimeDeposit class.
public record TimeDepositWithWithdrawals(TimeDeposit timeDeposit, List<Withdrawal> withdrawals) {

    public TimeDepositWithWithdrawals {
        withdrawals = List.copyOf(withdrawals);
    }
}
