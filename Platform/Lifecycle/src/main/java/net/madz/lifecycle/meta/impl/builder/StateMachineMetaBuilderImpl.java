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
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.RelationMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.builder.TransitionMetaBuilder;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineMetaBuilderImpl extends
        AnnotationMetaBuilderBase<StateMachineMetaBuilder, StateMachineMetaBuilder> implements StateMachineMetaBuilder {

    private StateMachineMetadata superStateMachineMetadata;
    private StateMachineMetadata parentStateMachineMetadata;
    /* //////////////////////////////////////////////////// */
    /* //////// Fields For Related State Machine ////////// */
    /* //////////////////////////////////////////////////// */
    private ArrayList<RelationMetaBuilder> relationList = new ArrayList<>();
    private HashMap<Object, RelationMetaBuilder> relationMap = new HashMap<>();
    private HashMap<Object, StateMachineMetadata> relatedStateMachineMap = new HashMap<>();
    private ArrayList<StateMachineMetadata> relatedStateMachineList = new ArrayList<>();
    /* //////////////////////////////////////////////////// */
    /* ////////////// Fields For Transitions ////////////// */
    /* //////////////////////////////////////////////////// */
    private final ArrayList<TransitionMetaBuilder> transitionList = new ArrayList<>();
    private final HashMap<Object, TransitionMetaBuilder> transitionMap = new HashMap<>();
    private TransitionMetaBuilder corruptTransition;
    private TransitionMetaBuilder recoverTransition;
    private TransitionMetaBuilder redoTransition;
    private TransitionMetaBuilder failTransition;
    /* //////////////////////////////////////////////////// */
    /* ////////////// Fields For Condition /////////////// */
    /* //////////////////////////////////////////////////// */
    private final ArrayList<ConditionMetaBuilder> conditionList = new ArrayList<>();
    private final HashMap<Object, ConditionMetaBuilder> conditionMap = new HashMap<>();
    /* //////////////////////////////////////////////////// */
    /* /////////////////// Fields For State /////////////// */
    /* //////////////////////////////////////////////////// */
    private final ArrayList<StateMetaBuilder> stateList = new ArrayList<>();
    private final HashMap<Object, StateMetaBuilder> stateMap = new HashMap<>();
    private ArrayList<StateMetaBuilder> finalStateList = new ArrayList<>();
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
    private final ArrayList<StateMetaBuilder> shortcutStateList = new ArrayList<>();
    private final ArrayList<RelationMetaBuilder> compositeStateMachineList = new ArrayList<>();

    public StateMachineMetaBuilderImpl(AbsStateMachineRegistry registry, String name) {
        this(name);
        this.registry = registry;
    }

    public StateMachineMetaBuilderImpl(String name) {
        super(null, name);
    }

    public StateMachineMetaBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, name);
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
        return relationList.size() > 0;
    }

    @Override
    public StateMachineMetadata[] getRelatedStateMachineMetadata() {
        return relatedStateMachineList.toArray(new StateMachineMetadata[relatedStateMachineList.size()]);
    }

    @Override
    public StateMachineMetadata getRelatedStateMachine(Class<?> relationClass) {
        return relatedStateMachineMap.get(relationClass);
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
    public TransitionMetadata[] getDeclaredTransitionSet() {
        return transitionList.toArray(new TransitionMetadata[transitionList.size()]);
    }

    @Override
    public TransitionMetadata getDeclaredTransition(Object transitionKey) {
        return this.transitionMap.get(transitionKey);
    }

    @Override
    public TransitionMetadata getStateSynchronizationTransition() {
        return null;
    }

    @Override
    public StateMachineInst newInstance(Class<?> clazz) throws VerificationException {
        final StateMachineInstBuilderImpl builder = new StateMachineInstBuilderImpl(this, clazz.getName());
        builder.setRegistry(registry);
        return builder.build(clazz).getMetaData();
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public StateMachineMetaBuilder filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        return this;
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
    /* ////////////// Methods For Builder ///////////////// */
    /* //////////////////////////////////////////////////// */
    @Override
    public StateMachineMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent) throws VerificationException {
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
        for ( final Class<?> stateClass : findComponentClasses(clazz, StateSet.class) ) {
            StateMetaBuilder stateMetaBuilder = this.stateMap.get(stateClass);
            stateMetaBuilder.configureCompositeStateMachine(stateClass);
        }
    }

    private void configureSuperStateMachine(Class<?> clazz) throws VerificationException {
        if ( hasSuperMetadataClass(clazz) ) {
            this.superStateMachineMetadata = load(getSuperMetadataClass(clazz));
        }
    }

    private void configureFunctions(Class<?> clazz) throws VerificationException {
        for ( final Class<?> stateClass : findComponentClasses(clazz, StateSet.class) ) {
            final StateMetaBuilder stateMetaBuilder = this.stateMap.get(stateClass);
            stateMetaBuilder.configureFunctions(stateClass);
        }
    }

    private void configureStateSetRelations(Class<?> clazz) throws VerificationException {
        for ( final Class<?> stateClass : findComponentClasses(clazz, StateSet.class) ) {
            final StateMetaBuilder stateMetaBuilder = this.stateMap.get(stateClass);
            stateMetaBuilder.configureRelations(stateClass);
        }
    }

    private void configureRelationSet(Class<?> clazz) throws VerificationException {
        RelationMetaBuilder relationBuilder = null;
        for ( Class<?> klass : findComponentClasses(clazz, RelationSet.class) ) {
            relationBuilder = new RelationMetaBuilderImpl(this, klass.getSimpleName());
            relationBuilder.build(klass, this);
            addRelationMetadata(klass, relationBuilder);
            RelateTo relateTo = klass.getAnnotation(RelateTo.class);
            Class<?> relatedStateMachineClass = relateTo.value();
            try {
                StateMachineMetadata relatedStateMachineMetadata = load(relatedStateMachineClass);
                this.relatedStateMachineList.add(relatedStateMachineMetadata);
                this.relatedStateMachineMap.put(klass, relatedStateMachineMetadata);
                this.relatedStateMachineMap.put(klass.getName(), relatedStateMachineMetadata);
                this.relatedStateMachineMap.put(klass.getSimpleName(), relatedStateMachineMetadata);
            } catch (VerificationException e) {
                if ( Errors.STATEMACHINE_CLASS_WITHOUT_ANNOTATION.equals(e.getVerificationFailureSet().iterator()
                        .next().getErrorCode()) ) {
                    throw newVerificationException(getDottedPath(),
                            Errors.RELATION_RELATED_TO_REFER_TO_NON_STATEMACHINE, relateTo);
                }
            }
        }
    }

    private void addRelationMetadata(Class<?> klass, RelationMetaBuilder relationBuilder) {
        this.relationList.add(relationBuilder);
        final Iterator<Object> iterator = relationBuilder.getKeySet().iterator();
        while ( iterator.hasNext() ) {
            this.relationMap.put(iterator.next(), relationBuilder);
        }
    }

    private void configureStateSetBasic(Class<?> clazz) throws VerificationException {
        final Class<?>[] stateClasses = findComponentClasses(clazz, StateSet.class);
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

    private void configureTransitionSet(Class<?> clazz) throws VerificationException {
        final List<Class<?>> transitionSetClasses = findComponentClass(clazz.getDeclaredClasses(), TransitionSet.class);
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
        ConditionMetaBuilder conditionMetaBuilder = null;
        for ( Class<?> klass : findComponentClasses(clazz, ConditionSet.class) ) {
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

    protected StateMachineMetadata load(Class<?> stateMachineClass) throws VerificationException {
        StateMachineMetadata stateMachineMetadata = registry.getStateMachineMeta(stateMachineClass);
        if ( null != stateMachineMetadata ) {
            return stateMachineMetadata;
        } else {
            StateMachineMetaBuilder stateMachineMetaBuilder = new StateMachineMetaBuilderImpl(registry,
                    stateMachineClass.getName());
            stateMachineMetadata = stateMachineMetaBuilder.build(stateMachineClass, this).getMetaData();
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

    /* //////////////////////////////////////////////////// */
    /* ////////////// Methods For Syntax Verification ///// */
    /* //////////////////////////////////////////////////// */
    private void verifySyntax(Class<?> clazz) throws VerificationException {
        verifyStateMachineDefinition(clazz);
        verifyRequiredComponents(clazz);
        verifyOptionalComponents(clazz);
    }

    private void verifyOptionalComponents(Class<?> klass) throws VerificationException {
        verifyRelationSet(klass);
        verifyConditionSet(klass);
    }

    private void verifyRelationSet(Class<?> klass) throws VerificationException {
        final List<Class<?>> relationSetClass = findComponentClass(klass.getDeclaredClasses(), RelationSet.class);
        if ( 1 < relationSetClass.size() ) {
            throw newVerificationException(getDottedPath(), Errors.RELATIONSET_MULTIPLE, klass);
        }
    }

    private void verifyConditionSet(Class<?> klass) throws VerificationException {
        final List<Class<?>> conditionSetClasses = findComponentClass(klass.getDeclaredClasses(), ConditionSet.class);
        if ( 1 < conditionSetClasses.size() ) {
            throw newVerificationException(getDottedPath(), Errors.CONDITIONSET_MULTIPLE, klass);
        }
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
        final List<Class<?>> stateClasses = findComponentClass(declaredClasses, StateSet.class);
        final List<Class<?>> transitionClasses = findComponentClass(declaredClasses, TransitionSet.class);
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
            List<Class<?>> initialClasses = findComponentClass(stateSetClasses, Initial.class);
            if ( initialClasses.size() == 0 ) {
                vs.add(newVerificationException(stateSetPath + ".Initial", Errors.STATESET_WITHOUT_INITAL_STATE,
                        new Object[] { stateSetClass.getName() }));
            } else if ( initialClasses.size() > 1 ) {
                vs.add(newVerificationException(stateSetPath + ".Initial", Errors.STATESET_MULTIPLE_INITAL_STATES,
                        new Object[] { stateSetClass.getName() }));
            }
            List<Class<?>> endClasses = findComponentClass(stateSetClasses, End.class);
            if ( endClasses.size() == 0 ) {
                vs.add(newVerificationException(stateSetPath + ".Final", Errors.STATESET_WITHOUT_FINAL_STATE,
                        new Object[] { stateSetClass.getName() }));
            }
        }
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

    private Class<?>[] findComponentClasses(Class<?> clazz, Class<? extends Annotation> componentClass) {
        final List<Class<?>> stateSetClasses = findComponentClass(clazz.getDeclaredClasses(), componentClass);
        if ( 0 >= stateSetClasses.size() ) {
            return new Class<?>[0];
        }
        final Class<?>[] stateClasses = stateSetClasses.get(0).getDeclaredClasses();
        return stateClasses;
    }

    private List<Class<?>> findComponentClass(final Class<?>[] declaredClasses,
            Class<? extends Annotation> annotationClass) {
        ArrayList<Class<?>> stateClasses = new ArrayList<>();
        for ( Class<?> klass : declaredClasses ) {
            if ( null != klass.getAnnotation(annotationClass) ) {
                stateClasses.add(klass);
            }
        }
        return stateClasses;
    }

    @Override
    public boolean hasRelation(Class<?> relationClass) {
        return relationMap.containsKey(relationClass);
    }

    @Override
    public TransitionMetadata[] getAllTransitions() {
        final ArrayList<TransitionMetadata> result = new ArrayList<>();
        loadTransitions(this, result);
        return result.toArray(new TransitionMetadata[0]);
    }

    private void loadTransitions(final StateMachineMetaBuilder stateMachineMetaBuilder,
            final ArrayList<TransitionMetadata> result) {
        populateTransitions(stateMachineMetaBuilder, result);
        for ( final StateMachineMetaBuilder compositeStateMachine : stateMachineMetaBuilder.getCompositeStateMachines() ) {
            populateTransitions(compositeStateMachine, result);
        }
        if ( null != stateMachineMetaBuilder.getSuperStateMachine() ) {
            loadTransitions(stateMachineMetaBuilder, result);
        }
    }

    private void populateTransitions(StateMachineMetaBuilder stateMachineMetaBuilder,
            ArrayList<TransitionMetadata> result) {
        for ( TransitionMetadata transition : stateMachineMetaBuilder.getDeclaredTransitionSet() ) {
            result.add(transition);
        }
    }

    @Override
    public TransitionMetadata getTransition(Object transitionKey) {
        return findTransition(this, transitionKey);
    }

    private TransitionMetadata findTransition(StateMachineMetadata stateMachineMetaBuilder, Object transitionKey) {
        if ( null == stateMachineMetaBuilder ) return null;
        TransitionMetadata transitionMetadata = stateMachineMetaBuilder.getDeclaredTransition(transitionKey);
        if ( null != transitionMetadata ) {
            return transitionMetadata;
        }
        for ( StateMachineMetadata builder : stateMachineMetaBuilder.getCompositeStateMachines() ) {
            transitionMetadata = builder.getDeclaredTransition(transitionKey);
            if ( null != transitionMetadata ) return transitionMetadata;
        }
        return findTransition(getSuperStateMachine(), transitionKey);
    }

    @Override
    public boolean hasTransition(Object transitionKey) {
        return null != getTransition(transitionKey);
    }

    @Override
    public StateMachineMetaBuilder[] getCompositeStateMachines() {
        return compositeStateMachineList.toArray(new StateMachineMetaBuilder[0]);
    }
}
