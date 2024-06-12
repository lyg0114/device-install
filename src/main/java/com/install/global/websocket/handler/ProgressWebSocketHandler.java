package com.install.global.websocket.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author : iyeong-gyo
 * @package : com.install.global.websocket.handler
 * @since : 12.06.24
 */
public class ProgressWebSocketHandler extends TextWebSocketHandler {

  private WebSocketSession session;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    this.session = session;
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    this.session = null;
  }

  public void sendProgressUpdate(String message) throws Exception {
    if (this.session != null && this.session.isOpen()) {
      this.session.sendMessage(new TextMessage(message));
    }
  }
}