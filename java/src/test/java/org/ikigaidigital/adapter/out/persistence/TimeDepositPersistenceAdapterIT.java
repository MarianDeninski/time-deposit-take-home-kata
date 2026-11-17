package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.PostgresContainerConfiguration;
import org.ikigaidigital.TimeDeposit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostgresContainerConfiguration.class, TimeDepositPersistenceAdapter.class, TimeDepositMapper.class})
class TimeDepositPersistenceAdapterIT {

    private static final int SEEDED_DEPOSIT_COUNT = 11;

    @Autowired
    private TimeDepositPersistenceAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAll_returnsAllSeededDeposits_whenDatabaseIsMigrated() {
        List<TimeDeposit> deposits = adapter.findAll();

        assertThat(deposits)
                .extracting(TimeDeposit::getId, TimeDeposit::getPlanType, TimeDeposit::getDays, TimeDeposit::getBalance)
                .containsExactlyInAnyOrder(
                        tuple(1, "basic", 15, 1000.00),
                        tuple(2, "basic", 31, 1000.00),
                        tuple(3, "basic", 400, 10000.00),
                        tuple(4, "student", 15, 1000.00),
                        tuple(5, "student", 31, 1000.00),
                        tuple(6, "student", 365, 10000.00),
                        tuple(7, "student", 366, 10000.00),
                        tuple(8, "premium", 15, 1000.00),
                        tuple(9, "premium", 31, 1000.00),
                        tuple(10, "premium", 45, 10000.00),
                        tuple(11, "premium", 46, 10000.00));
    }

    @Test
    void saveAll_updatesBalancesWithoutAddingRows_whenDepositsExist() {
        List<TimeDeposit> deposits = adapter.findAll();
        deposits.forEach(deposit -> deposit.setBalance(deposit.getBalance() + 0.5));

        adapter.saveAll(deposits);
        entityManager.flush();
        entityManager.clear();

        List<TimeDeposit> reloaded = adapter.findAll();
        assertThat(reloaded).hasSize(SEEDED_DEPOSIT_COUNT);
        assertThat(reloaded)
                .filteredOn(deposit -> deposit.getId() == 3)
                .extracting(TimeDeposit::getBalance)
                .containsExactly(10000.50);
    }
}
