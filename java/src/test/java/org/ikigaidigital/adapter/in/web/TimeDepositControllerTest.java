package org.ikigaidigital.adapter.in.web;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimeDepositController.class)
@Import(TimeDepositWebMapper.class)
class TimeDepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;

    @Test
    void getAllTimeDeposits_returnsDepositsWithWithdrawals_whenDepositsExist() throws Exception {
        TimeDeposit premium = new TimeDeposit(11, "premium", 10000.00, 46);
        Withdrawal withdrawal = new Withdrawal(3, 11, new BigDecimal("500.00"), LocalDate.of(2026, 2, 1));
        when(getAllTimeDepositsUseCase.getAll())
                .thenReturn(List.of(new TimeDepositWithWithdrawals(premium, List.of(withdrawal))));

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{
                          "id": 11,
                          "planType": "premium",
                          "balance": 10000.00,
                          "days": 46,
                          "withdrawals": [{"id": 3, "amount": 500.00, "date": "2026-02-01"}]
                        }]
                        """, true));
    }

    @Test
    void getAllTimeDeposits_returnsEmptyWithdrawalsArray_whenDepositHasNoWithdrawals() throws Exception {
        TimeDeposit basic = new TimeDeposit(1, "basic", 1000.00, 15);
        when(getAllTimeDepositsUseCase.getAll())
                .thenReturn(List.of(new TimeDepositWithWithdrawals(basic, List.of())));

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 1, "planType": "basic", "balance": 1000.00, "days": 15, "withdrawals": []}]
                        """, true));
    }

    @Test
    void getAllTimeDeposits_returnsEmptyArray_whenNoDepositsExist() throws Exception {
        when(getAllTimeDepositsUseCase.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]", true));
    }
}
