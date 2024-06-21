package com.install;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.install.domain.common.file.config.StorageProperties;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class InstallApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstallApplication.class, args);
	}
}
