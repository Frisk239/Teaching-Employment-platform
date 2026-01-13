package com.teaching.employment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 就业服务平台启动类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@SpringBootApplication
public class EmploymentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmploymentPlatformApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("就业服务平台启动成功!");
        System.out.println("Knife4j文档地址: http://localhost:8080/doc.html");
        System.out.println("========================================\n");
    }
}
