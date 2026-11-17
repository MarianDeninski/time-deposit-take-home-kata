# Time Deposit Refactoring Kata - Take-Home Assignment

## XA Bank Time Deposit

### Context
A junior developer implemented domain logic for a time deposit system but did not complete the API functionality. Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur.

### Requirements

1. **API Endpoints**:
    - Create a RESTful API endpoint to update the balances of all time deposits in the database.
    - Create a RESTful API endpoint to retrieve all time deposits.
        - The GET endpoint should return a list of all time deposits with the following schema:
            - `id`
            - `planType`
            - `balance`
            - `days`
            - `withdrawals`

2. **Database Setup**:
    - Store all time deposit plans in a database.
    - Define the following tables:
        - `timeDeposits`:
            - `id`: Integer (primary key)
            - `planType`: String (required)
            - `days`: Integer (required)
            - `balance`: Decimal (required)
        - `withdrawals`:
            - `id`: Integer (primary key)
            - `timeDepositId`: Integer (foreign key, required)
            - `amount`: Decimal (required)
            - `date`: Date (required)

3. **Interest Calculation**:
    - Implement logic to calculate monthly interest based on the plan type:
        - **Basic Plan**: 1% interest
        - **Student Plan**: 3% interest (no interest after 1 year)
        - **Premium Plan**: 5% interest (interest starts after 45 days)
    - No interest is applied for the first 30 days for any existing plans.

4. **Refactoring Constraints**:
    - Do not introduce breaking changes to the shared `TimeDeposit` class or modify the `updateBalance` method signature.
    - Ensure the design is extensible to accommodate future complexities in interest calculations.

5. **Code Quality**:
    - Adhere to SOLID principles, design patterns, and clean code practices where applicable.

6. **AI-Assisted Development**:
    - Set up an AI harness or agent workflow and use it throughout the development for this take-home exercise.
    - Briefly document the tools and setup used (e.g., LLMs, coding assistants, agentic frameworks, configuration).
    - Ensure your AI setup is practical and reproducible.
    - Include any custom rules, system prompts, or agent configurations used.
    - Include a brief summary of which parts of the solution were AI-assisted and why.

### Important Guidelines
- The existing `TimeDepositCalculator.updateBalance` method is functioning correctly. Ensure its behavior remains unchanged after refactoring.
- The final solution must include **exactly two API endpoints**. Do not develop additional endpoints.
- **Do not** create a pull request or a new branch in the ikigai-digital repository. Instead, fork the repository into your own GitHub repository and develop the solution there.
- Handling invalid input or exceptions is not required.
- Use any tools, frameworks, or libraries you find suitable.
- In case of ambiguity, make logical assumptions and justify them in code comments.

### Preferred Stack
- Use an OpenAPI Swagger contract.
- Embrace Hexagonal Architecture.
- Follow atomic commit practices.
- Utilize testcontainers.
- Leverage AI-assisted development tools for code generation, testing, and refactoring.

### Submission Instructions
- Provide clear instructions on how to trigger the endpoints using the Swagger contract.
- Email the link to your public GitHub repository.

---

# My solution

## How to run it

You need Java 17, Maven and Docker. On my Mac I use Colima instead of Docker Desktop, so `colima start` first.

All commands are run from the `java/` folder.

```bash
# 1. start PostgreSQL in Docker (use "docker compose" if you have the newer plugin)
docker-compose up -d

# 2. start the app; Flyway creates the tables and loads 11 sample deposits on startup
mvn spring-boot:run
```

That's it, the app is on http://localhost:8080.

### Trying the endpoints in Swagger

Open **http://localhost:8080/swagger-ui.html**. The page explains the interest rules at the top, then shows the two endpoints:

1. **GET /time-deposits** → "Try it out" → "Execute". You get all deposits with their withdrawals.
2. **POST /time-deposits/balance-updates** → "Try it out" → "Execute". Returns 200 and applies this month's interest.
3. Run the GET again: for example deposit 3 (basic, 400 days) went from `10000.00` to `10008.33`.
4. Run the POST again: still 200, but nothing changes, because interest is only applied once per month.

The raw OpenAPI contract is at http://localhost:8080/v3/api-docs.yaml.

Prefer the terminal?

```bash
curl http://localhost:8080/time-deposits
curl -X POST -i http://localhost:8080/time-deposits/balance-updates
```

Want to start over with fresh data? `docker-compose down -v && docker-compose up -d` and restart the app.

### Running the tests

```bash
mvn test      # unit + web tests, no Docker needed
mvn verify    # also the integration tests; Testcontainers starts its own throwaway PostgreSQL
```

On Colima, Testcontainers can't find Docker on its own. Tell it where the socket is, once per machine:

```bash
echo "docker.host=unix://$HOME/.colima/default/docker.sock" >> ~/.testcontainers.properties
```

(The other Colima setting, the socket path inside the VM, is already set for the tests in `pom.xml`.)

## How it's built

- **Hexagonal architecture.** `adapter.in.web` (controller) → `application.port.in` (use cases) → `application.service` → `application.port.out` (repository interfaces) ← `adapter.out.persistence` (JPA). The services don't know about Spring MVC or JPA.
- **The API is code-first.** DTOs and the controller are hand-written in `adapter.in.web`; springdoc generates the OpenAPI contract and Swagger UI from the code. (I started contract-first with a YAML file and generated code, but switched because generated classes in `target/` were confusing to work with in the IDE.)
- **Interest rules are pluggable.** Each plan is an `InterestRule` (`domain/interest`). A new plan or a more complex rule is a new class passed to `TimeDepositCalculator`; the calculator itself doesn't change. `TimeDeposit` and the `updateBalance` signature are untouched, and `TimeDepositCalculatorBehaviourTest` proves the results are exactly the same as the original code (395 cases).
- **Database:** PostgreSQL with Flyway migrations, `ddl-auto: validate` so Hibernate never changes the schema.

## Assumptions

- Assumption: interest is applied once per calendar month; repeated calls within the same month are no-ops to prevent double crediting.
- The month is taken in UTC, so it doesn't depend on the server's time zone. Executed months are stored in a separate `interest_runs` table, written in the same transaction as the balance updates.
- Table and column names are snake_case in the database (`time_deposits`, `plan_type`), because PostgreSQL lowercases unquoted names. Java and the JSON keep camelCase.
- IDs are plain integers without auto-increment, because nothing in the API creates records. The sample data comes from a migration.
- `TimeDeposit.balance` is a `Double` and I didn't change it (shared class). It's converted to `BigDecimal` with 2 decimals when stored in the database and when returned by the API.
- Plan types without a rule (anything other than basic, student, premium) get no interest, same as the original code.

## AI-assisted development

### Setup

- **Claude Code** (Anthropic's coding agent, with a Claude Opus model) in the terminal, next to IntelliJ IDEA.
- **[`CLAUDE.md`](CLAUDE.md)** is my rule book for the agent. Claude Code loads it automatically every session. It covers the architecture, naming, Lombok usage, REST conventions, how to test, and the full spec for the "interest only once per month" behaviour. Anything I didn't want to repeat in every prompt went there.
- **Step prompts.** For every step I wrote a short spec for the agent: a goal, a "Do" list, a "Do NOT" list, a "Done when" checklist and the exact commit messages.

To reproduce it: install Claude Code and open the `java/` folder. `CLAUDE.md` is picked up automatically, so the agent follows the same rules; then work through the task step by step with prompts in the format above.

### How I worked with it

I split the task into small steps and gave the agent one at a time: project setup → database → persistence → GET endpoint → update endpoint → refactoring. After each step it had to run the tests, commit and **stop**, so I could check the result before moving on. When something didn't fit I changed direction. For example, I dropped the generated OpenAPI code and moved to code-first, and I asked for the calculator refactor to be protected by characterization tests first.

The later parts (update endpoint, interest rule refactor, Swagger texts) I drove with shorter chat messages instead of full step specs, relying on the rules in `CLAUDE.md`.

### What the AI did, and why

To be upfront: the AI wrote most of the code, not just the docs. I used it as a fast pair programmer and kept the decisions and the reviewing for myself.

- **Where it helped the most:** the Swagger/OpenAPI documentation and all the descriptions in it, explaining the whole flow, this README, comments explaining assumptions, and the boring but important parts like Flyway scripts, seed data, mappers and test setup with Testcontainers.
- **Code and tests:** I designed each piece before any code was written (the ports, adapters and services, where the transaction boundary goes, and what each test has to prove) and wrote it down as a step spec. The agent implemented it to that spec and the rules in `CLAUDE.md`; I reviewed every step, ran the app and checked the database myself before moving on.
- **What I decided:** the architecture (hexagonal, ports and adapters), the step plan, the "once per month" idempotency rule and how it must work, contract-first vs code-first, keeping `TimeDeposit` untouched, and which suggestions to accept or push back on.

Why like this? The task is small but has a lot of setup and documentation. Letting the agent do the typing meant I could spend my time on the design decisions and on checking that everything really works, and the rules in `CLAUDE.md` make the whole process repeatable.
