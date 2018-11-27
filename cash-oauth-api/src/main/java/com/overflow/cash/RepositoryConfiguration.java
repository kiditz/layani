package com.overflow.cash;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The Jpa configiration will be accessing application.yml as the configuration
 * file. this will be connected with the service project that working to handle
 * business function and business transaction
 * 
 * @author kiditz
 */
@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = { "com.overflow.cash.entity" })
@EnableJpaRepositories(basePackages = { "com.overflow.cash.repository" })
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.overflow.cash.service" })
public class RepositoryConfiguration {
    
}