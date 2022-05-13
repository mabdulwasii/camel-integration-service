package com.stsl.wallency.camelintegrationservice.dto.remita;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Data
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = WebApplicationContext.SCOPE_SESSION)
public class BearerTokenConfiguration {
    private String token;

    private String username;

    private String password;

    private String publicKey;

}