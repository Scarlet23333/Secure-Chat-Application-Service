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
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
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

    private String topicDestination;
    private String chatRoomId;
    private Message testMessage;
    CompletableFuture<Message> receivedMessageFuture;

    @BeforeEach
    public void setup() {
        chatRoomId = "123";
        topicDestination = "/topic/messages/" + chatRoomId;

        testMessage = new Message(chatRoomId, "user1", "Test message", TimeConvertUtil.localDateTimeToTimestamp(LocalDateTime.now()));

        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    private class MyStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            System.out.println("Headers: " + stompHeaders.toString());
            System.out.println("Subscribed to topic: " + topicDestination);
            return Message.class;
        }
    
        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
            System.out.println("Received message: " + payload);
            receivedMessageFuture.complete((Message) payload);
        }
    }

    private class MyStompSessionHandler extends StompSessionHandlerAdapter{
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(topicDestination, new MyStompFrameHandler());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            throw new RuntimeException("Failure in WebSocket Stomp Session Handling", exception);
        }
    }

    @Test
    public void testRestApiAndWebSocketIntegration() throws Exception {
        receivedMessageFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new MyStompSessionHandler();

        StompSession session = stompClient.connectAsync("ws://localhost:" + port + "/chat", sessionHandler)
                .get(5, TimeUnit.SECONDS);
        assertTrue(session.isConnected());
        System.out.println("session id: " + session.getSessionId());

        // Use RestTemplate to POST a message to the REST API
        String apiUrl = "http://localhost:" + port + "/api/messages/" + chatRoomId;

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, testMessage, String.class);

        // Assert that the REST API responded successfully
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        // Verify that the WebSocket client receives the message
        Message receivedMessage = receivedMessageFuture.get(5, TimeUnit.SECONDS);

        // Assert the received WebSocket message matches the sent message
        assertThat(receivedMessage).isEqualTo(testMessage);
    }
}
