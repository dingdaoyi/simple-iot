package com.github.dingdaoyi.config.openapi;

import com.github.dingdaoyi.config.satoken.SaTokenConfigure;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiDocumentationTest {

    @Test
    @SuppressWarnings("unchecked")
    void saTokenSkipsOpenApiAndSwaggerUiPublicDocumentationEndpoints() {
        List<String> skipUrls = (List<String>) ReflectionTestUtils.getField(SaTokenConfigure.class, "DEFAULT_SKIP_URL");

        assertThat(skipUrls)
            .contains(
                "/doc.html",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/webjars/**",
                "/swagger-resources/**"
            );
    }

    @Test
    void openApiBeanPublishesProjectMetadataAndUsesOpenApi31() {
        OpenAPI openAPI = new OpenApiConfig().simpleIotOpenApi();

        assertThat(openAPI.getOpenapi()).isEqualTo("3.1.0");
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Simple IoT API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("0.1.0");
        assertThat(openAPI.getInfo().getDescription()).contains("one-jar IoT platform");
    }
}
