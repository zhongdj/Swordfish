package net.madz.rs.scheduling.wsocket.messages;

public class ConcreteTruckLifecycleMessage extends SchedulingMessage {

    private static final long serialVersionUID = -8495598124939824931L;

    public ConcreteTruckLifecycleMessage() {
        this.sourceType = getClass().getSimpleName();
    }
}
