package org.ikigaidigital.config;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.springframework.transaction.support.TransactionTemplate;

// Runs the use case in one transaction while keeping the application service free of Spring.
@RequiredArgsConstructor
class TransactionalUpdateBalancesUseCase implements UpdateBalancesUseCase {

    private final UpdateBalancesUseCase delegate;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void updateBalances() {
        transactionTemplate.executeWithoutResult(status -> delegate.updateBalances());
    }
}
