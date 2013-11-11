package net.madz.lifecycle.engine;

import java.util.Date;

import net.madz.verification.VerificationException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EngineCoreFunctionPositiveTests extends CoreFuntionTestMetadata {

    @Test
    public void test_standalone_object_without_relation_lifecycle() throws VerificationException {
        Customer customer = new Customer();
        customer.activate();
        assertEquals(CustomerLifecycleMeta.States.Active.class.getSimpleName(), customer.getState());
        customer.suspend();
        assertEquals(CustomerLifecycleMeta.States.Suspended.class.getSimpleName(), customer.getState());
        customer.resume();
        assertEquals(CustomerLifecycleMeta.States.Active.class.getSimpleName(), customer.getState());
        customer.cancel();
        assertEquals(CustomerLifecycleMeta.States.Canceled.class.getSimpleName(), customer.getState());
    }

    @Test
    public void test_standalone_object_with_definite_relation() {
        Customer customer = new Customer();
        customer.activate();
        InternetServiceOrder order = new InternetServiceOrder(new Date(), null, customer, "1 year");
        order.start();
        assertEquals(InternetServiceLifecycleMeta.States.InService.class.getSimpleName(), order.getState());
    }

    @Test
    public void test_inherited_valid_while_relation_validation() {
        final InternetTVServiceProvider provider = new InternetTVServiceProvider();
        assertEquals(InternetTVProviderLifecycle.States.ServiceAvailable.class.getSimpleName(), provider.getState());
        Customer customer = new Customer();
        customer.activate();
        assertEquals(CustomerLifecycleMeta.States.Active.class.getSimpleName(), customer.getState());
        final InternetTVService service = new InternetTVService(customer);
        service.setProvider(provider);
        service.start();
        assertEquals(InternetServiceLifecycleMeta.States.InService.class.getSimpleName(), service.getState());
    }

    @Test
    public void test_overrides_inherited_valid_while_relation_validation_positive_with_super_valid_while() {
        final VOIPProvider provider = new VOIPProvider();
        final Customer customer = new Customer();
        assertEquals(CustomerLifecycleMeta.States.Draft.class.getSimpleName(), customer.getState());
        final VOIPService service = new VOIPService(customer);
        assertEquals(VOIPServiceLifecycleMeta.States.New.class.getSimpleName(), service.getState());
        service.setProvider(provider);
        service.start();
        assertEquals(VOIPServiceLifecycleMeta.States.InService.class.getSimpleName(), service.getState());
    }
}
