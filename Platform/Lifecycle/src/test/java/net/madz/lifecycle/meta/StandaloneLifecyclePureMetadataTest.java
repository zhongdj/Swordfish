package net.madz.lifecycle.meta;

import java.lang.reflect.Method;
import java.util.Map;

import net.madz.common.DottedPath;
import net.madz.lifecycle.AbstractStateMachineRegistry;
import net.madz.lifecycle.AbstractStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbstractStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.demo.standalone.IServiceOrder;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.States.Cancelled;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.States.Created;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.States.Finished;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.States.Ongoing;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.States.Queued;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Cancel;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Finish;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Schedule;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Start;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.lifecycle.meta.instance.StateInst;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata.TransitionTypeEnum;
import net.madz.verification.VerificationException;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StandaloneLifecyclePureMetadataTest {

    private static StateMachineMetadata machineMetadata;

    private static StateMachineInst stateMachineInst;

    private static StateMachineRegistry registry;

    @LifecycleRegistry(IServiceOrder.class)
    @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
    private static class StateMachineRegistry extends AbstractStateMachineRegistry {

        protected StateMachineRegistry() throws VerificationException {
            super();
        }}

    @BeforeClass
    public static void setup() throws VerificationException {
        registry = new StateMachineRegistry();
        machineMetadata = registry.getStateMachineMeta(ServiceableLifecycleMeta.class);
        stateMachineInst = registry.getStateMachineInst(IServiceOrder.class);
    }

    // /////////////////////////////////////////////////////////////////
    // Summary Part
    // ////////////////////////////////////////////////////////////////
    @Test
    public void testRegistry() {
        final Map<Object, StateMachineMetadata> types = registry.getStateMachineTypes();
        final Map<Object, StateMachineInst> instances = registry.getStateMachineInstances();
        // Check Sizes
        {
            assertTrue(types.size() > 0);
            assertTrue(instances.size() > 0);
        }
        // Check Keys
        {
            assertNotNull(types.get(ServiceableLifecycleMeta.class));
            assertNotNull(types.get(ServiceableLifecycleMeta.class.getName()));
            assertNotNull(instances.get(IServiceOrder.class));
            assertNotNull(instances.get(IServiceOrder.class.getName()));
        }
    }

    // //////////////////////////////////////////////////////////////////
    // TransitionMetadata Part
    // //////////////////////////////////////////////////////////////////
    @Test
    public void testTransitionMetadatas() throws SecurityException {
        StateMachineMetadata m = machineMetadata;
        assertEquals(m, stateMachineInst.getTemplate());
        {
            // Class Key Validation
            assertNotNull(m.getTransition(Schedule.class));
            assertNotNull(m.getTransition(Start.class));
            assertNotNull(m.getTransition(Finish.class));
            assertNotNull(m.getTransition(Cancel.class));
            // Class Name Key Validation
            assertNotNull(m.getTransition(Schedule.class.getName()));
            assertNotNull(m.getTransition(Start.class.getName()));
            assertNotNull(m.getTransition(Finish.class.getName()));
            assertNotNull(m.getTransition(Cancel.class.getName()));
            // Dotted Path Key Validation
            assertNotNull(m.getTransition(ServiceableLifecycleMeta.class.getName() + ".TransitionSet."
                    + Schedule.class.getSimpleName()));
            assertNotNull(m.getTransition(ServiceableLifecycleMeta.class.getName() + ".TransitionSet."
                    + Start.class.getSimpleName()));
            assertNotNull(m.getTransition(ServiceableLifecycleMeta.class.getName() + ".TransitionSet."
                    + Finish.class.getSimpleName()));
            assertNotNull(m.getTransition(ServiceableLifecycleMeta.class.getName() + ".TransitionSet."
                    + Cancel.class.getSimpleName()));
        }
        // Check 4 transition metadata
        {
            final TransitionMetadata scheduleTransition = m.getTransition(Schedule.class);
            assertEquals(m, scheduleTransition.getStateMachine());
            assertEquals(0, scheduleTransition.getTimeout());
            assertEquals(m, scheduleTransition.getParent());
            assertEquals(TransitionTypeEnum.Common, scheduleTransition.getType());
            assertEquals(
                    DottedPath
                            .parse("net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.TransitionSet.Schedule"),
                    scheduleTransition.getDottedPath());
        }
        {
            final TransitionMetadata startTransition = m.getTransition(Start.class);
            assertEquals(m, startTransition.getStateMachine());
            assertEquals(0, startTransition.getTimeout());
            assertEquals(m, startTransition.getParent());
            assertEquals(TransitionTypeEnum.Common, startTransition.getType());
            assertEquals(
                    DottedPath.parse("net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.TransitionSet.Start"),
                    startTransition.getDottedPath());
        }
        {
            final TransitionMetadata finishTransition = m.getTransition(Finish.class);
            assertEquals(m, finishTransition.getStateMachine());
            assertEquals(0, finishTransition.getTimeout());
            assertEquals(m, finishTransition.getParent());
            assertEquals(TransitionTypeEnum.Common, finishTransition.getType());
            assertEquals(
                    DottedPath
                            .parse("net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.TransitionSet.Finish"),
                    finishTransition.getDottedPath());
        }
        {
            final TransitionMetadata cancelTransition = m.getTransition(Cancel.class);
            assertEquals(m, cancelTransition.getStateMachine());
            assertEquals(0, cancelTransition.getTimeout());
            assertEquals(m, cancelTransition.getParent());
            assertEquals(TransitionTypeEnum.Common, cancelTransition.getType());
            assertEquals(
                    DottedPath
                            .parse("net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.TransitionSet.Cancel"),
                    cancelTransition.getDottedPath());
        }
    }

    // //////////////////////////////////////////////////////////////////
    // TransitionInstance Part
    // //////////////////////////////////////////////////////////////////
    @Test
    public void testTransitionInstances() throws NoSuchMethodException {
        // Check 3 transition instances
        final TransitionInst[] transitionSet = stateMachineInst.getTransitionSet();
        assertEquals(3, transitionSet.length);
        validateTranitionMethod(IServiceOrder.class, stateMachineInst, Schedule.class, "allocateResources");
        validateTranitionMethod(IServiceOrder.class, stateMachineInst, Start.class, "confirmStart");
        validateTranitionMethod(IServiceOrder.class, stateMachineInst, Finish.class, "confirmFinish");
    }

    @Test
    public void testStateMetadataSummary() throws Exception {
        final StateMachineMetadata m = machineMetadata;
        assertNotNull(m.getState(Created.class));
        assertNotNull(m.getState(Queued.class));
        assertNotNull(m.getState(Ongoing.class));
        assertNotNull(m.getState(Finished.class));
        assertNotNull(m.getState(Cancelled.class));
        assertNotNull(m.getState(Created.class.getName()));
        assertNotNull(m.getState(Queued.class.getName()));
        assertNotNull(m.getState(Ongoing.class.getName()));
        assertNotNull(m.getState(Finished.class.getName()));
        assertNotNull(m.getState(Cancelled.class.getName()));
        assertNotNull(m.getState(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                + Created.class.getSimpleName()));
        assertNotNull(m
                .getState(ServiceableLifecycleMeta.class.getName() + ".StateSet." + Queued.class.getSimpleName()));
        assertNotNull(m.getState(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                + Ongoing.class.getSimpleName()));
        assertNotNull(m.getState(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                + Finished.class.getSimpleName()));
        assertNotNull(m.getState(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                + Cancelled.class.getSimpleName()));
        assertEquals(5, m.getStateSet().length);
        assertEquals(m.getState(Created.class), m.getInitialState());
        assertEquals(2, m.getFinalStates().length);
        assertTrue(m.getFinalStates()[0] != m.getFinalStates()[1]);
        assertTrue(m.getFinalStates()[0] == m.getState(Finished.class)
                || m.getFinalStates()[0] == m.getState(Cancelled.class));
        assertTrue(m.getFinalStates()[1] == m.getState(Finished.class)
                || m.getFinalStates()[1] == m.getState(Cancelled.class));
    }

    // //////////////////////////////////////////////////////////////////
    // StateMetadata Part
    // //////////////////////////////////////////////////////////////////
    @Test
    public void testStateMetadatas() {
        final StateMachineMetadata m = machineMetadata;
        {
            final StateMetadata created = m.getState(Created.class);
            // Transition Validation
            assertNotNull(created.getTransition(Schedule.class));
            assertNull(created.getTransition(Start.class));
            assertNull(created.getTransition(Finish.class));
            assertNull(created.getTransition(Cancel.class));
            // Basic State Validation
            assertTrue(created.isInitial());
            assertFalse(created.isCompositeState());
            assertFalse(created.isFinal());
            assertEquals(
                    DottedPath.parse(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                            + Created.class.getSimpleName()), created.getDottedPath());
            simpleStateValidation(m, created);
        }
        {
            final StateMetadata queued = m.getState(Queued.class);
            // Transition Validation
            assertNull(queued.getTransition(Schedule.class));
            assertNotNull(queued.getTransition(Start.class));
            assertNull(queued.getTransition(Finish.class));
            assertNotNull(queued.getTransition(Cancel.class));
            // Basic State Validation
            assertFalse(queued.isInitial());
            assertFalse(queued.isCompositeState());
            assertFalse(queued.isFinal());
            assertEquals(
                    DottedPath.parse(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                            + Queued.class.getSimpleName()), queued.getDottedPath());
            simpleStateValidation(m, queued);
        }
        {
            final StateMetadata ongoing = m.getState(Ongoing.class);
            // Transition Validation
            assertNull(ongoing.getTransition(Schedule.class));
            assertNull(ongoing.getTransition(Start.class));
            assertNotNull(ongoing.getTransition(Finish.class));
            assertNotNull(ongoing.getTransition(Cancel.class));
            // Basic State Validation
            assertFalse(ongoing.isInitial());
            assertFalse(ongoing.isCompositeState());
            assertFalse(ongoing.isFinal());
            assertEquals(
                    DottedPath.parse(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                            + Ongoing.class.getSimpleName()), ongoing.getDottedPath());
            simpleStateValidation(m, ongoing);
        }
        {
            final StateMetadata finished = m.getState(Finished.class);
            // Transition Validation
            assertNull(finished.getTransition(Schedule.class));
            assertNull(finished.getTransition(Start.class));
            assertNull(finished.getTransition(Finish.class));
            assertNull(finished.getTransition(Cancel.class));
            // Basic State Validation
            assertFalse(finished.isInitial());
            assertFalse(finished.isCompositeState());
            assertTrue(finished.isFinal());
            assertEquals(
                    DottedPath.parse(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                            + Finished.class.getSimpleName()), finished.getDottedPath());
            simpleStateValidation(m, finished);
        }
        {
            final StateMetadata cancelled = m.getState(Cancelled.class);
            // Transition Validation
            assertNull(cancelled.getTransition(Schedule.class));
            assertNull(cancelled.getTransition(Start.class));
            assertNull(cancelled.getTransition(Finish.class));
            assertNull(cancelled.getTransition(Cancel.class));
            // Basic State Validation
            assertFalse(cancelled.isInitial());
            assertFalse(cancelled.isCompositeState());
            assertTrue(cancelled.isFinal());
            assertEquals(
                    DottedPath.parse(ServiceableLifecycleMeta.class.getName() + ".StateSet."
                            + Cancelled.class.getSimpleName()), cancelled.getDottedPath());
            simpleStateValidation(m, cancelled);
        }
    }

    private void simpleStateValidation(final StateMachineMetadata m, final StateMetadata created) {
        // Simple State Validation
        assertNull(created.getCompositeStateMachine());
        assertNull(created.getCorruptTransition());
        assertEquals(0, created.getInboundWhiles().length);
        assertNull(created.getLinkTo());
        assertNull(created.getOwningState());
        assertEquals(m, created.getParent());
        assertNull(created.getRecoverTransition());
        assertNull(created.getRedoTransition());
        assertNull(created.getSuperStateMetadata());
        assertEquals(0, created.getValidWhiles().length);
        assertFalse(created.hasCorruptTransition());
        assertFalse(created.hasInboundWhiles());
        assertFalse(created.hasRecoverTransition());
        assertFalse(created.hasRedoTransition());
        assertFalse(created.hasValidWhiles());
    }

    // //////////////////////////////////////////////////////////////////
    // StateInstance Part
    // //////////////////////////////////////////////////////////////////
    @Test
    public void testStateInstancesSummary() throws Exception {
        final StateMachineInst i = stateMachineInst;
        final StateInst[] stateSet = i.getStateSet();
        assertEquals(5, stateSet.length);
        // Class Key Valdiation
        assertNotNull(i.getState(Created.class));
        assertNotNull(i.getState(Queued.class));
        assertNotNull(i.getState(Ongoing.class));
        assertNotNull(i.getState(Finished.class));
        assertNotNull(i.getState(Cancelled.class));
        // Class Name Validation
        assertNotNull(i.getState(Created.class.getName()));
        assertNotNull(i.getState(Queued.class.getName()));
        assertNotNull(i.getState(Ongoing.class.getName()));
        assertNotNull(i.getState(Finished.class.getName()));
        assertNotNull(i.getState(Cancelled.class.getName()));
        // Dotted Path Validation
        assertNotNull(i.getState(IServiceOrder.class.getName() + ".StateSet." + Created.class.getSimpleName()));
        assertNotNull(i.getState(IServiceOrder.class.getName() + ".StateSet." + Queued.class.getSimpleName()));
        assertNotNull(i.getState(IServiceOrder.class.getName() + ".StateSet." + Ongoing.class.getSimpleName()));
        assertNotNull(i.getState(IServiceOrder.class.getName() + ".StateSet." + Finished.class.getSimpleName()));
        assertNotNull(i.getState(IServiceOrder.class.getName() + ".StateSet." + Cancelled.class.getSimpleName()));
        final StateMachineMetadata m = machineMetadata;
        assertEquals(m.getState(Created.class), i.getState(Created.class).getTemplate());
        assertEquals(m.getState(Queued.class), i.getState(Queued.class).getTemplate());
        assertEquals(m.getState(Ongoing.class), i.getState(Ongoing.class).getTemplate());
        assertEquals(m.getState(Finished.class), i.getState(Finished.class).getTemplate());
        assertEquals(m.getState(Cancelled.class), i.getState(Cancelled.class).getTemplate());
    }

    @Test
    public void testStateMachineInst() throws Throwable {
        final StateMachineInst i = stateMachineInst;
        final Method getter = i.stateGetter();
        final Method setter = i.stateSetter();
        final Method methodG = IServiceOrder.class.getMethod("getServiceOrderState");
        assertEquals(methodG, getter);
        assertNull(setter);
    }

    private void validateTranitionMethod(Class<IServiceOrder> lifecycleContainerClass,
            final StateMachineInst stateMachineInst, final Class<?> transitionContainerClass,
            final String expectMethodName) throws NoSuchMethodException {
        assertTrue(stateMachineInst.hasTransition(transitionContainerClass));
        final TransitionInst transition = stateMachineInst.getTransition(transitionContainerClass);
        final Method actualTransitionMethod = transition.getTransitionMethod();
        final Method expectedTransitionMethod = lifecycleContainerClass.getMethod(expectMethodName);
        assertEquals(expectedTransitionMethod, actualTransitionMethod);
    }
}
