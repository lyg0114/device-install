package com.install.global.jdbc;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.querydsl.core.annotations.Config;

/**
 * @author : iyeong-gyo
 * @package : com.install.global.jdbc
 * @since : 13.06.24
 */
@Config
public class JdbcConfig {
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
