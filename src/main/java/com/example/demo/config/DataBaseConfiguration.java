package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.Serializable;

@Configuration
@EnableJpaRepositories("com.example.demo.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditAware")
@EnableTransactionManagement
public class DataBaseConfiguration {
}
