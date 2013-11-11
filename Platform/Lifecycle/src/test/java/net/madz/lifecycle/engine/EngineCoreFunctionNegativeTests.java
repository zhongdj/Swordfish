package net.madz.lifecycle.engine;

import java.util.Date;

import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.annotations.relation.ValidWhile;

import org.apache.bcel.generic.NEW;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EngineCoreFunctionNegativeTests extends CoreFuntionTestMetadata {

    @Test(expected = LifecycleException.class)
    public void test_standalone_object_with_definite_relation_negative() throws LifecycleException {
        Customer customer = new Customer();
        customer.activate();
        customer.cancel();
        assertEquals(CustomerLifecycleMeta.States.Canceled.class.getSimpleName(), customer.getState());
        InternetServiceOrder order = new InternetServiceOrder(new Date(), null, customer, "1 year");
        try {
            order.start();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.STATE_INVALID, order, order.getState(), customer,
                    customer.getState(), NEW.class.getAnnotation(ValidWhile.class));
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_inherited_valid_while_relation_validation_negative_with_super_valid_while()
            throws LifecycleException {
        final InternetTVServiceProvider provider = new InternetTVServiceProvider();
        assertEquals(InternetTVProviderLifecycle.States.ServiceAvailable.class.getSimpleName(), provider.getState());
        final Customer customer = new Customer();
        customer.activate();
        assertEquals(CustomerLifecycleMeta.States.Active.class.getSimpleName(), customer.getState());
        customer.cancel();
        assertEquals(CustomerLifecycleMeta.States.Canceled.class.getSimpleName(), customer.getState());
        final InternetTVService service = new InternetTVService(customer);
        service.setProvider(provider);
        try {
            service.start();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.STATE_INVALID, service, service.getState(), customer,
                    customer.getState(), InternetServiceLifecycleMeta.States.New.class.getAnnotation(ValidWhile.class));
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_inherited_valid_while_relation_validation_negative_with_self_valid_while()
            throws LifecycleException {
        final InternetTVServiceProvider provider = new InternetTVServiceProvider();
        assertEquals(InternetTVProviderLifecycle.States.ServiceAvailable.class.getSimpleName(), provider.getState());
        provider.shutdown();
        assertEquals(InternetTVProviderLifecycle.States.Closed.class.getSimpleName(), provider.getState());
        final Customer customer = new Customer();
        customer.activate();
        assertEquals(CustomerLifecycleMeta.States.Active.class.getSimpleName(), customer.getState());
        final InternetTVService service = new InternetTVService(customer);
        service.setProvider(provider);
        try {
            service.start();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.STATE_INVALID, service, service.getState(), provider,
                    provider.getState(), InternetServiceLifecycleMeta.States.New.class.getAnnotation(ValidWhile.class));
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_overrides_inherited_valid_while_relation_validation_negative_with_self_valid_while()
            throws LifecycleException {
        final VOIPProvider provider = new VOIPProvider();
        final Customer customer = new Customer();
        assertEquals(CustomerLifecycleMeta.States.Draft.class.getSimpleName(), customer.getState());
        final VOIPService service = new VOIPService(customer);
        assertEquals(VOIPServiceLifecycleMeta.States.New.class.getSimpleName(), service.getState());
        provider.shutdown();
        assertEquals(VOIPProviderLifecycleMeta.States.Closed.class.getSimpleName(), provider.getState());
        service.setProvider(provider);
        try {
            service.start();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.STATE_INVALID, service, service.getState(), provider,
                    provider.getState(), VOIPServiceLifecycleMeta.States.New.class.getAnnotation(ValidWhile.class));
        }
    }
}
