package net.madz.lifecycle.engine;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import net.madz.bcel.intercept.DefaultStateMachineRegistry;
import net.madz.bcel.intercept.LifecycleInterceptor;
import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.relation.ValidWhile;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Overrides;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Active;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Canceled;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Draft;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Suspended;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Activate;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Cancel;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Resume;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Suspend;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.InternetServiceLifecycleMeta.Relations.CustomerRelation;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.InternetServiceLifecycleMeta.States.InService;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.InternetServiceLifecycleMeta.States.New;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.InternetTVServiceLifecycle.Relations.TVProvider;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.ServiceProviderLifecycle.States.Closed;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.ServiceProviderLifecycle.States.ServiceAvailable;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.ServiceProviderLifecycle.Transitions.Shutdown;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.VOIPServiceLifecycleMeta.Relations.VoipProvider;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationException;

import org.apache.bcel.generic.NEW;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <ol>
 * <li>Core Functions</li>
 * <ol>
 * <li>Perform State Change</li>
 * <ol>
 * <li>State Validation</li>
 * <ol>
 * <li>Relation With ValidWhile</li>
 * </ol>
 * <li>Transition Function Validation</li>
 * <ol>
 * <li>check whether transition is legal to current state</li>
 * </ol>
 * <li>Next State Evaluation and InboundWhile Validation</li>
 * <ol>
 * <li>pre-state-change-phase</li>
 * <li>post-state-change-phase</li>
 * </ol>
 * <li>Execute Transition Method</li> <li>Locking</li>
 * <ol>
 * <li>Locking Strategy</li>
 * <ol>
 * <li>Core Java Lock: native synchronized or java.util.concurrent.ReentryLock</li>
 * <li>JPA Lock:&nbsp;</li>
 * <ol>
 * <li>EntityManager.lock with LockModeType</li>
 * <ol>
 * <li>Be careful with the impact of locking scope while working with different
 * database transaction isolation level.</li>
 * </ol>
 * </ol> <li>CustomizedLocking</li> </ol> </ol> <li>Set New State</li> </ol> <li>
 * Relational</li>
 * <ol>
 * <li>with Optimistic Lock Mode: Touch Related Object to increase&nbsp;related
 * object&nbsp;Optimistic Lock's&nbsp;version. (once object x become managed
 * entity, and then the related object is also managed, after touching those
 * related object, it is expecting the next database synchronization to write it
 * into database. Once there is concurrent modification which will directly lead
 * optimistic lock exception, and then the state change will fail. This is the
 * relational life cycle objects' validation strategy. )</li>
 * <ul>
 * <li>NOTE: Since JPA provides READ and WRITE&nbsp;OPTIMISTIC lock. READ can be
 * applied to check relation scenario, and WRITE can be applied to update state
 * scenario.&nbsp;</li>
 * </ul>
 * <li>Parent State Synchronization Transition to update hierarchical business
 * objects.</li>
 * <ul>
 * <li><span style="font-size: 12px;">Example: For a service business, assuming
 * customer is the top level business object in CRM module/application, and
 * contracts, and service provisioning, and billing, and payment and etc are all
 * the hierarchical children business object. To suspend a customer's service,
 * with this Lifecycle Framework, there are ONLY two things to do:</span></li>
 * <ol>
 * <li><span style="font-size: 12px;">Suspend Customer ONLY, this will lead all
 * the children's business states to invalid states.</span></li>
 * <li><span style="font-size: 12px;">Synchronize the parent's State update with
 * Synchronization ONLY, because all the other transitions cannot happen, since
 * Lifecycle&nbsp;</span>Engine considers the children state is in invalid
 * state.<span style="font-size: 12px;">&nbsp;</span></li>
 * </ol>
 * </ul>
 * </ol>
 * </ol> <li><span style="font-size: 12px;">Recoverable Process</span></li>
 * <ol>
 * <ol>
 * <li>Corrupting invalid state before services started</li>
 * <li>Recover (Resume or Redo) Transition after services are ready.&nbsp;</li>
 * <ol>
 * <li>These transition methods will result in those corrupted recoverable
 * object into the service queue(pool, zone) first to ensure ordering</li>
 * </ol>
 * <li>RecoverableIterator</li>
 * <ol>
 * <li>Application can implement this interface and register the instance into
 * LifecycleModule.</li>
 * </ol>
 * </ol> </ol> <li>Callbacks VS Interceptors</li>
 * <ol>
 * <li>pre-state-change callback</li>
 * <li>post-state-change callback</li>
 * <li>context</li>
 * <ol>
 * <li>lifecycle object</li>
 * <li>transition method</li>
 * <li>method arguments</li>
 * <li>from state</li>
 * <li>possible target states</li>
 * </ol>
 * </ol> <li>Lifecycle Events</li>
 * <ol>
 * <li>StateChangeEvent</li>
 * <ol>
 * <li>Object X is transiting from S1 to S2 with Transition T</li>
 * </ol>
 * <li>TransitionEvent</li>
 * <ol>
 * <li>System Transition</li>
 * <ol>
 * <li>Non-functional Corrupting Object X From S1 to S2</li>
 * <li>Non-functional&nbsp;Recovering Object X From S2 to S1</li>
 * <li>Non-functional&nbsp;Redoing Object X From S2 to S1</li>
 * </ol>
 * <li>Application Transition&nbsp;</li>
 * <ol>
 * <li>Functional Transiting Object X From S1 to S2</li>
 * <li>Functional Transiting Object X From S1 to a Failed state with Fail
 * Transition</li>
 * </ol>
 * </ol> </ol> <li>Versions</li> </ol>
 * 
 * @author Barry
 * 
 */
public class EngineCoreFunctionTests {

    @StateMachine
    static interface CustomerLifecycleMeta {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Activate.class, value = { Active.class })
            static interface Draft {}
            @Functions({ @Function(transition = Suspend.class, value = Suspended.class),
                    @Function(transition = Cancel.class, value = Canceled.class) })
            static interface Active {}
            @Function(transition = Resume.class, value = Active.class)
            static interface Suspended {}
            @End
            static interface Canceled {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Activate {}
            static interface Suspend {}
            static interface Resume {}
            static interface Cancel {}
        }
    }
    @LifecycleMeta(CustomerLifecycleMeta.class)
    public static class Customer {

        private String name;
        private String email;
        private int age;
        private String mobile;
        private String state = Draft.class.getSimpleName();

        // Called by Entity Manager
        protected Customer() {
            super();
        }

        // Called by application
        public Customer(String name, String email) {
            super();
            this.name = name;
            this.email = email;
        }

        @Transition
        public void activate() {}

        @Transition
        public void suspend() {}

        @Transition
        public void resume() {}

        @Transition
        public void cancel() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        // Default @StateIndicator
        public String getState() {
            return state;
        }

        @SuppressWarnings("unused")
        private void setState(String state) {
            this.state = state;
        }
    }

    @BeforeClass
    public static void setup() throws VerificationException {
        DefaultStateMachineRegistry.getInstance().registerLifecycleMeta(Customer.class);
        DefaultStateMachineRegistry.getInstance().registerLifecycleMeta(InternetServiceOrder.class);
    }

    @Test
    public void test_standalone_object_without_relation_lifecycle() throws VerificationException {
        Customer customer = new Customer();
        customer.activate();
        assertEquals(Active.class.getSimpleName(), customer.getState());
        customer.suspend();
        assertEquals(Suspended.class.getSimpleName(), customer.getState());
        customer.resume();
        assertEquals(Active.class.getSimpleName(), customer.getState());
        customer.cancel();
        assertEquals(Canceled.class.getSimpleName(), customer.getState());
    }

    @StateMachine
    static interface InternetServiceLifecycleMeta {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = InternetServiceLifecycleMeta.Transitions.Start.class,
                    value = { InternetServiceLifecycleMeta.States.InService.class })
            @ValidWhile(on = { CustomerLifecycleMeta.States.Active.class }, relation = CustomerRelation.class)
            static interface New {}
            @Function(transition = InternetServiceLifecycleMeta.Transitions.End.class,
                    value = { InternetServiceLifecycleMeta.States.Ended.class })
            @InboundWhile(on = { CustomerLifecycleMeta.States.Active.class }, relation = CustomerRelation.class)
            static interface InService {}
            @End
            static interface Ended {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Start {}
            static interface End {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(value = CustomerLifecycleMeta.class)
            static interface CustomerRelation {}
        }
    }
    @LifecycleMeta(InternetServiceLifecycleMeta.class)
    public class InternetServiceOrder {

        private Date startDate;
        private Date endDate;
        @Relation(CustomerRelation.class)
        private Customer customer;
        private String type;
        private String state = InternetServiceLifecycleMeta.States.New.class.getSimpleName();;

        public InternetServiceOrder() {}

        public InternetServiceOrder(Date startDate, Date endDate, Customer customer, String type) {
            super();
            this.startDate = startDate;
            this.endDate = endDate;
            this.customer = customer;
            this.type = type;
        }

        @Transition
        public void start() {}

        @Transition
        public void end() {}

        public String getState() {
            return state;
        }

        @SuppressWarnings("unused")
        private void setState(String state) {
            this.state = state;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public Customer getCustomer() {
            return customer;
        }

        public String getType() {
            return type;
        }
    }

    @Test
    public void test_standalone_object_with_definite_relation() {
        Customer customer = new Customer();
        customer.activate();
        InternetServiceOrder order = new InternetServiceOrder(new Date(), null, customer, "1 year");
        order.start();
        assertEquals(InService.class.getSimpleName(), order.getState());
    }

    @Test(expected = LifecycleException.class)
    public void test_standalone_object_with_definite_relation_negative() throws LifecycleException {
        Customer customer = new Customer();
        customer.activate();
        customer.cancel();
        assertEquals(Canceled.class.getSimpleName(), customer.getState());
        InternetServiceOrder order = new InternetServiceOrder(new Date(), null, customer, "1 year");
        try {
            order.start();
        } catch (LifecycleException e) {
            assertEquals(LifecycleCommonErrors.STATE_INVALID, e.getErrorCode());
            assertEquals(BundleUtils.getBundledMessage(LifecycleInterceptor.class, LifecycleCommonErrors.BUNDLE,
                    LifecycleCommonErrors.STATE_INVALID, order, order.getState(), customer, customer.getState(),
                    NEW.class.getAnnotation(ValidWhile.class)), e.getMessage());
            throw e;
        }
    }

    @StateMachine
    static interface ServiceProviderLifecycle {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.Shutdown.class, value = Closed.class)
            static interface ServiceAvailable {}
            @End
            static interface Closed {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Shutdown {}
        }
    }
    @StateMachine
    static interface InternetTVServiceLifecycle extends InternetServiceLifecycleMeta {

        @StateSet
        static interface States extends InternetServiceLifecycleMeta.States {

            @ValidWhile(relation = TVProvider.class, on = ServiceAvailable.class)
            static interface New extends InternetServiceLifecycleMeta.States.New {}
        }
        @RelationSet
        static interface Relations extends InternetServiceLifecycleMeta.Relations {

            @RelateTo(InternetTVProviderLifecycle.class)
            static interface TVProvider {}
        }
    }
    @StateMachine
    static interface InternetTVProviderLifecycle extends ServiceProviderLifecycle {}
    public abstract static class ReactiveObject {

        @StateIndicator
        protected String state;

        public String getState() {
            return state;
        }
    }
    public static class BaseService<T extends BaseServiceProvider> extends ReactiveObject {

        private Customer customer;

        public BaseService(Customer customer) {
            this.state = New.class.getSimpleName();
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
    public static class BaseServiceProvider extends ReactiveObject {

        public BaseServiceProvider() {
            this.state = ServiceAvailable.class.getSimpleName();
        }

        @Transition
        void shutdown() {}
    }
    @LifecycleMeta(InternetTVServiceLifecycle.class)
    public static class InternetTVService extends BaseService<InternetTVServiceProvider> {

        public InternetTVService(Customer customer) {
            super(customer);
        }

        @Relation(InternetTVServiceLifecycle.Relations.TVProvider.class)
        public InternetTVServiceProvider getProvider() {
            return super.getProvider();
        }
    }
    @LifecycleMeta(InternetTVProviderLifecycle.class)
    public static class InternetTVServiceProvider extends BaseServiceProvider {}

    @Test
    public void test_inherited_valid_while_relation_validation() {
        final InternetTVServiceProvider provider = new InternetTVServiceProvider();
        assertEquals(ServiceAvailable.class.getSimpleName(), provider.getState());
        Customer customer = new Customer();
        customer.activate();
        assertEquals(Active.class.getSimpleName(), customer.getState());
        final InternetTVService service = new InternetTVService(customer);
        service.setProvider(provider);
        service.start();
        assertEquals(InService.class.getSimpleName(), service.getState());
    }

    @Test(expected = LifecycleException.class)
    public void test_inherited_valid_while_relation_validation_negative_with_super_valid_while()
            throws LifecycleException {
        final InternetTVServiceProvider provider = new InternetTVServiceProvider();
        assertEquals(ServiceAvailable.class.getSimpleName(), provider.getState());
        final Customer customer = new Customer();
        customer.activate();
        assertEquals(Active.class.getSimpleName(), customer.getState());
        customer.cancel();
        assertEquals(Canceled.class.getSimpleName(), customer.getState());
        final InternetTVService service = new InternetTVService(customer);
        service.setProvider(provider);
        try {
            service.start();
        } catch (LifecycleException e) {
            assertEquals(LifecycleCommonErrors.STATE_INVALID, e.getErrorCode());
            assertEquals(BundleUtils.getBundledMessage(LifecycleInterceptor.class, LifecycleCommonErrors.BUNDLE,
                    LifecycleCommonErrors.STATE_INVALID, service, service.getState(), customer, customer.getState(),
                    InternetServiceLifecycleMeta.States.New.class.getAnnotation(ValidWhile.class)), e.getMessage());
            throw e;
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_inherited_valid_while_relation_validation_negative_with_self_valid_while()
            throws LifecycleException {
        final InternetTVServiceProvider provider = new InternetTVServiceProvider();
        assertEquals(ServiceAvailable.class.getSimpleName(), provider.getState());
        provider.shutdown();
        assertEquals(Closed.class.getSimpleName(), provider.getState());
        final Customer customer = new Customer();
        customer.activate();
        assertEquals(Active.class.getSimpleName(), customer.getState());
        final InternetTVService service = new InternetTVService(customer);
        service.setProvider(provider);
        try {
            service.start();
        } catch (LifecycleException e) {
            assertEquals(LifecycleCommonErrors.STATE_INVALID, e.getErrorCode());
            assertEquals(BundleUtils.getBundledMessage(LifecycleInterceptor.class, LifecycleCommonErrors.BUNDLE,
                    LifecycleCommonErrors.STATE_INVALID, service, service.getState(), provider, provider.getState(),
                    InternetServiceLifecycleMeta.States.New.class.getAnnotation(ValidWhile.class)), e.getMessage());
            throw e;
        }
    }

    @StateMachine
    static interface VOIPServiceLifecycleMeta extends InternetServiceLifecycleMeta {

        @StateSet
        static interface States extends InternetServiceLifecycleMeta.States {

            @Overrides
            @ValidWhile(relation = VoipProvider.class, on = ServiceAvailable.class)
            static interface New extends InternetServiceLifecycleMeta.States.New {}
        }
        @RelationSet
        static interface Relations extends InternetServiceLifecycleMeta.Relations {

            @RelateTo(VOIPProviderLifecycleMeta.class)
            static interface VoipProvider {}
        }
    }
    @StateMachine
    static interface VOIPProviderLifecycleMeta extends ServiceProviderLifecycle {}
    @LifecycleMeta(VOIPServiceLifecycleMeta.class)
    public static class VOIPService extends BaseService<VOIPProvider> {

        public VOIPService(Customer customer) {
            super(customer);
        }

        @Relation(InternetTVServiceLifecycle.Relations.TVProvider.class)
        public VOIPProvider getProvider() {
            return super.getProvider();
        }
    }
    @LifecycleMeta(VOIPProviderLifecycleMeta.class)
    public static class VOIPProvider extends BaseServiceProvider {}

    @Test
    public void test_overrides_inherited_valid_while_relation_validation_positive_with_super_valid_while() {
        final VOIPProvider provider = new VOIPProvider();
        final Customer customer = new Customer();
        assertEquals(Draft.class.getSimpleName(), customer.getState());
        final VOIPService service = new VOIPService(customer);
        assertEquals(New.class.getSimpleName(), service.getState());
        service.setProvider(provider);
        service.start();
        assertEquals(InService.class.getSimpleName(), service.getState());
    }

    @Test(expected=LifecycleException.class)
    public void test_overrides_inherited_valid_while_relation_validation_negative_with_self_valid_while() throws LifecycleException{
        final VOIPProvider provider = new VOIPProvider();
        final Customer customer = new Customer();
        assertEquals(Draft.class.getSimpleName(), customer.getState());
        final VOIPService service = new VOIPService(customer);
        assertEquals(New.class.getSimpleName(), service.getState());
        provider.shutdown();
        assertEquals(Shutdown.class.getSimpleName(), provider.getState());
        service.setProvider(provider);
        try {
            service.start();
        } catch (LifecycleException e) {
            assertEquals(LifecycleCommonErrors.STATE_INVALID, e.getErrorCode());
            assertEquals(BundleUtils.getBundledMessage(LifecycleInterceptor.class, LifecycleCommonErrors.BUNDLE,
                    LifecycleCommonErrors.STATE_INVALID, service, service.getState(), provider, provider.getState(),
                    VOIPServiceLifecycleMeta.States.New.class.getAnnotation(ValidWhile.class)), e.getMessage());
            throw e;
        }
    }
}
