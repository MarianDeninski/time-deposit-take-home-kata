package org.ikigaidigital.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.out.InterestRunRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
class InterestRunPersistenceAdapter implements InterestRunRepository {

    private final InterestRunJpaRepository jpaRepository;

    // Joins the caller's transaction, so the period is only committed together with the balance updates.
    @Override
    @Transactional
    public boolean recordIfAbsent(YearMonth period, Instant executedAt) {
        return jpaRepository.insertIfAbsent(period.toString(), executedAt) == 1;
    }
}
