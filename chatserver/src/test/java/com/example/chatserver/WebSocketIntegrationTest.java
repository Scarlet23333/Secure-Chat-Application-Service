package com.example.chatserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.example.chatserver.models.Message;
import com.example.chatserver.utils.TimeConvertUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private WebSocketStompClient stompClient;

    private String topicDestination1, topicDestination2;
    private String chatRoomId, userId, friendId;
    private Message testMessage;
    CompletableFuture<String> receivedMessageFuture, receivedFriendApplicationFuture;

    @BeforeEach
    public void setup() {
        chatRoomId = "123";
        userId = "12";
        friendId = "123";
        topicDestination1 = "/topic/messages/" + chatRoomId;
        topicDestination2 = "/topic/friend/" + friendId;

        testMessage = new Message(chatRoomId, "user1", "Test message", 
                                  TimeConvertUtil.localDateTimeToTimestamp(LocalDateTime.now()));

        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    private class MessageStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            System.out.println("Subscribed to topic: " + topicDestination1);
            return String.class;
        }
    
        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
            System.out.println("Received message: " + payload);
            receivedMessageFuture.complete((String) payload);
        }
    }

    private class AuthStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            System.out.println("Subscribed to topic: " + topicDestination2);
            return String.class;
        }
    
        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
            System.out.println("Received message: " + payload);
            receivedFriendApplicationFuture.complete((String) payload);
        }
    }

    private class MessageStompSessionHandler extends StompSessionHandlerAdapter{
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(topicDestination1, new MessageStompFrameHandler());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            throw new RuntimeException("Failure in WebSocket Stomp Session Handling", exception);
        }
    }

    private class AuthStompSessionHandler extends StompSessionHandlerAdapter{
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(topicDestination2, new AuthStompFrameHandler());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            throw new RuntimeException("Failure in WebSocket Stomp Session Handling", exception);
        }
    }

    @Test
    public void testSendMessageAndSubscribe() throws Exception {
        receivedMessageFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new MessageStompSessionHandler();
        StompSession session = stompClient.connectAsync("ws://localhost:" + port + "/chat", sessionHandler)
                .get(5, TimeUnit.SECONDS);
        assertTrue(session.isConnected());

        // Use RestTemplate to POST a message to the REST API
        String apiUrl = "http://localhost:" + port + "/api/messages/" + chatRoomId;
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, testMessage, String.class);

        // Assert that the REST API responded successfully
        assertThat(response.getBody()).isEqualTo("Message sent.");
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        // Verify that the WebSocket client receives the friend application
        String receivedMessage = receivedMessageFuture.get(5, TimeUnit.SECONDS);

        // Assert the received WebSocket message matches the sent message
        assertThat(receivedMessage).isEqualTo("message update");
    }

    @Test
    public void testSendFriendApplicationAndSubscribe() throws Exception {
        receivedFriendApplicationFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new AuthStompSessionHandler();
        StompSession session = stompClient.connectAsync("ws://localhost:" + port + "/chat", sessionHandler)
                .get(5, TimeUnit.SECONDS);
        assertTrue(session.isConnected());

        // Use RestTemplate to POST a message to the REST API
        String apiUrl = "http://localhost:" + port + "/api/auth/" + userId;
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, friendId, String.class);

        // Assert that the REST API responded successfully
        assertThat(response.getBody()).isEqualTo("Friend application sent.");
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        // Verify that the WebSocket client receives the message
        String receivedFriendApplication = receivedFriendApplicationFuture.get(5, TimeUnit.SECONDS);

        // Assert the received WebSocket message matches the sent message
        assertThat(receivedFriendApplication).isEqualTo(userId);
    }
}
