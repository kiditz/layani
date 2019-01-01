package com.layani.pulsa.integration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Configuration
@EntityScan(basePackages = { "com.layani.pulsa.entity" })
@EnableJpaRepositories(basePackages = { "com.layani.pulsa.repository" })
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.layani.pulsa.service"})
public class RepositoryConfiguration {

	@Bean
	public LocaleResolver localeResolver() {
		SmartLocaleResolver slr = new SmartLocaleResolver();
		slr.setDefaultLocale(Locale.US); // Set default Locale as US
		return slr;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("i18n/messages"); // name of the resource bundle
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}

	public class SmartLocaleResolver extends CookieLocaleResolver {
		@Override
		public Locale resolveLocale(HttpServletRequest request) {
			String acceptLanguage = request.getHeader("Accept-Language");
			if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
				return super.determineDefaultLocale(request);
			}
			return request.getLocale();
		}
	}

	@Bean
	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
		ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
		return converter;
	}
	
}