package net.madz.rs.scheduling.wsocket;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/scheduling/ws-endpoint", encoders = {})
public class BroadcastingEndpoint {

    @OnOpen
    public void onOpenConnection(Session session, EndpointConfig config) {
    }

    @OnError
    public void onError(Session session, Throwable t) {
    }

    @OnClose
    public void onCloseConnection(Session session, CloseReason reason) {
    }

    @OnMessage
    public void onMessage(Session session, String noMessage) {
    }
}
