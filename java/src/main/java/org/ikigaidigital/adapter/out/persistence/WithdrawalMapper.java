package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Component;

@Component
class WithdrawalMapper {

    Withdrawal toDomain(WithdrawalEntity entity) {
        return new Withdrawal(entity.getId(), entity.getTimeDepositId(), entity.getAmount(), entity.getDate());
    }
}
