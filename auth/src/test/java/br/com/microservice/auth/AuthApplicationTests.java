package br.com.microservice.auth;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@SpringBootTest
class AuthApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void authTest() {
        String bCryptPasswordEncoder = new BCryptPasswordEncoder().encode("admin");
        log.info("PasswordEncoder: '{}'", bCryptPasswordEncoder);
        Assert.notEmpty(Collections.singletonList(bCryptPasswordEncoder),
                "Error when performing password encryption.");
    }

}
