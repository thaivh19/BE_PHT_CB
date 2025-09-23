package com.pht.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {
    @Value("${pht.openapi.dev-url}")
    private String devUrl;

    @Value("${pht.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("ida@fis.com");
        contact.setName("IDA");
        contact.setUrl("https://ida.fis.fpt.vn");

        License mitLicense = new License().name("IDA License").url("https://ida.fis.fpt.vn/licenses/mit/");

        Info info = new Info()
                .title("Demo Service API")
                .version("1.0")
                .contact(contact)
                .description("IDA API exposes endpoints.").termsOfService("https://ida.fis.fpt.vn")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
