package net.madz.rs.scheduling.wsocket.messages;

public class JobLifecycleMessage extends SchedulingMessage {

    private static final long serialVersionUID = -6188643026300173646L;

    public JobLifecycleMessage() {
        this.sourceType = getClass().getSimpleName();
    }
}
