package net.madz.rs.scheduling.wsocket.encoders;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import net.madz.rs.scheduling.wsocket.messages.SchedulingMessage;

public class SchedulingMessageEncoder implements Encoder.Text<SchedulingMessage> {

    @Override
    public void init(EndpointConfig config) {
        // TODO Auto-generated method stub
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public String encode(SchedulingMessage object) throws EncodeException {
        // TODO Auto-generated method stub
        return null;
    }
}
