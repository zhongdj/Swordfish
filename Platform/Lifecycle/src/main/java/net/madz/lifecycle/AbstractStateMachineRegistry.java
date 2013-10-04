package net.madz.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;

public abstract class AbstractStateMachineRegistry {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface LifecycleRegistry {

        /**
         * @return classes can be the name of life cycle interface itself, which
         *         has a @StateMachine annotated on it's type.
         *         or the name of a class/interface that has a @LifecycleMeta
         *         annotated on the type.
         */
        Class<?>[] value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface StateMachineMetadataBuilder {

        /**
         * @return a concrete StateMachineMetaBuilder implementation class,
         *         which can build state machines from value of @LifecycleRegisty
         */
        Class<? extends StateMachineMetaBuilder> value();
    }

    protected static final Logger logger = Logger.getLogger(AbstractStateMachineRegistry.class.getName());

    /**
     * The key might be class object as:
     * The life cycle interface itself that has a @StateMachine,
     * or a class/interface that has a @LifecycleMeta
     * The key might be String as:
     * The full qualified name corresponds to the dotted path,
     * or simple name, or class full name
     */
    protected final HashMap<Object, StateMachineMetadata> typeMap = new HashMap<>();

    protected final HashMap<Object, StateMachineInst> instanceMap = new HashMap<>();

    protected AbstractStateMachineRegistry() {
        registerStateMachines();
    }

    /**
     * To process all the registered class to build the corresponding state
     * machines.
     */
    private void registerStateMachines() {
        final LifecycleRegistry lifecycleRegistry = getClass().getAnnotation(LifecycleRegistry.class);
        final StateMachineMetadataBuilder builderMeta = getClass().getAnnotation(StateMachineMetadataBuilder.class);
        if ( null == lifecycleRegistry || null == builderMeta ) {
            throw new NullPointerException(
                    "A subclass of AbstractStateMachineRegistry must have both @LifecycleRegistry and @StateMachineMetadataBuilder annotated on Type.");
        }
        final Class<?>[] toRegister = lifecycleRegistry.value();
        final StateMachineMetaBuilder builder = createBuilder(builderMeta);
        for ( Class<?> clazz : toRegister ) {
            if ( null != clazz.getAnnotation(StateMachine.class) ) {
                final StateMachineMetadata metaData = builder.build(clazz).getMetaData();
                addTemplate(metaData);
            } else if ( null != clazz.getAnnotation(LifecycleMeta.class) ) {
                final StateMachineMetadata metaData = builder.build(clazz).getMetaData();
                addTemplate(metaData);
                addInstance(clazz, metaData.newInstance(clazz));
            } else {
                logger.warning("Class "
                        + clazz.getName()
                        + " is not a valid class to process, since there is no @StateMachine or @LifecycleMeta defined on it.");
            }
        }
    }

    private void addInstance(Class<?> clazz, StateMachineInst stateMachine) {
        instanceMap.put(clazz, stateMachine);
        instanceMap.put(clazz.getName(), stateMachine);
    }

    private void addTemplate(final StateMachineMetadata metaData) {
        for ( Object key : metaData.getKeySet() ) {
            StateMachineMetadata existedStateMachine = typeMap.get(key);
            if ( typeMap.containsKey(key) && null == existedStateMachine ) {
                typeMap.remove(key);
            }
            if ( typeMap.containsKey(key) && existedStateMachine.getDottedPath().equals(metaData.getDottedPath()) ) {
                throw new IllegalStateException("Same Key corresponds two different StateMachine: " + key.toString()
                        + ", one is : " + existedStateMachine.getDottedPath() + " and another is:"
                        + metaData.getDottedPath());
            }
            typeMap.put(key, metaData);
        }
    }

    private StateMachineMetaBuilder createBuilder(final StateMachineMetadataBuilder builderMeta) {
        final Class<? extends StateMachineMetaBuilder> builderClass = builderMeta.value();
        final StateMachineMetaBuilder builder;
        try {
            builder = builderClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return builder;
    }

    public Map<Object, StateMachineMetadata> getStateMachineTypes() {
        return Collections.unmodifiableMap(this.typeMap);
    }

    public Map<Object, StateMachineInst> getStateMachineInstances() {
        return Collections.unmodifiableMap(this.instanceMap);
    }
}
