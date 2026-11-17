package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.TimeDepositNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class TimeDepositPersistenceAdapter implements TimeDepositRepository {

    private final TimeDepositJpaRepository jpaRepository;
    private final TimeDepositMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<TimeDeposit> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    // Updates only the balance of existing rows; dirty checking writes the changes on commit.
    // An unknown id fails instead of inserting, so saveAll can never create new deposits.
    @Override
    @Transactional
    public void saveAll(List<TimeDeposit> timeDeposits) {
        Map<Integer, TimeDepositEntity> entitiesById = loadEntitiesById(timeDeposits);
        for (TimeDeposit timeDeposit : timeDeposits) {
            TimeDepositEntity entity = entitiesById.get(timeDeposit.getId());
            if (entity == null) {
                throw new TimeDepositNotFoundException(timeDeposit.getId());
            }
            entity.setBalance(mapper.toEntityBalance(timeDeposit.getBalance()));
        }
    }

    private Map<Integer, TimeDepositEntity> loadEntitiesById(List<TimeDeposit> timeDeposits) {
        List<Integer> ids = timeDeposits.stream().map(TimeDeposit::getId).toList();
        return jpaRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(TimeDepositEntity::getId, Function.identity()));
    }
}
