package io.github.manavpreet1809.sleeptracker;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Sleep Tracker API")
                .version("1.0.0")
                .description("Record nights of sleep and analyse what is associated with sleeping well."));
    }
}
