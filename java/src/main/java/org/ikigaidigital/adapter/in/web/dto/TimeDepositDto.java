package org.ikigaidigital.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

// "Dto" suffix avoids a clash with the domain TimeDeposit class.
@Schema(name = "TimeDeposit", description = "A time deposit with its current balance and withdrawals.")
public record TimeDepositDto(
        @Schema(description = "Unique identifier of the time deposit.", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Integer id,

        @Schema(description = "Plan type that determines the interest rule (basic, student or premium).",
                example = "basic", requiredMode = Schema.RequiredMode.REQUIRED)
        String planType,

        @Schema(description = "Current balance of the time deposit, with two decimal places.", example = "1000.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal balance,

        @Schema(description = "Number of days since the time deposit was opened.", example = "31",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Integer days,

        @ArraySchema(arraySchema = @Schema(description = "Withdrawals made from this time deposit; empty if there are none.",
                requiredMode = Schema.RequiredMode.REQUIRED))
        List<WithdrawalDto> withdrawals) {

    public TimeDepositDto {
        withdrawals = List.copyOf(withdrawals);
    }
}
