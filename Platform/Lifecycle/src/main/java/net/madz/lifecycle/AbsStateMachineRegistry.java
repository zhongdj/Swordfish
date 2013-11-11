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
import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.lifecycle.meta.instance.StateMachineObject;
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
    protected final HashMap<Object, StateMachineObject> instanceMap = new HashMap<>();
    private final LifecycleRegistry lifecycleRegistry;
    private final StateMachineBuilder builderMeta;

    protected AbsStateMachineRegistry() throws VerificationException {
        lifecycleRegistry = getClass().getAnnotation(LifecycleRegistry.class);
        builderMeta = getClass().getAnnotation(StateMachineBuilder.class);
        registerStateMachines();
    }

    /**
     * To process all the registered class to build the corresponding state
     * machines.
     */
    private synchronized void registerStateMachines() throws VerificationException {
        if ( null == lifecycleRegistry || null == builderMeta ) {
            throw new NullPointerException(
                    "A subclass of AbstractStateMachineRegistry must have both @LifecycleRegistry and @StateMachineMetadataBuilder annotated on Type.");
        }
        final Class<?>[] toRegister = lifecycleRegistry.value();
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        for ( Class<?> clazz : toRegister )
            registerLifecycleMeta(failureSet, clazz);
        if ( failureSet.size() > 0 ) {
            failureSet.dump(new Dumper(System.out));
            throw new VerificationException(failureSet);
        }
    }

    public void registerLifecycleMeta(final Class<?> clazz) throws VerificationException {
        System.out.println("registering .. " + clazz);
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        registerLifecycleMeta(failureSet, clazz);
        if ( failureSet.size() > 0 ) {
            failureSet.dump(new Dumper(System.out));
            throw new VerificationException(failureSet);
        }
    }

    private void registerLifecycleMeta(final VerificationFailureSet failureSet, Class<?> clazz)
            throws VerificationException {
        {
            if ( null != clazz.getAnnotation(StateMachine.class) ) {
                if ( isRegistered(clazz) ) {
                    return;
                }
                createStateMachineMetaBuilder(clazz, null, failureSet);
            } else if ( null != clazz.getAnnotation(LifecycleMeta.class) ) {
                final Class<?> stateMachineClass = clazz.getAnnotation(LifecycleMeta.class).value();
                final StateMachineMetadata metaData;
                if ( !isRegistered(stateMachineClass) ) {
                    metaData = createStateMachineMetaBuilder(stateMachineClass, null, failureSet);
                } else {
                    metaData = loadStateMachineMetadata(stateMachineClass);
                }
                if (null == metaData) {
                    //Failed to create State machine Meta Builder will return null.
                    return;
                }
                if ( null == getStateMachineInst(clazz) ) {
                    StateMachineObject stateMachineInstance = metaData.newInstance(clazz);
                    stateMachineInstance.verifyMetaData(failureSet);
                    addInstance(clazz, stateMachineInstance);
                }
            } else {
                final String errorMessage = BundleUtils.getBundledMessage(getClass(), "syntax_error",
                        SyntaxErrors.REGISTERED_META_ERROR, clazz);
                failureSet.add(new VerificationFailure(this, getClass().getName(), SyntaxErrors.REGISTERED_META_ERROR,
                        errorMessage));
            }
        }
    }

    private boolean isRegistered(Class<?> clazz) {
        return null != this.typeMap.get(clazz.getName());
    }

    public synchronized void addInstance(Class<?> clazz, StateMachineObject stateMachine) {
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

    private StateMachineMetaBuilder createBuilder(Class<?> metadataClass) throws VerificationException {
        try {
            final Constructor<? extends StateMachineMetaBuilder> c = builderMeta.value().getConstructor(
                    AbsStateMachineRegistry.class, String.class);
            return c.newInstance(this, metadataClass.getName());
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public synchronized Map<Object, StateMachineMetadata> getStateMachineTypes() {
        return Collections.unmodifiableMap(this.typeMap);
    }

    public synchronized Map<Object, StateMachineObject> getStateMachineInstances() {
        return Collections.unmodifiableMap(this.instanceMap);
    }

    public synchronized StateMachineMetadata getStateMachineMeta(Object key) {
        return this.typeMap.get(key);
    }

    public synchronized StateMachineObject getStateMachineInst(Object key) {
        return this.instanceMap.get(key);
    }

    private StateMachineMetadata createStateMachineMetaBuilder(Class<?> stateMachineClass,
            StateMachineMetaBuilder owningStateMachine, VerificationFailureSet failureSet) throws VerificationException {
        StateMachineMetaBuilder metaBuilder = null;
        try {
            if ( null != stateMachineClass.getAnnotation(CompositeStateMachine.class) ) {
                metaBuilder = createCompositeBuilder(stateMachineClass, owningStateMachine);
            } else {
                metaBuilder = createBuilder(stateMachineClass);
            }
            final StateMachineMetadata metaData = metaBuilder.build(stateMachineClass, null).getMetaData();
            addTemplate(metaData);
            if ( null != failureSet ) {
                metaData.verifyMetaData(failureSet);
            } else {
                VerificationFailureSet tmpSet = new VerificationFailureSet();
                metaData.verifyMetaData(tmpSet);
                if ( 0 < tmpSet.size() ) {
                    throw new VerificationException(tmpSet);
                }
            }
            return metaData;
        } catch (VerificationException ex) {
            if ( null == failureSet ) {
                throw ex;
            } else {
                failureSet.add(ex);
            }
        }
        return null;
    }

    public StateMachineMetadata loadStateMachineMetadata(Class<?> stateMachineClass) throws VerificationException {
        return loadStateMachineMetadata(stateMachineClass, null);
    }

    public StateMachineMetadata loadStateMachineMetadata(Class<?> stateMachineClass,
            StateMachineMetaBuilder owningStateMachine) throws VerificationException {
        StateMachineMetadata stateMachineMeta = getStateMachineMeta(stateMachineClass);
        if ( null != stateMachineMeta ) return stateMachineMeta;
        return createStateMachineMetaBuilder(stateMachineClass, owningStateMachine, null);
    }

    private StateMachineMetaBuilder createCompositeBuilder(Class<?> stateMachineClass,
            StateMachineMetaBuilder owningStateMachine) throws VerificationException {
        Constructor<? extends StateMachineMetaBuilder> c;
        try {
            c = builderMeta.value().getConstructor(builderMeta.value(), String.class);
            final StateMachineMetaBuilder compositeStateMachine = c.newInstance(owningStateMachine,
                    "CompositeStateMachine." + stateMachineClass.getSimpleName());
            return compositeStateMachine;
        } catch (Throwable t) {
            if ( t.getCause() instanceof VerificationException ) {
                throw (VerificationException) t.getCause();
            }
            throw new IllegalStateException(t);
        }
    }
}
