package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.application.port.out.WithdrawalRepository;
import org.ikigaidigital.domain.Withdrawal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
class WithdrawalPersistenceAdapter implements WithdrawalRepository {

    private final WithdrawalJpaRepository jpaRepository;
    private final WithdrawalMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<Withdrawal> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
