package com.layani.pulsa.integration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Locale;

@Configuration
@EntityScan(basePackages = { "com.layani.pulsa.entity" })
@EnableJpaRepositories(basePackages = { "com.layani.pulsa.repository" })
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.layani.pulsa.service"})
public class RepositoryConfiguration {



	@Bean
	JdbcTemplate jdbcTemplate(DataSource dataSource){
		return new JdbcTemplate(dataSource);
	}
	
}