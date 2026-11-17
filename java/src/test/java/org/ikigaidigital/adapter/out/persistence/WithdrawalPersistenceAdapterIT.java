package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.PostgresContainerConfiguration;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostgresContainerConfiguration.class, WithdrawalPersistenceAdapter.class, WithdrawalMapper.class})
class WithdrawalPersistenceAdapterIT {

    @Autowired
    private WithdrawalPersistenceAdapter adapter;

    @Test
    void findAll_returnsAllSeededWithdrawals_whenDatabaseIsMigrated() {
        assertThat(adapter.findAll()).containsExactlyInAnyOrder(
                new Withdrawal(1, 3, new BigDecimal("100.00"), LocalDate.of(2026, 1, 15)),
                new Withdrawal(2, 6, new BigDecimal("250.00"), LocalDate.of(2026, 3, 10)),
                new Withdrawal(3, 11, new BigDecimal("500.00"), LocalDate.of(2026, 2, 1)),
                new Withdrawal(4, 11, new BigDecimal("200.00"), LocalDate.of(2026, 5, 20)),
                new Withdrawal(5, 11, new BigDecimal("50.00"), LocalDate.of(2026, 8, 5)));
    }
}
