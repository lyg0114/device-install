package com.install;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.install.domain.common.file.config.StorageProperties;
import com.install.domain.common.file.service.StorageService;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class InstallApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstallApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

	@Bean
	public CommandLineRunner fileInit(StorageService storageService) {
		return args -> {
			storageService.init();
		};
	}
}
