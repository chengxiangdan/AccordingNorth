package com.website;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class JingleWebsiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(JingleWebsiteApplication.class, args);
        System.out.println("请直接访问：http://localhost:8080/roncoo-jui-springboot/index");
    }
}
