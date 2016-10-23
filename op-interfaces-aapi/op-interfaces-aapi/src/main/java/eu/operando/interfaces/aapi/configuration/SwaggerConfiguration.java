/*******************************************************************************
 *  # Copyright (c) 2016 {UPRC}.
 *  # All rights reserved. This program and the accompanying materials
 *  # are made available under the terms of the The MIT License (MIT).
 *  # which accompanies this distribution, and is available at
 *  # http://opensource.org/licenses/MIT
 *
 *  # Contributors:
 *  #    {Constantinos Patsakis} {UPRC}
 *  #    {Stamatis Glykos} {UPRC}
 *  #    {Constantinos Alexandris} {UPRC}
 *  # Initially developed in the context of OPERANDO EU project www.operando.eu 
 *******************************************************************************/
package eu.operando.interfaces.aapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import io.swagger.annotations.SwaggerDefinition;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** 
 * @author sg
 * @since 4.1.5
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "eu.operando.interfaces.aapi")
@EnableSwagger2
@SwaggerDefinition(basePath="/authenticationservice")
class SwaggerConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Bean
	public Docket api() { 
		return new Docket(DocumentationType.SWAGGER_2)  
				.select()                                  
				.apis(RequestHandlerSelectors.any())              
				.paths(PathSelectors.any())                          
				.build()
				.apiInfo(apiInfo());                                           
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(
				"OPERANDO's Authentication Service's API (AAPI)",
				"A Restful API provided by OPERANDO's AS for authentication/authorization",
				"1.0.0",
				"termsOfService = \"share and care\"",
				"@Contact(name = \"Patsakis-Costas\", email = \"kpatsak@gmail.com\", url = \"\")",
				"license = @License(name = \"Apache 2.0\", url = \"http://www.apache.org\"",          
				null);        
		return apiInfo;
	}
}
