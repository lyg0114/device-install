package com.install.global;

import static com.install.domain.code.entity.CodeSet.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.common.file.service.StorageService;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * @author : iyeong-gyo
 * @package : com.install.global
 * @since : 21.06.24
 */
@Configuration
public class GlobalConfig {

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


	@Profile("local")
	@Bean
	public CommandLineRunner codeInit(CodeRepository codeRepository) {
		return args -> {
			codeRepository.saveAll(getAllCodes());
		};
	}
}
