package com.install.domain.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties("excel")
public class MaxBufferSizeProperties {

	private Integer maxBufferSize;

}
