package net.madz.lifecycle.engine;

import java.lang.annotation.Annotation;

import net.madz.bcel.intercept.DefaultStateMachineRegistry;
import net.madz.bcel.intercept.LifecycleInterceptor;
import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.Customer;
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.InternetServiceLifecycleMeta;
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.ServiceProviderLifecycle;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationException;

import static org.junit.Assert.assertEquals;

public class EngineTestBase {

    public abstract static class ReactiveObject {

        @StateIndicator
        private String state = null;

        protected void initialState(String stateName) {
            if ( null == state ) {
                this.state = stateName;
            } else {
                throw new IllegalStateException("Cannot call initialState method after state had been intialized.");
            }
        }

        public String getState() {
            return state;
        }
    }
    @LifecycleMeta(InternetServiceLifecycleMeta.class)
    public static class BaseService<T extends BaseServiceProvider> extends ReactiveObject {

        private Customer customer;

        public BaseService(Customer customer) {
            initialState(InternetServiceLifecycleMeta.States.New.class.getSimpleName());
            this.customer = customer;
        }

        private T provider;

        public T getProvider() {
            return provider;
        }

        public void setProvider(T provider) {
            this.provider = provider;
        }

        @Relation(InternetServiceLifecycleMeta.Relations.CustomerRelation.class)
        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        @Transition
        void start() {}

        @Transition
        void end() {}
    }
    @LifecycleMeta(ServiceProviderLifecycle.class)
    public static class BaseServiceProvider extends ReactiveObject {

        public BaseServiceProvider() {
            initialState(ServiceProviderLifecycle.States.ServiceAvailable.class.getSimpleName());
        }

        @Transition
        void shutdown() {}
    }

    protected static void registerMetaFromClass(final Class<?> metadataClas) throws VerificationException {
        for ( Class<?> cursorClass = metadataClas; null != cursorClass; cursorClass = cursorClass.getSuperclass() ) {
            for ( final Class<?> c : cursorClass.getDeclaredClasses() ) {
                for ( final Annotation a : c.getDeclaredAnnotations() ) {
                    if ( LifecycleMeta.class == a.annotationType() ) {
                        DefaultStateMachineRegistry.getInstance().registerLifecycleMeta(c);
                        break;
                    }
                }
            }
        }
    }

    protected static void assertLifecycleError(LifecycleException e, final String expectedErrorCode,
            final Object... messageVars) {
        assertEquals(expectedErrorCode, e.getErrorCode());
        assertEquals(BundleUtils.getBundledMessage(LifecycleInterceptor.class, LifecycleCommonErrors.BUNDLE,
                expectedErrorCode, messageVars), e.getMessage());
        throw e;
    }

    public EngineTestBase() {
        super();
    }
}