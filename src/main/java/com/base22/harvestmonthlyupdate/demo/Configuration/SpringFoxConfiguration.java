package com.base22.harvestmonthlyupdate.demo.Configuration;

import com.google.common.base.Predicates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfiguration {
    private static final Logger logger = LogManager.getLogger();
    @Bean
    public Docket api() {
        logger.info("Registering Swagger2 documentation");

        Docket docket =
                new Docket(DocumentationType.SWAGGER_2).host("google.com").select()
                        .apis(RequestHandlerSelectors.any()).paths(Predicates.not(PathSelectors.regex("/error.*"))).build()
                        .apiInfo(apiInfo());
        logger.info("Swagger2 documentation registered correctly");
        return docket;
    }
    private ApiInfo apiInfo() {

        return new ApiInfoBuilder().title("Harvest Notifier").description("DESCRIPTION").version("1.0.0").build();
    }
}

