package com.teaching.employment.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Knife4j API文档配置类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Knife4jConfig {

    /**
     * 创建API文档
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // API信息
                .apiInfo(apiInfo())
                // 选择哪些接口生成文档
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.teaching.employment.controller"))
                .paths(PathSelectors.any())
                .build()
                // 安全认证
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * API信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("高校教学就业平台 API文档")
                .description("提供教学管理和就业管理相关的接口文档")
                .contact(new Contact("开发团队", "", ""))
                .version("1.0.0")
                .build();
    }

    /**
     * 安全方案
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> schemes = new ArrayList<>();
        // API Key认证
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        schemes.add(apiKey);
        return schemes;
    }

    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> contexts = new ArrayList<>();
        contexts.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(operationContext -> true)
                .build());
        return contexts;
    }

    /**
     * 默认的安全引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
    }
}
