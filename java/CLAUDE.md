# Project rules (Java / Spring Boot)

## Working style (keep token use low)
- Read only files needed for the task; do not re-read files already in context.
- Make minimal, targeted edits; no unrelated refactoring or reformatting.
- Do not restate the request or paste back unchanged code; end with a 1-3 line summary.
- Run only the tests affected by the change unless asked for the full suite.
- If the requirement is ambiguous, ask one short question before coding.

## Design
- Layers: Controller (HTTP only) -> Service (logic) -> Repository (storage).
- SOLID; constructor injection with `final` fields, no field `@Autowired`; depend on interfaces.
- Composition over inheritance; new behaviour via new implementation, not more `if`/`switch`.
- KISS/YAGNI: no speculative abstractions. Extract duplication on third occurrence.

## Methods and names
- One thing per method, ~20 lines, max 3 params (group into a record), no boolean flag params.
- Guard clauses over nesting. Commands and queries are separate; no hidden side effects.
- Intent-revealing names, no abbreviations; constants or enums instead of magic values.

## Java
- Immutable by default: records for DTOs/value objects, `List.copyOf` for exposed collections.
- Never return `null`: `Optional<T>` for single values (return type only), empty collection for many.
- Specific exceptions with the identifier in the message (`RideNotFoundException("Ride 42 not found")`); never generic `RuntimeException`, never empty `catch`.
- Money: `BigDecimal` or integer cents. Time: `java.time`, inject `Clock`.
- Shared in-memory state: `ConcurrentHashMap`/atomics; singleton beans hold no request state.
- SLF4J with placeholders; no `System.out`, no sensitive data in logs.

## Builder and Lombok
- Create objects with 3+ fields via builder (`@Builder`), not long constructors or setters; applies to DTOs, domain objects and test data.
- Immutable types: record + `@Builder`, or class with `@Value` + `@Builder`. Use `toBuilder = true` for modified copies instead of setters.
- Defaults need `@Builder.Default`; collections may use `@Singular`.
- Jackson-deserialized builder classes: add `@Jacksonized`.
- Required fields: validate in the service or with Bean Validation; a builder does not enforce them.
- No `@Data` or class-level `@Setter` on domain objects; add `@Getter` and only the setters truly needed.
- JPA entities: no `@Data`, `@Value` or `@EqualsAndHashCode`; use `@Getter`, `@NoArgsConstructor(access = PROTECTED)`, `@Builder` on a constructor, and `@ToString.Exclude` on lazy relations.
- Never `@SneakyThrows`, `val/var` from Lombok, or `@Cleanup`; no hand-written code that Lombok can generate.

## REST
- The API is code-first. DTOs and the controller are hand-written in `adapter.in.web`; springdoc generates the OpenAPI contract and Swagger UI from the code.
- Request/response records separate from domain classes; `@Valid` + Bean Validation at the edge.
- One `@RestControllerAdvice`, error body `{"error": "..."}`.
- Status: 201 created, 200 read, 204 no content, 400 invalid, 404 missing, 409 conflict.
- Plural noun paths, no verbs. Config via `@ConfigurationProperties`, nothing hard-coded.

## Tests
- JUnit 5 + Mockito for all service logic; `@WebMvcTest` for controllers incl. error cases.
- Arrange/Act/Assert, one behaviour per test, name `method_expected_whenCondition`.
- Mock only external dependencies. Cover happy path, edge and failure cases.

## Comments
- Explain why, not what. No commented-out code, dead code or unused imports.

## Idempotency: balance update endpoint

### Business rule
- The update-balances endpoint applies ONE month of interest to all time deposits.
- Interest must be applied at most once per period (calendar month, format `YYYY-MM`).
- Calling the endpoint again in the same period is a no-op: return 200 OK, change no balances.
- Reason: retries, double clicks or a scheduler firing twice must never credit interest twice.

### Implementation rules
- Do NOT change the `timeDeposits` or `withdrawals` schemas defined in the task.
- Track executed periods in a separate table:
    - `interestRuns`: `id` (PK), `period` (String, UNIQUE, required), `executedAt` (Timestamp, required)
- Flow, inside ONE database transaction:
    1. Try to insert the current period into `interestRuns`.
    2. If the period already exists (unique constraint), skip the calculation and return OK.
    3. Otherwise run the balance update for all deposits and commit.
- The insert and the balance updates must commit or roll back together, so a crash can never leave a period marked as done without the interest applied.
- Rely on the unique constraint, not on a "select then insert" check, so concurrent calls are also safe.
- Get the current period from an injected `Clock` (never `LocalDate.now()` directly), so tests can control time.

### Architecture (hexagonal)
- The idempotency check belongs in the application/use-case layer, not in the controller or in the domain calculator.
- Define an outbound port (e.g. `InterestRunRepository`) for recording executed periods; implement it in the persistence adapter.
- Do NOT modify `TimeDepositCalculator.updateBalance` or the `TimeDeposit` class; idempotency wraps around the existing logic.
- Keep it minimal: no Idempotency-Key headers, no generic idempotency framework, no extra endpoints.

### Tests (Testcontainers)
- Calling the endpoint twice in the same period changes balances only once.
- Calling it in two different periods (controlled via `Clock`) applies interest twice.
- If the balance update fails, the period is NOT recorded in `interestRuns`.

### Documentation
- Add a code comment at the idempotency check and a short note in the README:
  "Assumption: interest is applied once per calendar month; repeated calls within the same month are no-ops to prevent double crediting."
