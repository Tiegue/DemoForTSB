package nz.co.tsb.demofortsb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers())
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("authentication")
                .displayName("TSB Auth & Security")
                .pathsToMatch("/api/auth/**", "/api/setup/**")
                .packagesToScan("nz.co.tsb.demofortsb.controller")
                .addOpenApiCustomizer(openApi -> openApi.info(
                        new Info()
                                .title("Authentication API")
                                .description("Login, registration, and setup endpoints (no authentication required)")
                                .version("1.0.0")))
                .build();
    }

    @Bean
    public GroupedOpenApi customersApi() {
        return GroupedOpenApi.builder()
                .group("customers")
                .displayName("TSB Customer")
                .pathsToMatch("/api/customers/**")
                .packagesToScan("nz.co.tsb.demofortsb.controller")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Customer Management API")
                                .description("CRUD operations for customer management (requires JWT authentication)")
                                .version("1.0.0"))
                        .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth")))
                .build();
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all")
                .displayName("Complete API")
                .pathsToMatch("/api/**")
                .packagesToScan("nz.co.tsb.demofortsb.controller")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("DemoForTSB Complete API")
                                .description("All available API endpoints (some require JWT authentication)")
                                .version("1.0.0"))
                        .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth")))
                .build();
    }

    private Info getApiInfo() {
        return new Info()
                .title("DemoForTSB API")
                .description("Customer Management System API for TSB Demo Application")
                .version("1.0.0")
                .contact(getContact())
                .license(getLicense());
    }

    private Contact getContact() {
        return new Contact()
                .name("TSB Development Team")
                .email("dev@tsb.co.nz")
                .url("https://www.tsb.co.nz");
    }

    private License getLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> getServers() {
        String formattedContextPath = contextPath;
        if (!contextPath.isEmpty() && !contextPath.startsWith("/")) {
            formattedContextPath = "/" + contextPath;
        }

        String baseUrl = "http://localhost:" + serverPort + formattedContextPath;

        Server localServer = new Server()
                .url(baseUrl)
                .description("Local Development Server");

        Server productionServer = new Server()
                .url("https://api.tsb.co.nz")
                .description("Production Server");

        return List.of(localServer, productionServer);
    }
}