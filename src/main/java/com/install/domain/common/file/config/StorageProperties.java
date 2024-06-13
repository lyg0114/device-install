package com.install.domain.common.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties("storage")
public class StorageProperties {

	private String location;

	public void setLocation(String location) {
		this.location = location;
	}
}
