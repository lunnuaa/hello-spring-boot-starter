package com.lun.hello.spring.boot.autoconfigure;

/**
 * @author lun
 * @create 2024/2/4 - 11:31
 */
public class User {

    public User(String name) {
        this.name = name;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public static void main(String[] args) {
        System.out.println("hello-spring-boot-starter");
    }
}
