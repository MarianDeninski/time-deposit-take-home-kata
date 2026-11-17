package org.ikigaidigital.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "Withdrawal", description = "A withdrawal from a time deposit.")
public record WithdrawalDto(
        @Schema(description = "Unique identifier of the withdrawal.", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Integer id,

        @Schema(description = "Amount withdrawn, with two decimal places.", example = "100.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal amount,

        @Schema(description = "Date of the withdrawal (ISO 8601).", example = "2026-01-15",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate date) {
}
