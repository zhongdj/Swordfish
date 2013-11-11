package net.madz.lifecycle.engine;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.InternetServiceLifecycleMeta.Relations.CustomerRelation;
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.VOIPServiceLifecycleMeta.Relations.VoipProvider;

import org.junit.Test;

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
                    customer.getState(),
                    validWhileDottedPath(InternetServiceLifecycleMeta.States.New.class, CustomerRelation.class));
        }
    }

    private String validWhileDottedPath(Class<?> stateClass, Class<?> relationKeyClss) {
        return stateClass.getDeclaringClass().getDeclaringClass().getName() + ".StateSet." + stateClass.getSimpleName()
                + ".ValidWhiles." + relationKeyClss.getSimpleName();
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
                    customer.getState(),
                    validWhileDottedPath(InternetServiceLifecycleMeta.States.New.class, CustomerRelation.class));
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
            assertLifecycleError(
                    e,
                    LifecycleCommonErrors.STATE_INVALID,
                    service,
                    service.getState(),
                    provider,
                    provider.getState(),
                    validWhileDottedPath(InternetTVServiceLifecycle.States.New.class,
                            InternetTVServiceLifecycle.Relations.TVProvider.class));
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
                    provider.getState(),
                    validWhileDottedPath(VOIPServiceLifecycleMeta.States.New.class, VoipProvider.class));
        }
    }
}
