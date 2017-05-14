package org.techforumist.ui.websocket;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author Sarath Muraleedharan
 *
 */
@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

	List<WebSocketSession> listSession = new ArrayList<WebSocketSession>();

	public void sendMessage(String message) {
		for (WebSocketSession session : listSession) {
			if (session != null && session.isOpen()) {
				try {
					session.sendMessage(new TextMessage(message));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Don't have open session to send:" + message);
			}
		}

	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		System.out.println("Connection established");
		listSession.add(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		if ("CLOSE".equalsIgnoreCase(message.getPayload())) {
			session.close();
		} else {
			System.out.println("Received:" + message.getPayload());
		}
	}
}
