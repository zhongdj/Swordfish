package net.madz.lifecycle.engine;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.util.Date;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.LifecycleMeta;
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
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.CustomerLifecycleMeta.States.Draft;
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.InternetTVServiceLifecycle.Relations.TVProvider;
import net.madz.lifecycle.engine.CoreFuntionTestMetadata.ServiceProviderLifecycle.States.ServiceAvailable;
import net.madz.verification.VerificationException;

import org.junit.BeforeClass;

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
public class CoreFuntionTestMetadata extends EngineTestBase {

    @BeforeClass
    public static void setup() throws VerificationException {
        registerMetaFromClass(CoreFuntionTestMetadata.class);
    }

    @StateMachine
    protected static interface CustomerLifecycleMeta {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = CustomerLifecycleMeta.Transitions.Activate.class, value = { Active.class })
            static interface Draft {}
            @Functions({
                    @Function(transition = CustomerLifecycleMeta.Transitions.Suspend.class, value = Suspended.class),
                    @Function(transition = CustomerLifecycleMeta.Transitions.Cancel.class, value = Canceled.class) })
            static interface Active {}
            @Function(transition = CustomerLifecycleMeta.Transitions.Resume.class, value = Active.class)
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

        private String state = Draft.class.getSimpleName();

        protected Customer() {}

        @Transition
        public void activate() {}

        @Transition
        public void suspend() {}

        @Transition
        public void resume() {}

        @Transition
        public void cancel() {}

        // Default @StateIndicator
        public String getState() {
            return state;
        }

        @SuppressWarnings("unused")
        private void setState(String state) {
            this.state = state;
        }
    }
    @StateMachine
    protected static interface InternetServiceLifecycleMeta {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = InternetServiceLifecycleMeta.Transitions.Start.class,
                    value = { InternetServiceLifecycleMeta.States.InService.class })
            @ValidWhile(on = { CustomerLifecycleMeta.States.Active.class },
                    relation = InternetServiceLifecycleMeta.Relations.CustomerRelation.class)
            static interface New {}
            @Function(transition = InternetServiceLifecycleMeta.Transitions.End.class,
                    value = { InternetServiceLifecycleMeta.States.Ended.class })
            @InboundWhile(on = { CustomerLifecycleMeta.States.Active.class },
                    relation = InternetServiceLifecycleMeta.Relations.CustomerRelation.class)
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
    @StateMachine
    protected static interface ServiceProviderLifecycle {

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
    protected static interface InternetTVServiceLifecycle extends InternetServiceLifecycleMeta {

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
    protected static interface InternetTVProviderLifecycle extends ServiceProviderLifecycle {}
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
    @StateMachine
    protected static interface VOIPServiceLifecycleMeta extends InternetServiceLifecycleMeta {

        @StateSet
        static interface States extends InternetServiceLifecycleMeta.States {

            @Initial
            @Overrides
            @ValidWhile(relation = VOIPServiceLifecycleMeta.Relations.VoipProvider.class,
                    on = VOIPProviderLifecycleMeta.States.ServiceAvailable.class)
            @Function(transition = VOIPServiceLifecycleMeta.Transitions.Start.class,
                    value = { VOIPServiceLifecycleMeta.States.InService.class })
            static interface New extends InternetServiceLifecycleMeta.States.New {}
        }
        @RelationSet
        static interface Relations extends InternetServiceLifecycleMeta.Relations {

            @RelateTo(VOIPProviderLifecycleMeta.class)
            static interface VoipProvider {}
        }
    }
    @StateMachine
    protected static interface VOIPProviderLifecycleMeta extends ServiceProviderLifecycle {}
    @LifecycleMeta(VOIPServiceLifecycleMeta.class)
    public static class VOIPService extends BaseService<VOIPProvider> {

        public VOIPService(Customer customer) {
            super(customer);
        }

        @Relation(VOIPServiceLifecycleMeta.Relations.VoipProvider.class)
        public VOIPProvider getProvider() {
            return super.getProvider();
        }
    }
    @LifecycleMeta(VOIPProviderLifecycleMeta.class)
    public static class VOIPProvider extends BaseServiceProvider {}
    @LifecycleMeta(InternetServiceLifecycleMeta.class)
    public class InternetServiceOrder {

        private Date startDate;
        private Date endDate;
        @Relation(InternetServiceLifecycleMeta.Relations.CustomerRelation.class)
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

    public CoreFuntionTestMetadata() {
        super();
    }
}