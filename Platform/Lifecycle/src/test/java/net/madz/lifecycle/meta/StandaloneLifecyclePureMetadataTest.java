package net.madz.lifecycle.meta;


import java.lang.reflect.Method;
import java.util.Map;

import net.madz.common.DottedPath;
import net.madz.lifecycle.AbstractStateMachineRegistry;
import net.madz.lifecycle.AbstractStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbstractStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.demo.standalone.IServiceOrder;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Finish;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Schedule;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Start;
import net.madz.lifecycle.meta.impl.builder.AnnotationBasedStateMachineMetaBuilder;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata.TransitionTypeEnum;

import org.junit.Test;
import static junit.framework.Assert.*;

public class StandaloneLifecyclePureMetadataTest {

    @LifecycleRegistry(IServiceOrder.class)
    @StateMachineMetadataBuilder(AnnotationBasedStateMachineMetaBuilder.class)
    private static class StateMachineRegistry extends AbstractStateMachineRegistry {}

    @Test
    public void testTransitions() throws NoSuchMethodException, SecurityException {
        final StateMachineRegistry registry = new StateMachineRegistry();
        final Map<Object, StateMachineMetadata> types = registry.getStateMachineTypes();
        final Map<Object, StateMachineInst> instances = registry.getStateMachineInstances();
        // Check Sizes
        assertTrue(types.size() > 0);
        assertTrue(instances.size() > 0);
        // Check Keys
        assertTrue(types.containsKey(ServiceableLifecycleMeta.class));
        assertTrue(types.containsKey(ServiceableLifecycleMeta.class.getName()));
        assertTrue(instances.containsKey(IServiceOrder.class));
        assertTrue(instances.containsKey(IServiceOrder.class.getName()));
        // Check 4 transition instances
        final StateMachineInst stateMachineInst = instances.get(IServiceOrder.class);
        final TransitionInst[] transitionSet = stateMachineInst.getTransitionSet();
        assertEquals(4, transitionSet.length);
        validateTranitionMethod(IServiceOrder.class, stateMachineInst, Schedule.class, "allocateResources");
        validateTranitionMethod(IServiceOrder.class, stateMachineInst, Start.class, "confirmStart");
        validateTranitionMethod(IServiceOrder.class, stateMachineInst, Finish.class, "confirmFinish");
        // Check StateMachineMetadata
        final StateMachineMetadata machineMetadata = types.get(ServiceableLifecycleMeta.class);
        assertEquals(machineMetadata, stateMachineInst.getTemplate());
        // Check 4 transition metadata
        final TransitionMetadata scheduleTransition = machineMetadata.getTransition(Schedule.class);
        assertEquals(machineMetadata, scheduleTransition.getStateMachine());
        assertEquals(0, scheduleTransition.getTimeout());
        assertEquals(machineMetadata, scheduleTransition.getParent());
        assertEquals(TransitionTypeEnum.Common, scheduleTransition.getType());
        assertEquals(
                DottedPath.parse("net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.TransitionSet.Schedule"),
                scheduleTransition.getDottedPath());
        {
            final TransitionMetadata startTransition = machineMetadata.getTransition(Start.class);
            assertEquals(machineMetadata, startTransition.getStateMachine());
            final TransitionMetadata finishTransition = machineMetadata.getTransition(Finish.class);
            assertEquals(machineMetadata, finishTransition.getStateMachine());
        }
    }

    @Test
    public void testStates() throws Exception {
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
