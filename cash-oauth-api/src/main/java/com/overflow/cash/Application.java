package com.overflow.cash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;




/**
 * @author kiditz
 * @since Saturday 16 September 2017
 */
@SpringBootApplication
@EnableDiscoveryClient
//@EnableScheduling
public class Application extends WebMvcConfigurerAdapter {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}

//	@Bean
//	public ConfigWatch configWatch(ConsulConfigProperties properties, ConsulPropertySourceLocator locator,ConsulClient client) {
//		return new ConfigWatch(properties, locator.getContexts(), client);
//	}
}