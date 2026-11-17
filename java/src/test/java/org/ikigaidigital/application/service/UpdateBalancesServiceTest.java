package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.out.InterestRunRepository;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBalancesServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-17T10:00:00Z");
    private static final YearMonth CURRENT_PERIOD = YearMonth.of(2026, 11);

    @Mock
    private TimeDepositRepository timeDepositRepository;

    @Mock
    private InterestRunRepository interestRunRepository;

    private UpdateBalancesService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new UpdateBalancesService(timeDepositRepository, interestRunRepository, new TimeDepositCalculator(), clock);
    }

    @Test
    void updateBalances_appliesInterestAndSavesDeposits_whenPeriodNotYetRecorded() {
        TimeDeposit basic = new TimeDeposit(1, "basic", 10000.00, 31);
        when(interestRunRepository.recordIfAbsent(CURRENT_PERIOD, NOW)).thenReturn(true);
        when(timeDepositRepository.findAll()).thenReturn(List.of(basic));

        service.updateBalances();

        assertThat(basic.getBalance()).isEqualTo(10008.33);
        verify(timeDepositRepository).saveAll(List.of(basic));
    }

    @Test
    void updateBalances_changesNothing_whenPeriodAlreadyRecorded() {
        when(interestRunRepository.recordIfAbsent(CURRENT_PERIOD, NOW)).thenReturn(false);

        service.updateBalances();

        verifyNoInteractions(timeDepositRepository);
    }

    @Test
    void updateBalances_doesNotSave_whenLoadingDepositsFails() {
        when(interestRunRepository.recordIfAbsent(CURRENT_PERIOD, NOW)).thenReturn(true);
        when(timeDepositRepository.findAll()).thenThrow(new IllegalStateException("Database unavailable"));

        assertThatThrownBy(() -> service.updateBalances())
                .isInstanceOf(IllegalStateException.class);
        verify(timeDepositRepository, never()).saveAll(anyList());
    }
}
