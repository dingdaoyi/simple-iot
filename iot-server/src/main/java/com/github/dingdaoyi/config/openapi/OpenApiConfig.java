package com.github.dingdaoyi.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation metadata.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI simpleIotOpenApi() {
        return new OpenAPI()
            .openapi("3.1.0")
            .info(new Info()
                .title("Simple IoT API")
                .version("0.1.0")
                .description("Simple IoT is a one-jar IoT platform for device access, telemetry, rule chains, and operations dashboards.")
                .contact(new Contact()
                    .name("dingdaoyi")
                    .url("https://github.com/dingdaoyi/simple-iot"))
                .license(new License()
                    .name("MIT")
                    .url("https://github.com/dingdaoyi/simple-iot/blob/main/LICENSE")));
    }
}
