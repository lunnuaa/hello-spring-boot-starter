package com.lun.hello.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

/**
 * @author lun
 * @create 2024/2/4 - 13:50
 */
@ConfigurationProperties(prefix =  HelloProperties.HELLO_PREFIX)
public class HelloProperties {

    public static final String HELLO_PREFIX = "hello";

    private String name = "hello-spring-boot-starter";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
