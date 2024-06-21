package com.install.global.websocket.handler;

import static java.util.Objects.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : iyeong-gyo
 * @package : com.install.global.websocket.handler
 * @since : 12.06.24
 */
@Slf4j
public class ProgressWebSocketHandler extends TextWebSocketHandler {

	private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		String sessionId = requireNonNull(session.getUri()).getPath().split("/progress/")[1];
		sessions.put(sessionId, session);
		log.info("Connection established with session ID: {}", sessionId);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		String sessionId = requireNonNull(session.getUri()).getPath().split("/progress/")[1];
		sessions.remove(sessionId);
		log.info("Connection closed for session ID: {}", sessionId);
	}

	// TODO : client쪽에 데이터 전달시 단순 text가 아닌 json 형태의 데이터 전달 할 수 있도록 개선 필요.
	public void sendProgressUpdate(String sessionId, String message) throws IOException {
		WebSocketSession session = sessions.get(sessionId);
		if (session != null && session.isOpen()) {
			session.sendMessage(new TextMessage(message));
		}
	}

	public void closeSession(String sessionId) throws IOException {
		WebSocketSession session = sessions.get(sessionId);
		if (session != null && session.isOpen()) {
			session.close();
			log.info("Session closed for session ID: {}", sessionId);
		}
	}
}