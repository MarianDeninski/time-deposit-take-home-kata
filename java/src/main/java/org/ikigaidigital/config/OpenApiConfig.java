package org.ikigaidigital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// API-level documentation; springdoc generates the rest of the contract from the controller and DTOs.
@Configuration
public class OpenApiConfig {

    private static final String TITLE = "Time Deposit API";
    private static final String VERSION = "1.0.0";
    private static final String LOCAL_SERVER_URL = "http://localhost:8080";

    private static final String DESCRIPTION = """
            API of the XA Bank time deposit system. It exposes exactly two operations:

            | Operation | What it does |
            |---|---|
            | `GET /time-deposits` | Lists all time deposits with their withdrawals |
            | `POST /time-deposits/balance-updates` | Applies one month of interest to all time deposits |

            ### Interest rules (monthly, applied by the balance update)
            | Plan | Annual rate | Paid monthly | Condition |
            |---|---|---|---|
            | `basic` | 1% | 1% / 12 | after the first 30 days |
            | `student` | 3% | 3% / 12 | after the first 30 days, and only up to 365 days (no interest after one year) |
            | `premium` | 5% | 5% / 12 | only after 45 days |

            No plan earns interest during the first 30 days. Interest is rounded to cents (half up).

            ### Idempotency
            Interest is applied at most once per calendar month (UTC). Repeated calls within the same month
            are no-ops and also return `200`, so retries, double clicks or a scheduler firing twice can never
            credit interest twice.

            ### Try it out
            Start the app (`docker-compose up -d` and `mvn spring-boot:run`), then use **Try it out** on an
            operation below. The database is seeded with 11 sample deposits covering every plan boundary.
            """;

    @Bean
    OpenAPI timeDepositOpenApi() {
        return new OpenAPI()
                .info(new Info().title(TITLE).version(VERSION).description(DESCRIPTION))
                .addServersItem(new Server().url(LOCAL_SERVER_URL).description("Local development server"));
    }
}
