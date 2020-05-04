package br.com.microservice.core.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "jwt.config")
public class JwtConfiguration {

    private String loginUrl = "/login/**";
    private String privateKey = "vZ4gjwpF3RDE5SO9p08HPk3SS8pdtGGY";
    private String type = "encrypted";

    private int expiration = 3600;

    @NestedConfigurationProperty
    private Header header = new Header();

    @Getter
    @Setter
    public static class Header {
        private String name = "Authorization";
        private String prefix = "Bearer ";
    }
}
