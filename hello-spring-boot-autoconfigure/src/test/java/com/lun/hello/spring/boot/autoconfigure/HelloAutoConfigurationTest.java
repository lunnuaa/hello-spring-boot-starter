package com.lun.hello.spring.boot.autoconfigure;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author lun
 * @create 2024/2/4 - 14:13
 */
class HelloAutoConfigurationTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HelloAutoConfiguration.class));

    @Test
    void testUserAlreadyExists() {
        this.contextRunner.withUserConfiguration(UserConfiguration.class).run(
                context -> {
                    assertThat(context.getBeanNamesForType(User.class)).hasSize(1);
                });
    }
    @Test
    void testUserAutoConfigure() {
        this.contextRunner.run(
                context -> {
                    assertThat(context.getBeanNamesForType(User.class)).hasSize(1);
                });
    }

    @Configuration
    static class UserConfiguration {
        @Bean
        User user() {
            return new User();
        }
    }
}