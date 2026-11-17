package org.ikigaidigital.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.adapter.in.web.dto.TimeDepositDto;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/time-deposits")
@RequiredArgsConstructor
@Tag(name = "TimeDeposits", description = "Time deposits, their withdrawals and the monthly interest run")
class TimeDepositController {

    private static final String GET_ALL_EXAMPLE = """
            [
              {"id": 1, "planType": "basic", "balance": 1000.00, "days": 15, "withdrawals": []},
              {"id": 3, "planType": "basic", "balance": 10000.00, "days": 400,
               "withdrawals": [{"id": 1, "amount": 100.00, "date": "2026-01-15"}]},
              {"id": 11, "planType": "premium", "balance": 10000.00, "days": 46,
               "withdrawals": [{"id": 3, "amount": 500.00, "date": "2026-02-01"},
                               {"id": 4, "amount": 200.00, "date": "2026-05-20"}]}
            ]
            """;

    private final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;
    private final UpdateBalancesUseCase updateBalancesUseCase;
    private final TimeDepositWebMapper mapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "getAllTimeDeposits",
            summary = "Get all time deposits",
            description = "Returns all time deposits ordered by id, each with its list of withdrawals. "
                    + "A deposit without withdrawals has an empty `withdrawals` array.")
    @ApiResponse(responseCode = "200", description = "All time deposits with their withdrawals.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = TimeDepositDto.class)),
                    examples = @ExampleObject(value = GET_ALL_EXAMPLE)))
    public List<TimeDepositDto> getAllTimeDeposits() {
        return getAllTimeDepositsUseCase.getAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @PostMapping("/balance-updates")
    @Operation(
            operationId = "updateAllTimeDepositBalances",
            summary = "Apply one month of interest to all time deposits",
            description = """
                    Applies one month of interest to the balance of every time deposit, using the interest \
                    rules described above. No request body is needed.

                    Interest is applied at most once per calendar month (UTC). Repeated calls within the same \
                    month are no-ops and also return `200`, so interest can never be credited twice. \
                    Call `GET /time-deposits` afterwards to see the new balances, for example deposit 3 \
                    (basic, 400 days) goes from `10000.00` to `10008.33`.""")
    @ApiResponse(responseCode = "200", description = "Interest for the current month has been applied, either by "
            + "this call or by an earlier call in the same month. No response body.",
            content = @Content)
    public ResponseEntity<Void> updateAllTimeDepositBalances() {
        updateBalancesUseCase.updateBalances();
        return ResponseEntity.ok().build();
    }
}
