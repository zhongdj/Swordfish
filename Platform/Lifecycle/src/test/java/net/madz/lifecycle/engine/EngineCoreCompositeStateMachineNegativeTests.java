package net.madz.lifecycle.engine;

import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;

import org.junit.Test;
import static org.junit.Assert.*;

public class EngineCoreCompositeStateMachineNegativeTests extends EngineCoreCompositeStateMachineMetadata {

    @Test(expected = LifecycleException.class)
    public void test_non_relational_standalone_composite_invalid_transition() {
        final ProductOrder order = new ProductOrder();
        assertState(OrderLifecycle.States.Created.class, order);
        order.start();
        assertState(OrderLifecycle.States.Started.SubStates.OrderCreated.class, order);
        try {
            order.doDeliver();
            fail("should throw LifecycleException");
        } catch (LifecycleException e) {
            try {
                assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                        OrderLifecycle.States.Started.SubTransitions.DoDeliver.class.getSimpleName(),
                        OrderLifecycle.States.Started.SubStates.OrderCreated.class.getSimpleName(), order);
            } catch (LifecycleException ex) {}
        }
        order.doProduce();
        try {
            order.confirmComplete();
            fail("should throw LifecycleException");
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                    OrderLifecycle.States.Started.SubTransitions.ConfirmComplete.class.getSimpleName(),
                    OrderLifecycle.States.Started.SubStates.Producing.class.getSimpleName(), order);
        }
    }
}
