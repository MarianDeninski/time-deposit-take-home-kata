package org.ikigaidigital;

import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresContainerConfiguration.class)
class UpdateBalancesIT {

    private static final Instant NOVEMBER = Instant.parse("2026-11-17T10:00:00Z");
    private static final Instant DECEMBER = Instant.parse("2026-12-01T10:00:00Z");
    private static final int BASIC_400_DAYS_ID = 3;
    private static final int BASIC_15_DAYS_ID = 1;
    private static final String BALANCE_UPDATES_PATH = "/time-deposits/balance-updates";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UpdateBalancesUseCase updateBalancesUseCase;

    @MockitoBean
    private Clock clock;

    @MockitoSpyBean
    private TimeDepositRepository timeDepositRepository;

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.update("DELETE FROM interest_runs");
        // Restores the seeded balances (see V2__seed_sample_data.sql), as each call commits.
        jdbcTemplate.update("UPDATE time_deposits SET balance = CASE WHEN id IN (1, 2, 4, 5, 8, 9) THEN 1000.00 ELSE 10000.00 END");
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }

    @Test
    void updateBalances_appliesInterestOnlyOnce_whenCalledTwiceInSamePeriod() throws Exception {
        when(clock.instant()).thenReturn(NOVEMBER);

        mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isOk());
        mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isOk());

        assertThat(balanceOf(BASIC_400_DAYS_ID)).isEqualByComparingTo("10008.33");
        assertThat(balanceOf(BASIC_15_DAYS_ID)).isEqualByComparingTo("1000.00");
        assertThat(recordedPeriods()).isEqualTo(1);
    }

    @Test
    void updateBalances_appliesInterestTwice_whenCalledInTwoDifferentPeriods() throws Exception {
        when(clock.instant()).thenReturn(NOVEMBER);
        mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isOk());

        when(clock.instant()).thenReturn(DECEMBER);
        mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isOk());

        assertThat(balanceOf(BASIC_400_DAYS_ID)).isEqualByComparingTo("10016.67");
        assertThat(recordedPeriods()).isEqualTo(2);
    }

    @Test
    void updateBalances_doesNotRecordPeriod_whenBalanceUpdateFails() {
        when(clock.instant()).thenReturn(NOVEMBER);
        doThrow(new IllegalStateException("Simulated failure while saving balances"))
                .when(timeDepositRepository).saveAll(anyList());

        assertThatThrownBy(() -> updateBalancesUseCase.updateBalances())
                .isInstanceOf(IllegalStateException.class);

        assertThat(recordedPeriods()).isZero();
        assertThat(balanceOf(BASIC_400_DAYS_ID)).isEqualByComparingTo("10000.00");
    }

    private BigDecimal balanceOf(int timeDepositId) {
        return jdbcTemplate.queryForObject("SELECT balance FROM time_deposits WHERE id = ?", BigDecimal.class, timeDepositId);
    }

    private int recordedPeriods() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM interest_runs", Integer.class);
    }
}
