package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.madz.common.Dumper;
import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.builder.TransitionMetaBuilder;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineMetaBuilderImpl extends AnnotationBasedMetaBuilder<StateMachineMetadata, StateMachineMetadata>
        implements StateMachineMetaBuilder {

    private StateMachineMetadata superStateMachineMetadata;
    private StateMachineMetadata parentStateMachineMetadata;
    private final ArrayList<TransitionMetaBuilder> transitionList = new ArrayList<>();
    private final HashMap<Object, TransitionMetaBuilder> transitionMap = new HashMap<>();
    private final ArrayList<ConditionMetaBuilder> conditionList = new ArrayList<>();
    private final HashMap<Object, ConditionMetaBuilder> conditionMap = new HashMap<>();
    private final ArrayList<StateMetaBuilder> stateList = new ArrayList<>();
    private final HashMap<Object, StateMetaBuilder> stateMap = new HashMap<>();
    private ArrayList<StateMetaBuilder> finalStateList = new ArrayList<>();
    private TransitionMetaBuilder corruptTransition;
    private TransitionMetaBuilder recoverTransition;
    private TransitionMetaBuilder redoTransition;
    private TransitionMetaBuilder failTransition;
    private StateMetaBuilder initialState;
    /* //////////////////////////////////////////////////// */
    /* //////// Fields For Composite State Machine /////// */
    /* //////////////////////////////////////////////////// */
    // If this state machine is a composite state machine, this.composite = true
    private boolean composite;
    // If this state machine is a composite state machine, owningState is the
    // enclosing state
    private StateMetaBuilder owningState;
    // If this state machine is a composite state machine, owningStateMachine is
    // the enclosing state's (parent) StateMachine
    private StateMachineMetaBuilder owningStateMachine;
    // Also for composite State Machine
    private ArrayList<StateMetaBuilder> shortcutStateList = new ArrayList<>();

    public StateMachineMetaBuilderImpl(AbsStateMachineRegistry registry, String name) {
        this(name);
        this.registry = registry;
    }

    public StateMachineMetaBuilderImpl(String name) {
        super(null, name);
    }

    @Override
    public boolean hasSuper() {
        return null != this.superStateMachineMetadata;
    }

    @Override
    public StateMachineMetadata getSuperStateMachine() {
        return superStateMachineMetadata;
    }

    @Override
    public boolean hasParent() {
        return null != parentStateMachineMetadata;
    }

    @Override
    public boolean hasRelations() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMachineMetadata[] getRelatedStateMachineMetadata() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata[] getStateSet() {
        return stateList.toArray(new StateMetadata[stateList.size()]);
    }

    @Override
    public StateMetadata getState(Object stateKey) {
        return stateMap.get(stateKey);
    }

    @Override
    public StateMetadata getInitialState() {
        return initialState;
    }

    @Override
    public StateMetadata[] getFinalStates() {
        return finalStateList.toArray(new StateMetadata[finalStateList.size()]);
    }

    @Override
    public TransitionMetadata[] getTransitionSet() {
        return transitionList.toArray(new TransitionMetadata[transitionList.size()]);
    }

    @Override
    public TransitionMetadata getTransition(Object transitionKey) {
        return this.transitionMap.get(transitionKey);
    }

    @Override
    public TransitionMetadata getStateSynchronizationTransition() {
        return null;
    }

    @Override
    public StateMachineInst newInstance(Class<?> clazz) {
        final StateMachineInstBuilderImpl builder = new StateMachineInstBuilderImpl(this, clazz.getSimpleName());
        builder.setRegistry(registry);
        return builder.build(clazz).getMetaData();
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasRedoTransition() {
        return null != redoTransition;
    }

    @Override
    public TransitionMetadata getRedoTransition() {
        return redoTransition;
    }

    @Override
    public boolean hasRecoverTransition() {
        return null != recoverTransition;
    }

    @Override
    public TransitionMetadata getRecoverTransition() {
        return recoverTransition;
    }

    @Override
    public boolean hasCorruptTransition() {
        return null != corruptTransition;
    }

    @Override
    public TransitionMetadata getCorruptTransition() {
        return corruptTransition;
    }

    /* //////////////////////////////////////////////////// */
    /* //////// Methods For Composite State Machine /////// */
    /* //////////////////////////////////////////////////// */
    @Override
    public boolean isComposite() {
        return composite;
    }

    @Override
    public void setComposite(boolean b) {
        this.composite = b;
    }

    @Override
    public StateMachineMetadata getOwningStateMachine() {
        return owningStateMachine;
    }

    @Override
    public StateMetadata getCompositeState() {
        return owningState;
    }

    @Override
    public void setOwningState(StateMetaBuilder stateMetaBuilder) {
        this.owningState = stateMetaBuilder;
        this.owningStateMachine = stateMetaBuilder.getStateMachine();
    }

    @Override
    public StateMetadata[] getShortcutStateSet() {
        return shortcutStateList.toArray(new StateMetadata[shortcutStateList.size()]);
    }

    /* //////////////////////////////////////////////////// */
    /* //////// Methods For Builder /////// */
    /* //////////////////////////////////////////////////// */
    @Override
    public StateMachineMetaBuilder build(Class<?> clazz) throws VerificationException {
        // Step 1. Syntax Validation
        {
            verifySyntax(clazz);
        }
        // Step 2. Configure StateMachine
        {
            configureSuperStateMachine(clazz);
            configureConditionSet(clazz);
            configureTransitionSet(clazz);
            configureStateSetBasic(clazz);
            configureCompositeStateMachine(clazz);
            configureFunctions(clazz);
            configureRelationSet(clazz);
            configureStateSetRelations(clazz);
        }
        return this;
    }

    private void configureCompositeStateMachine(Class<?> clazz) throws VerificationException {
        final List<Class<?>> stateSetClasses = findClass(clazz.getDeclaredClasses(), StateSet.class);
        if ( 0 >= stateSetClasses.size() ) {
            return;
        }
        final Class<?>[] stateClasses = stateSetClasses.get(0).getDeclaredClasses();
        for ( Class<?> stateClass : stateClasses ) {
            StateMetaBuilder stateMetaBuilder = this.stateMap.get(stateClass);
            stateMetaBuilder.configureCompositeStateMachine(stateClass);
        }
    }

    private void configureSuperStateMachine(Class<?> clazz) throws VerificationException {
        if ( hasSuperMetadataClass(clazz) ) {
            StateMachineMetadata superStateMachineMetadata = load(getSuperMetadataClass(clazz));
            this.superStateMachineMetadata = superStateMachineMetadata;
        }
    }

    private void configureFunctions(Class<?> clazz) throws VerificationException {
        final List<Class<?>> stateSetClasses = findClass(clazz.getDeclaredClasses(), StateSet.class);
        if ( 0 >= stateSetClasses.size() ) {
            return;
        }
        final Class<?>[] stateClasses = stateSetClasses.get(0).getDeclaredClasses();
        for ( Class<?> stateClass : stateClasses ) {
            StateMetaBuilder stateMetaBuilder = this.stateMap.get(stateClass);
            stateMetaBuilder.configureFunctions(stateClass);
        }
    }

    private void configureStateSetRelations(Class<?> clazz) throws VerificationException {
        // TODO Auto-generated method stub
    }

    private void configureRelationSet(Class<?> clazz) throws VerificationException {
        // TODO Auto-generated method stub
    }

    private void configureStateSetBasic(Class<?> clazz) throws VerificationException {
        final List<Class<?>> stateSetClasses = findClass(clazz.getDeclaredClasses(), StateSet.class);
        if ( 0 >= stateSetClasses.size() ) {
            return;
        }
        final Class<?>[] stateClasses = stateSetClasses.get(0).getDeclaredClasses();
        StateMetaBuilder stateBuilder = null;
        for ( Class<?> klass : stateClasses ) {
            stateBuilder = new StateMetaBuilderImpl(this, klass.getSimpleName());
            final StateMetaBuilder stateMetaBuilder = stateBuilder.build(klass, this);
            addStateMetadata(klass, stateMetaBuilder);
        }
    }

    private void addStateMetadata(Class<?> stateClass, StateMetaBuilder stateMetadata) {
        this.stateList.add(stateMetadata);
        final Iterator<Object> iterator = stateMetadata.getKeySet().iterator();
        while ( iterator.hasNext() ) {
            this.stateMap.put(iterator.next(), stateMetadata);
        }
        if ( stateMetadata.isInitial() ) {
            this.initialState = stateMetadata;
        } else if ( stateMetadata.isFinal() ) {
            this.finalStateList.add(stateMetadata);
        }
        if ( null != stateClass.getAnnotation(ShortCut.class) ) {
            this.shortcutStateList.add(stateMetadata);
        }
    }

    private void configureTransitionSet(Class<?> clazz) {
        final List<Class<?>> transitionSetClasses = findClass(clazz.getDeclaredClasses(), TransitionSet.class);
        if ( 0 >= transitionSetClasses.size() ) {
            return;
        }
        final Class<?>[] transitionClasses = transitionSetClasses.get(0).getDeclaredClasses();
        TransitionMetaBuilder transitionMetaBuilder = null;
        for ( Class<?> klass : transitionClasses ) {
            transitionMetaBuilder = new TransitionMetaBuilderImpl(this, klass.getSimpleName());
            final TransitionMetaBuilder transitionMetadata = transitionMetaBuilder.build(klass, this);
            addTransitionMetadata(clazz, transitionMetadata);
        }
    }

    private void addTransitionMetadata(Class<?> transitionClass, TransitionMetaBuilder transitionMetadata) {
        this.transitionList.add(transitionMetadata);
        final Iterator<Object> iterator = transitionMetadata.getKeySet().iterator();
        while ( iterator.hasNext() ) {
            this.transitionMap.put(iterator.next(), transitionMetadata);
        }
        switch (transitionMetadata.getType()) {
            case Corrupt:
                this.corruptTransition = transitionMetadata;
                break;
            case Recover:
                this.recoverTransition = transitionMetadata;
                break;
            case Redo:
                this.redoTransition = transitionMetadata;
                break;
            case Fail:
                this.failTransition = transitionMetadata;
                break;
            default:
                break;
        }
    }

    private void configureConditionSet(Class<?> clazz) throws VerificationException {
        List<Class<?>> conditionSetClasses = findClass(clazz.getDeclaredClasses(), ConditionSet.class);
        if ( 0 >= conditionSetClasses.size() ) {
            return;
        }
        if ( 1 != conditionSetClasses.size() ) {
            throw newVerificationException(clazz.getName() + ".ConditionSet",
                    Errors.STATEMACHINE_MULTIPLE_CONDITIONSET, clazz.getName());
        }
        final Class<?>[] conditionClasses = conditionSetClasses.get(0).getDeclaredClasses();
        ConditionMetaBuilder conditionMetaBuilder = null;
        for ( Class<?> klass : conditionClasses ) {
            conditionMetaBuilder = new ConditionMetaBuilderImpl(this, klass.getSimpleName());
            final ConditionMetaBuilder conditionMetadata = conditionMetaBuilder.build(klass, this);
            addConditionMetadata(clazz, conditionMetadata);
        }
    }

    private void addConditionMetadata(Class<?> clazz, ConditionMetaBuilder conditionMetaBuilder) {
        this.conditionList.add(conditionMetaBuilder);
        final Iterator<Object> iterator = conditionMetaBuilder.getKeySet().iterator();
        while ( iterator.hasNext() ) {
            conditionMap.put(iterator.next(), conditionMetaBuilder);
        }
    }

    private StateMachineMetadata load(Class<?> stateMachineClass) throws VerificationException {
        StateMachineMetadata stateMachineMetadata = registry.getStateMachineMeta(stateMachineClass);
        if ( null != stateMachineMetadata ) {
            return stateMachineMetadata;
        } else {
            StateMachineMetaBuilder stateMachineMetaBuilder = new StateMachineMetaBuilderImpl(registry,
                    stateMachineClass.getName());
            stateMachineMetadata = stateMachineMetaBuilder.build(stateMachineClass).getMetaData();
            registry.addTemplate(stateMachineMetadata);
            return stateMachineMetadata;
        }
    }

    private Class<?> getSuperMetadataClass(Class<?> clazz) {
        if ( !hasSuperMetadataClass(clazz) ) {
            throw new IllegalStateException("Class " + clazz + " has no super class");
        }
        if ( null != clazz.getSuperclass() && !Object.class.equals(clazz.getSuperclass()) ) {
            return clazz.getSuperclass();
        } else {
            // if clazz is interface or clazz implements an interface.
            return clazz.getInterfaces()[0];
        }
    }

    private void verifySyntax(Class<?> clazz) throws VerificationException {
        verifyStateMachineDefinition(clazz);
        verifyRequiredComponents(clazz);
    }

    private void verifyRequiredComponents(Class<?> clazz) throws VerificationException {
        final String stateSetPath = clazz.getName() + ".StateSet";
        final String transitionSetPath = clazz.getName() + ".TransitionSet";
        if ( hasSuperMetadataClass(clazz) ) {
            return;
        }
        final Class<?>[] declaredClasses = clazz.getDeclaredClasses();
        if ( 0 == declaredClasses.length ) {
            throw newVerificationException(clazz.getName(), Errors.STATEMACHINE_WITHOUT_INNER_CLASSES_OR_INTERFACES,
                    new Object[] { clazz.getName() });
        }
        final List<Class<?>> stateClasses = findClass(declaredClasses, StateSet.class);
        final List<Class<?>> transitionClasses = findClass(declaredClasses, TransitionSet.class);
        final VerificationFailureSet vs = new VerificationFailureSet();
        verifyStateSet(clazz, stateSetPath, stateClasses, vs);
        verifyTransitionSet(clazz, transitionSetPath, transitionClasses, vs);
        if ( vs.size() > 0 ) {
            throw new VerificationException(vs);
        }
    }

    private VerificationFailureSet verifyStateSet(Class<?> clazz, final String stateSetPath,
            final List<Class<?>> stateClasses, final VerificationFailureSet vs) {
        if ( stateClasses.size() <= 0 ) {
            vs.add(newVerificationException(stateSetPath, Errors.STATEMACHINE_WITHOUT_STATESET,
                    new Object[] { clazz.getName() }));
        } else if ( stateClasses.size() > 1 ) {
            vs.add(newVerificationException(stateSetPath, Errors.STATEMACHINE_MULTIPLE_STATESET,
                    new Object[] { clazz.getName() }));
        } else {
            verifyStateSetComponent(stateSetPath, stateClasses.get(0), vs);
        }
        return vs;
    }

    private void verifyTransitionSet(Class<?> clazz, final String transitionSetPath,
            final List<Class<?>> transitionClasses, final VerificationFailureSet vs) {
        if ( transitionClasses.size() <= 0 ) {
            vs.add(newVerificationException(transitionSetPath, Errors.STATEMACHINE_WITHOUT_TRANSITIONSET,
                    new Object[] { clazz.getName() }));
        } else if ( transitionClasses.size() > 1 ) {
            vs.add(newVerificationException(transitionSetPath, Errors.STATEMACHINE_MULTIPLE_TRANSITIONSET,
                    new Object[] { clazz.getName() }));
        } else {
            verifyTransitionSetComponent(transitionSetPath, transitionClasses.get(0), vs);
        }
    }

    private void verifyTransitionSetComponent(final String dottedPath, final Class<?> transitionClass,
            final VerificationFailureSet vs) {
        final Class<?>[] transitionSetClasses = transitionClass.getDeclaredClasses();
        if ( 0 == transitionSetClasses.length ) {
            vs.add(newVerificationException(dottedPath, Errors.TRANSITIONSET_WITHOUT_TRANSITION,
                    new Object[] { transitionClass.getName() }));
        }
    }

    private void verifyStateSetComponent(final String stateSetPath, final Class<?> stateSetClass,
            final VerificationFailureSet vs) {
        final Class<?>[] stateSetClasses = stateSetClass.getDeclaredClasses();
        if ( 0 == stateSetClasses.length ) {
            vs.add(newVerificationException(stateSetPath, Errors.STATESET_WITHOUT_STATE,
                    new Object[] { stateSetClass.getName() }));
        } else {
            List<Class<?>> initialClasses = findClass(stateSetClasses, Initial.class);
            if ( initialClasses.size() == 0 ) {
                vs.add(newVerificationException(stateSetPath + ".Initial", Errors.STATESET_WITHOUT_INITAL_STATE,
                        new Object[] { stateSetClass.getName() }));
            } else if ( initialClasses.size() > 1 ) {
                vs.add(newVerificationException(stateSetPath + ".Initial", Errors.STATESET_MULTIPLE_INITAL_STATES,
                        new Object[] { stateSetClass.getName() }));
            }
            List<Class<?>> endClasses = findClass(stateSetClasses, End.class);
            if ( endClasses.size() == 0 ) {
                vs.add(newVerificationException(stateSetPath + ".Final", Errors.STATESET_WITHOUT_FINAL_STATE,
                        new Object[] { stateSetClass.getName() }));
            }
        }
    }

    private List<Class<?>> findClass(final Class<?>[] declaredClasses, Class<? extends Annotation> annotationClass) {
        ArrayList<Class<?>> stateClasses = new ArrayList<>();
        for ( Class<?> klass : declaredClasses ) {
            if ( null != klass.getAnnotation(annotationClass) ) {
                stateClasses.add(klass);
            }
        }
        return stateClasses;
    }

    private boolean hasSuperMetadataClass(Class<?> clazz) {
        return ( null != clazz.getSuperclass() && !Object.class.equals(clazz.getSuperclass()) )
                || ( 1 <= clazz.getInterfaces().length );
    }

    private void verifyStateMachineDefinition(Class<?> clazz) throws VerificationException {
        if ( !clazz.isInterface() && null != clazz.getSuperclass() ) {
            Class<?> superclass = clazz.getSuperclass();
            if ( !Object.class.equals(superclass) && null == superclass.getAnnotation(StateMachine.class) ) {
                throw newVerificationException(clazz.getName(), Errors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE,
                        new Object[] { superclass.getName() });
            }
        } else if ( clazz.isInterface() && clazz.getInterfaces().length > 0 ) {
            if ( clazz.getInterfaces().length > 1 ) {
                throw newVerificationException(clazz.getName(), Errors.STATEMACHINE_HAS_ONLY_ONE_SUPER_INTERFACE,
                        new Object[] { clazz.getName() });
            }
            Class<?> clz = clazz.getInterfaces()[0];
            if ( null == clz.getAnnotation(StateMachine.class) ) {
                throw newVerificationException(clazz.getName(), Errors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE,
                        new Object[] { clz.getName() });
            }
        } else if ( clazz.isInterface() && clazz.getInterfaces().length <= 0 ) {
            if ( null == clazz.getAnnotation(StateMachine.class)
                    && null == clazz.getAnnotation(CompositeStateMachine.class) ) {
                throw newVerificationException(clazz.getName(), Errors.STATEMACHINE_CLASS_WITHOUT_ANNOTATION,
                        clazz.getName());
            }
        }
    }
}
