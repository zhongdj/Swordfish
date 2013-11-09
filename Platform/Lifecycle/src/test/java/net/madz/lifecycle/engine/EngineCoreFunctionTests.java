package net.madz.lifecycle.engine;

import net.madz.bcel.intercept.DefaultStateMachineRegistry;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Active;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Canceled;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Draft;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.States.Suspended;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Activate;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Cancel;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Resume;
import net.madz.lifecycle.engine.EngineCoreFunctionTests.CustomerLifecycleMeta.Transitions.Suspend;
import net.madz.verification.VerificationException;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
