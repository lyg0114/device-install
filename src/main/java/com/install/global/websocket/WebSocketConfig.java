package com.install.global.websocket;

import com.install.global.websocket.handler.ProgressWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

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
    registry.addHandler(getWebSocketHandler(), "/progress")
        .setAllowedOrigins("*");
  }

  @Bean
  public ProgressWebSocketHandler getWebSocketHandler() {
    return new ProgressWebSocketHandler();
  }

}