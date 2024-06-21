package com.install.global.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.global.websocket.handler.ProgressWebSocketHandler;

/**
 * @author : iyeong-gyo
 * @package : com.install.global.websocket
 * @since : 12.06.24
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(progressWebSocketHandler(), "/progress/**");
	}

	@Bean
	public ProgressWebSocketHandler progressWebSocketHandler() {
		return new ProgressWebSocketHandler(objectMapper());
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}