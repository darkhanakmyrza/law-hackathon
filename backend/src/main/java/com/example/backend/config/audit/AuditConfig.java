package com.example.backend.config.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

  @Autowired
  private AuditorAwareImpl auditorAware;

  @Bean
  public AuditorAware<Long> auditorProvider() {
    return auditorAware;
  }
}
