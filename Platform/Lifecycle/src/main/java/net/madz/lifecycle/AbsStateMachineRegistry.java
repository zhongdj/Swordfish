package net.madz.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.madz.common.Dumper;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;
import net.madz.verification.VerificationFailureSet;

public abstract class AbsStateMachineRegistry {

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
    public static @interface StateMachineBuilder {

        /**
         * @return a concrete StateMachineMetaBuilder implementation class,
         *         which can build state machines from value of @LifecycleRegisty
         */
        Class<? extends StateMachineMetaBuilder> value() default StateMachineMetaBuilderImpl.class;
    }

    protected static final Logger logger = Logger.getLogger(AbsStateMachineRegistry.class.getName());
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

    protected AbsStateMachineRegistry() throws VerificationException {
        registerStateMachines();
    }

    /**
     * To process all the registered class to build the corresponding state
     * machines.
     */
    private synchronized void registerStateMachines() throws VerificationException {
        final LifecycleRegistry lifecycleRegistry = getClass().getAnnotation(LifecycleRegistry.class);
        final StateMachineBuilder builderMeta = getClass().getAnnotation(StateMachineBuilder.class);
        if ( null == lifecycleRegistry || null == builderMeta ) {
            throw new NullPointerException(
                    "A subclass of AbstractStateMachineRegistry must have both @LifecycleRegistry and @StateMachineMetadataBuilder annotated on Type.");
        }
        final Class<?>[] toRegister = lifecycleRegistry.value();
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        for ( Class<?> clazz : toRegister ) {
            if ( null != clazz.getAnnotation(StateMachine.class) ) {
                if ( isRegistered(clazz) ) {
                    return;
                }
                buildStateMachineMetadata(builderMeta, failureSet, clazz);
            } else if ( null != clazz.getAnnotation(LifecycleMeta.class) ) {
                final Class<?> stateMachineClass = clazz.getAnnotation(LifecycleMeta.class).value();
                if ( isRegistered(stateMachineClass) ) {
                    return;
                }
                final StateMachineMetadata metaData = buildStateMachineMetadata(builderMeta, failureSet,
                        stateMachineClass);
                StateMachineInst stateMachineInstance = metaData.newInstance(clazz);
                stateMachineInstance.verifyMetaData(failureSet);
                addInstance(clazz, stateMachineInstance);
            } else {
                final String errorMessage = BundleUtils.getBundledMessage(getClass(), "syntax_error",
                        Errors.REGISTERED_META_ERROR, new String[] { clazz.getName() });
                failureSet.add(new VerificationFailure(this, getClass().getName(), Errors.REGISTERED_META_ERROR,
                        errorMessage));
            }
        }
        if ( failureSet.size() > 0 ) {
            failureSet.dump(new Dumper(System.out));
            throw new VerificationException(failureSet);
        }
    }

    private StateMachineMetadata buildStateMachineMetadata(final StateMachineBuilder builderMeta,
            final VerificationFailureSet failureSet, final Class<?> stateMachineClass) throws VerificationException {
        final StateMachineMetaBuilder builder = createBuilder(builderMeta, stateMachineClass.getName());
        builder.setRegistry(this);
        final StateMachineMetadata metaData = builder.build(stateMachineClass, null).getMetaData();
        metaData.verifyMetaData(failureSet);
        addTemplate(metaData);
        return metaData;
    }

    private boolean isRegistered(Class<?> clazz) {
        return null != this.typeMap.get(clazz.getName());
    }

    public synchronized void addInstance(Class<?> clazz, StateMachineInst stateMachine) {
        instanceMap.put(clazz, stateMachine);
        instanceMap.put(clazz.getName(), stateMachine);
    }

    public synchronized void addTemplate(final StateMachineMetadata metaData) {
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

    private StateMachineMetaBuilder createBuilder(final StateMachineBuilder builderMeta, String metadataClass)
            throws VerificationException {
        final Class<? extends StateMachineMetaBuilder> builderClass = builderMeta.value();
        try {
            Constructor<? extends StateMachineMetaBuilder> c = builderClass.getConstructor(
                    AbsStateMachineRegistry.class, String.class);
            return c.newInstance(this, metadataClass);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public synchronized Map<Object, StateMachineMetadata> getStateMachineTypes() {
        return Collections.unmodifiableMap(this.typeMap);
    }

    public synchronized Map<Object, StateMachineInst> getStateMachineInstances() {
        return Collections.unmodifiableMap(this.instanceMap);
    }

    public synchronized StateMachineMetadata getStateMachineMeta(Object key) {
        return this.typeMap.get(key);
    }

    public synchronized StateMachineInst getStateMachineInst(Object key) {
        return this.instanceMap.get(key);
    }
}
