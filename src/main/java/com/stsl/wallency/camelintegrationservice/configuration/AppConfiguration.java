package com.stsl.wallency.camelintegrationservice.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties("app")
public class AppConfiguration {

    private CamelConfig camel;

    private RemitaClient remita;

    @Value("${server.port}")
    String serverPort;

    @Value("${camel.springboot.name}")
    String contextPath;

    @Data
    public static class CamelConfig {
        private String test;

    }

    @Data
    public static class RemitaClient {
        private String publicKey;

        private String username;

        private String password;

        private String liveUsername;

        private String livePassword;

        private boolean demoEnv;

    }

}
