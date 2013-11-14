package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.madz.common.Dumper;
import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.relation.Parent;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Overrides;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.builder.TransitionMetaBuilder;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineMetaBuilderImpl extends
        InheritableAnnotationMetaBuilderBase<StateMachineMetadata, StateMachineMetadata> implements
        StateMachineMetaBuilder {

    StateMachineMetadata superMeta;
    private StateMachineMetadata parentStateMachineMetadata;
    /* //////////////////////////////////////////////////// */
    /* //////// Fields For Related State Machine ////////// */
    /* //////////////////////////////////////////////////// */
    private HashSet<Class<?>> relationSet = new HashSet<>();
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
    @SuppressWarnings("unused")
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
    private StateMetadata owningState;
    // If this state machine is a composite state machine, owningStateMachine is
    // the enclosing state's (parent) StateMachine
    // private StateMachineMetadata owningStateMachine;
    // Also for composite State Machine
    private final ArrayList<StateMetaBuilder> shortcutStateList = new ArrayList<>();
    private final ArrayList<StateMachineMetaBuilder> compositeStateMachineList = new ArrayList<>();

    public StateMachineMetaBuilderImpl(AbsStateMachineRegistry registry, String name) {
        super(null, name);
        this.registry = registry;
    }

    public StateMachineMetaBuilderImpl(StateMachineMetaBuilderImpl parent, String name) {
        super(parent, name);
        parent.compositeStateMachineList.add(this);
        this.registry = parent.getRegistry();
    }

    @Override
    public boolean hasSuper() {
        return null != this.getSuper();
    }

    @Override
    public StateMachineMetadata getSuperStateMachine() {
        return getSuper();
    }

    @Override
    public boolean hasParent() {
        return null != parentStateMachineMetadata;
    }

    @Override
    public boolean hasRelations() {
        // return relationList.size() > 0;
        return relationSet.size() > 0;
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
    public StateMetadata[] getDeclaredStateSet() {
        return stateList.toArray(new StateMetadata[stateList.size()]);
    }

    @Override
    public StateMetadata getDeclaredState(Object stateKey) {
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
    public StateMachineObject newInstance(Class<?> clazz) throws VerificationException {
        final StateMachineObjectBuilderImpl builder = new StateMachineObjectBuilderImpl(this, clazz.getName());
        builder.setRegistry(registry);
        return builder.build(clazz).getMetaData();
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
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
        return getParent();
    }

    @Override
    public StateMetadata getOwningState() {
        return owningState;
    }

    @Override
    public void setOwningState(StateMetadata stateMetaBuilder) {
        this.owningState = stateMetaBuilder;
    }

    @Override
    public StateMetadata[] getShortcutStateSet() {
        return shortcutStateList.toArray(new StateMetadata[shortcutStateList.size()]);
    }

    /* //////////////////////////////////////////////////// */
    /* ////////////// Methods For Builder ///////////////// */
    /* //////////////////////////////////////////////////// */
    @Override
    public StateMachineMetaBuilder build(Class<?> clazz, StateMachineMetadata parent) throws VerificationException {
        preConfigureStateMachineType(clazz);
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
            configureRelationSet(clazz);
            configureStateSetRelations(clazz);
            configureCompositeStateMachine(clazz);
            configureFunctions(clazz);
        }
        addKeys(clazz);
        return this;
    }

    private void preConfigureStateMachineType(Class<?> clazz) {
        if ( isCompositeStateMachine(clazz) ) {
            setOwningState(getOwningStateMachine().getState(clazz));
            setComposite(true);
        }
    }

    private void configureCompositeStateMachine(Class<?> clazz) throws VerificationException {
        for ( final Class<?> stateClass : findComponentClasses(clazz, StateSet.class) ) {
            StateMetaBuilder stateMetaBuilder = this.stateMap.get(stateClass);
            stateMetaBuilder.configureCompositeStateMachine(stateClass);
            verifyCompositeParentRelationSyntax(clazz, stateClass, stateMetaBuilder.getCompositeStateMachine());
        }
    }

    private void verifyCompositeParentRelationSyntax(Class<?> owningStateMachineClass, final Class<?> stateClass,
            final StateMachineMetadata compositeStateMachine) throws VerificationException {
        final Class<?> owningParentRelation = getDeclaredParentRelation(owningStateMachineClass);
        if ( null != compositeStateMachine ) {
            final Class<?> compositeParentRelation = getDeclaredParentRelation(stateClass);
            if ( null != owningParentRelation && null != compositeParentRelation ) {
                throw newVerificationException(compositeStateMachine.getDottedPath(),
                        SyntaxErrors.RELATION_COMPOSITE_STATE_MACHINE_CANNOT_OVERRIDE_OWNING_PARENT_RELATION,
                        compositeStateMachine.getDottedPath(), compositeParentRelation, owningStateMachineClass,
                        owningParentRelation);
            }
        }
    }

    private void configureSuperStateMachine(Class<?> clazz) throws VerificationException {
        if ( !hasSuperMetadataClass(clazz) ) {
            return;
        }
        if ( isComposite() && isNotCompositeState(getSuperMetadataClass(clazz)) ) {
            this.setSuper(null);
        } else if ( isComposite() ) {
            this.setSuper(registry.loadStateMachineMetadata(getSuperMetadataClass(clazz), this));
        } else if ( hasSuperMetadataClass(clazz) ) {
            this.setSuper(registry.loadStateMachineMetadata(getSuperMetadataClass(clazz), this));
        }
    }

    private boolean isNotCompositeState(Class<?> superMetadataClass) {
        if ( isCompositeStateMachine(superMetadataClass) ) {
            return false;
        }
        if ( null != superMetadataClass.getAnnotation(End.class) ) {
            return true;
        } else if ( null != superMetadataClass.getAnnotation(Function.class) ) {
            return true;
        }
        return false;
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
        verifyParentRelationSyntax(clazz);
        for ( Class<?> relationClass : findComponentClasses(clazz, RelationSet.class) ) {
            verifyRelateTo(relationClass);
            addRelationMetadata(relationClass);
            addRelatedStateMachine(relationClass);
        }
    }

    private void verifyRelateTo(Class<?> clazz) throws VerificationException {
        if ( !hasSuperMetadataClass(clazz) ) {
            if ( null == clazz.getAnnotation(RelateTo.class) ) {
                throw newVerificationException(clazz.getName(), SyntaxErrors.RELATION_NO_RELATED_TO_DEFINED, clazz);
            }
        } else if ( isOverriding(clazz) ) {
            if ( !hasRelateToDeclared(clazz) ) {
                throw newVerificationException(clazz.getName(), SyntaxErrors.RELATION_NO_RELATED_TO_DEFINED, clazz);
            }
        } else {
            if ( null != clazz.getAnnotation(RelateTo.class) ) {
                verifyRelateTo(getSuperMetadataClass(clazz));
            }
        }
    }

    private boolean hasRelateToDeclared(Class<?> clazz) {
        for ( Annotation anno : clazz.getDeclaredAnnotations() ) {
            if ( RelateTo.class == anno.annotationType() ) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverriding(Class<?> clazz) {
        boolean overriding = false;
        for ( Annotation anno : clazz.getDeclaredAnnotations() ) {
            if ( Overrides.class == anno.annotationType() ) {
                return true;
            }
        }
        return false;
    }

    private Class<?> getDeclaredParentRelation(Class<?> clazz) {
        for ( Class<?> relationClass : findComponentClasses(clazz, RelationSet.class) ) {
            if ( hasParent(relationClass) ) {
                return relationClass;
            }
        }
        return null;
    }

    private void verifyParentRelationSyntax(Class<?> clazz) throws VerificationException {
        boolean hasParentRelation = false;
        for ( Class<?> relationClass : findComponentClasses(clazz, RelationSet.class) ) {
            if ( hasParentRelation && hasParent(relationClass) ) {
                throw newVerificationException(getDottedPath(), SyntaxErrors.RELATION_MULTIPLE_PARENT_RELATION, clazz);
            } else if ( hasParent(relationClass) ) {
                hasParentRelation = true;
                if ( hasSuper() ) {
                    if ( getSuperStateMachine().hasParent() && null == relationClass.getAnnotation(Overrides.class) ) {
                        throw newVerificationException(getDottedPath(),
                                SyntaxErrors.RELATION_NEED_OVERRIDES_TO_OVERRIDE_SUPER_STATEMACHINE_PARENT_RELATION,
                                clazz, getSuperMetadataClass(clazz));
                    }
                }
            }
        }
    }

    private boolean hasParent(Class<?> relationClass) {
        return null != relationClass.getAnnotation(Parent.class);
    }

    private void addRelationMetadata(Class<?> relationClass) throws VerificationException {
        // RelationMetaBuilder relationBuilder = new
        // RelationMetaBuilderImpl(this, relationClass.getSimpleName());
        // relationBuilder.build(relationClass, this);
        // this.relationList.add(relationBuilder);
        // final Iterator<Object> iterator =
        // relationBuilder.getKeySet().iterator();
        // while ( iterator.hasNext() ) {
        // this.relationMap.put(iterator.next(), relationBuilder);
        // }
        this.relationSet.add(relationClass);
    }

    private void addRelatedStateMachine(Class<?> relationClass) throws VerificationException {
        final RelateTo relateTo = relationClass.getAnnotation(RelateTo.class);
        final Class<?> relatedStateMachineClass = relateTo.value();
        try {
            final StateMachineMetadata relatedStateMachineMetadata = registry.loadStateMachineMetadata(
                    relatedStateMachineClass, this);
            this.relatedStateMachineList.add(relatedStateMachineMetadata);
            final Iterator<Object> keyIterator = relatedStateMachineMetadata.getKeySet().iterator();
            while ( keyIterator.hasNext() ) {
                this.relatedStateMachineMap.put(keyIterator.next(), relatedStateMachineMetadata);
            }
            this.relatedStateMachineMap.put(relationClass, relatedStateMachineMetadata);
            if ( hasParent(relationClass) ) {
                this.parentStateMachineMetadata = relatedStateMachineMetadata;
            }
        } catch (VerificationException e) {
            if ( SyntaxErrors.STATEMACHINE_CLASS_WITHOUT_ANNOTATION.equals(e.getVerificationFailureSet().iterator()
                    .next().getErrorCode()) ) {
                throw newVerificationException(getDottedPath(),
                        SyntaxErrors.RELATION_RELATED_TO_REFER_TO_NON_STATEMACHINE, relateTo);
            }
        }
    }

    private void configureStateSetBasic(Class<?> clazz) throws VerificationException {
        final Class<?>[] stateClasses = findComponentClasses(clazz, StateSet.class);
        StateMetaBuilder stateBuilder = null;
        for ( Class<?> klass : stateClasses ) {
            verifyStateClassBasic(klass);
            stateBuilder = new StateMetaBuilderImpl(this, klass.getSimpleName());
            final StateMetaBuilder stateMetaBuilder = stateBuilder.build(klass, this);
            addStateMetadata(klass, stateMetaBuilder);
        }
    }

    private void verifyStateClassBasic(Class<?> klass) throws VerificationException {
        if ( null != klass.getAnnotation(Overrides.class) ) {
            if ( klass.isInterface() ) {
                if ( 0 >= klass.getInterfaces().length ) {
                    throw newVerificationException(getDottedPath() + ".StateSet." + klass.getSimpleName(),
                            SyntaxErrors.STATE_OVERRIDES_WITHOUT_SUPER_CLASS, klass);
                }
            } else if ( null == klass.getSuperclass() ) {
                throw newVerificationException(getDottedPath() + ".StateSet." + klass.getSimpleName(),
                        SyntaxErrors.STATE_OVERRIDES_WITHOUT_SUPER_CLASS, klass);
            }
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

    private boolean isCompositeStateMachine(Class<?> stateMachineClass) {
        return null != stateMachineClass.getAnnotation(CompositeStateMachine.class);
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
            throw newVerificationException(getDottedPath(), SyntaxErrors.RELATIONSET_MULTIPLE, klass);
        }
    }

    private void verifyConditionSet(Class<?> klass) throws VerificationException {
        final List<Class<?>> conditionSetClasses = findComponentClass(klass.getDeclaredClasses(), ConditionSet.class);
        if ( 1 < conditionSetClasses.size() ) {
            throw newVerificationException(getDottedPath(), SyntaxErrors.CONDITIONSET_MULTIPLE, klass);
        }
    }

    private void verifyRequiredComponents(Class<?> clazz) throws VerificationException {
        final String stateSetPath = clazz.getName() + ".StateSet";
        final String transitionSetPath = clazz.getName() + ".TransitionSet";
        if ( hasSuperMetadataClass(clazz) ) {
            verifyStateOverrides(clazz);
            return;
        } else {
            final Class<?>[] declaredClasses = clazz.getDeclaredClasses();
            if ( 0 == declaredClasses.length ) {
                throw newVerificationException(clazz.getName(),
                        SyntaxErrors.STATEMACHINE_WITHOUT_INNER_CLASSES_OR_INTERFACES, new Object[] { clazz.getName() });
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
    }

    private void verifyStateOverrides(Class<?> clazz) throws VerificationException {
        final List<Class<?>> stateSetClasses = findComponentClass(clazz.getDeclaredClasses(), StateSet.class);
        if ( 0 >= stateSetClasses.size() ) {
            return;
        }
        Class<?> stateSetClass = stateSetClasses.get(0);
        final Class<?>[] stateClasses = stateSetClass.getDeclaredClasses();
        if ( 0 == stateClasses.length ) {
            return;
        }
        verifyStateOverridesInitial(stateClasses);
    }

    private void verifyStateOverridesInitial(Class<?>[] stateClasses) throws VerificationException {
        if ( 0 != findComponentClass(stateClasses, Initial.class).size() ) {
            return;
        }
        for ( Class<?> stateClass : stateClasses ) {
            if ( hasSuperMetadataClass(stateClass) ) {
                final Class<?> superStateClass = getSuperMetadataClass(stateClass);
                if ( hasInitial(superStateClass) && hasOverrides(stateClass) ) {
                    throw newVerificationException(getDottedPath() + ".StateSet",
                            SyntaxErrors.STATESET_WITHOUT_INITAL_STATE_AFTER_OVERRIDING_SUPER_INITIAL_STATE,
                            stateClass, superStateClass);
                }
            }
        }
    }

    private boolean hasOverrides(Class<?> stateClass) {
        return null != stateClass.getAnnotation(Overrides.class);
    }

    private boolean hasInitial(Class<?> superClass) {
        return null != superClass.getAnnotation(Initial.class);
    }

    private VerificationFailureSet verifyStateSet(Class<?> clazz, final String stateSetPath,
            final List<Class<?>> stateClasses, final VerificationFailureSet vs) {
        if ( stateClasses.size() <= 0 ) {
            vs.add(newVerificationException(stateSetPath, SyntaxErrors.STATEMACHINE_WITHOUT_STATESET, clazz));
        } else if ( stateClasses.size() > 1 ) {
            vs.add(newVerificationException(stateSetPath, SyntaxErrors.STATEMACHINE_MULTIPLE_STATESET, clazz));
        } else {
            verifyStateSetComponent(stateSetPath, stateClasses.get(0), vs);
        }
        return vs;
    }

    private void verifyTransitionSet(Class<?> clazz, final String transitionSetPath,
            final List<Class<?>> transitionClasses, final VerificationFailureSet vs) {
        if ( transitionClasses.size() <= 0 ) {
            vs.add(newVerificationException(transitionSetPath, SyntaxErrors.STATEMACHINE_WITHOUT_TRANSITIONSET, clazz));
        } else if ( transitionClasses.size() > 1 ) {
            vs.add(newVerificationException(transitionSetPath, SyntaxErrors.STATEMACHINE_MULTIPLE_TRANSITIONSET, clazz));
        } else {
            verifyTransitionSetComponent(transitionSetPath, transitionClasses.get(0), vs);
        }
    }

    private void verifyTransitionSetComponent(final String dottedPath, final Class<?> transitionClass,
            final VerificationFailureSet vs) {
        final Class<?>[] transitionSetClasses = transitionClass.getDeclaredClasses();
        if ( 0 == transitionSetClasses.length ) {
            vs.add(newVerificationException(dottedPath, SyntaxErrors.TRANSITIONSET_WITHOUT_TRANSITION, transitionClass));
        }
    }

    private void verifyStateSetComponent(final String stateSetPath, final Class<?> stateSetClass,
            final VerificationFailureSet vs) {
        final Class<?>[] stateSetClasses = stateSetClass.getDeclaredClasses();
        if ( 0 == stateSetClasses.length ) {
            vs.add(newVerificationException(stateSetPath, SyntaxErrors.STATESET_WITHOUT_STATE, stateSetClass));
        } else {
            List<Class<?>> initialClasses = findComponentClass(stateSetClasses, Initial.class);
            if ( initialClasses.size() == 0 ) {
                vs.add(newVerificationException(stateSetPath + ".Initial", SyntaxErrors.STATESET_WITHOUT_INITIAL_STATE,
                        stateSetClass));
            } else if ( initialClasses.size() > 1 ) {
                vs.add(newVerificationException(stateSetPath + ".Initial",
                        SyntaxErrors.STATESET_MULTIPLE_INITAL_STATES, stateSetClass));
            }
            List<Class<?>> endClasses = findComponentClass(stateSetClasses, End.class);
            if ( endClasses.size() == 0 ) {
                vs.add(newVerificationException(stateSetPath + ".Final", SyntaxErrors.STATESET_WITHOUT_FINAL_STATE,
                        stateSetClass));
            }
        }
    }

    private boolean hasSuperMetadataClass(Class<?> clazz) {
        return ( null != clazz.getSuperclass() && !Object.class.equals(clazz.getSuperclass()) )
                || ( 1 <= clazz.getInterfaces().length );
    }

    private void verifyStateMachineDefinition(Class<?> clazz) throws VerificationException {
        if ( !clazz.isInterface() && null != clazz.getSuperclass() ) {
            final Class<?> superclass = clazz.getSuperclass();
            if ( !Object.class.equals(superclass) && null == superclass.getAnnotation(StateMachine.class) ) {
                throw newVerificationException(clazz.getName(), SyntaxErrors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE,
                        superclass);
            }
        } else if ( clazz.isInterface() && clazz.getInterfaces().length > 0 ) {
            if ( clazz.getInterfaces().length > 1 ) {
                throw newVerificationException(clazz.getName(), SyntaxErrors.STATEMACHINE_HAS_ONLY_ONE_SUPER_INTERFACE,
                        clazz);
            }
            final Class<?> clz = clazz.getInterfaces()[0];
            if ( isComposite() ) {
                //
            } else if ( null == clz.getAnnotation(StateMachine.class) ) {
                throw newVerificationException(clazz.getName(), SyntaxErrors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE,
                        new Object[] { clz.getName() });
            }
        } else if ( clazz.isInterface() && clazz.getInterfaces().length <= 0 ) {
            if ( null == clazz.getAnnotation(StateMachine.class)
                    && null == clazz.getAnnotation(CompositeStateMachine.class) ) {
                throw newVerificationException(clazz.getName(), SyntaxErrors.STATEMACHINE_CLASS_WITHOUT_ANNOTATION,
                        clazz);
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
            for ( Annotation annotation : klass.getDeclaredAnnotations() ) {
                if ( annotation.annotationType().equals(annotationClass) ) {
                    stateClasses.add(klass);
                    break;
                }
            }
        }
        return stateClasses;
    }

    @Override
    public boolean hasRelation(Class<?> relationClass) {
        if ( this.relationSet.contains(relationClass) ) {
            return true;
        }
        for ( StateMachineMetaBuilder builder : getCompositeStateMachines() ) {
            if ( builder.hasRelation(relationClass) ) {
                return true;
            }
        }
        if ( !isComposite() || ( isComposite() && getOwningStateMachine().equals(getSuperStateMachine()) ) ) {
            if ( null != getSuperStateMachine() ) {
                if ( getSuperStateMachine().hasRelation(relationClass) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public TransitionMetadata[] getAllTransitions() {
        final ArrayList<TransitionMetadata> result = new ArrayList<>();
        loadTransitions(this, result);
        return result.toArray(new TransitionMetadata[0]);
    }

    private void loadTransitions(final StateMachineMetadata stateMachineMetaBuilder,
            final ArrayList<TransitionMetadata> result) {
        populateTransitions(stateMachineMetaBuilder, result);
        for ( final StateMachineMetadata compositeStateMachine : stateMachineMetaBuilder.getCompositeStateMachines() ) {
            populateTransitions(compositeStateMachine, result);
        }
        if ( null != stateMachineMetaBuilder.getSuperStateMachine() ) {
            loadTransitions(stateMachineMetaBuilder.getSuperStateMachine(), result);
        }
    }

    private void populateTransitions(StateMachineMetadata stateMachineMetaBuilder, ArrayList<TransitionMetadata> result) {
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

    @Override
    public StateMetadata[] getAllStates() {
        final ArrayList<StateMetadata> results = new ArrayList<StateMetadata>();
        final ArrayList<StateMetadata> overridedStates = new ArrayList<StateMetadata>();
        populateStateMetadatas(this, results, overridedStates);
        return results.toArray(new StateMetadata[0]);
    }

    private void populateStateMetadatas(final StateMachineMetadata stateMachine,
            final ArrayList<StateMetadata> results, final ArrayList<StateMetadata> overridedStates) {
        if ( null == stateMachine ) {
            return;
        }
        populateStates(results, stateMachine, overridedStates);
        for ( final StateMachineMetadata stateMachineMeta : stateMachine.getCompositeStateMachines() ) {
            if ( !overridedStates.contains(stateMachineMeta.getOwningState()) ) {
                populateStates(results, stateMachineMeta, overridedStates);
            }
        }
        if ( !stateMachine.isComposite() || stateMachine.isComposite() && !stateMachine.getOwningState().isOverriding() ) {
            populateStateMetadatas(stateMachine.getSuperStateMachine(), results, overridedStates);
        }
    }

    private void populateStates(final ArrayList<StateMetadata> results, final StateMachineMetadata stateMachineMeta,
            final ArrayList<StateMetadata> overridedStates) {
        for ( StateMetadata stateMetadata : stateMachineMeta.getDeclaredStateSet() ) {
            if ( !overridedStates.contains(stateMetadata) ) {
                results.add(stateMetadata);
                if ( null != stateMetadata.getSuper() ) { // stateMetadata.isOverriding()
                                                          // ) {
                    addOverridedStates(overridedStates, stateMetadata.getSuper());
                }
            }
        }
    }

    private void addOverridedStates(ArrayList<StateMetadata> overridedStates, StateMetadata superStateMetadata) {
        if ( null == superStateMetadata ) {
            return;
        }
        overridedStates.add(superStateMetadata);
        if ( null != superStateMetadata.getSuper() ) {// .isOverriding()
                                                      // ) {
            addOverridedStates(overridedStates, superStateMetadata.getSuper());
        }
    }

    @Override
    public StateMetadata getState(Object stateKey) {
        return findState(this, stateKey);
    }

    private StateMetadata findState(final StateMachineMetadata stateMachine, final Object stateKey) {
        if ( null == stateMachine ) {
            return null;
        }
        final StateMetadata declaredState = stateMachine.getDeclaredState(stateKey);
        if ( null != declaredState ) {
            return declaredState;
        }
        for ( final StateMachineMetadata stateMachineMetadata : stateMachine.getCompositeStateMachines() ) {
            final StateMetadata state = stateMachineMetadata.getDeclaredState(stateKey);
            if ( null != state ) {
                return state;
            }
        }
        return findState(stateMachine.getSuperStateMachine(), stateKey);
    }

    @Override
    public ConditionMetadata[] getDeclaredConditions() {
        return this.conditionList.toArray(new ConditionMetadata[0]);
    }

    @Override
    public ConditionMetadata[] getAllCondtions() {
        final LinkedList<ConditionMetadata> conditions = new LinkedList<ConditionMetadata>();
        getCondition(this, conditions);
        return conditions.toArray(new ConditionMetadata[0]);
    }

    private void getCondition(final StateMachineMetadata stateMachine, final LinkedList<ConditionMetadata> conditions) {
        if ( null == stateMachine ) {
            return;
        }
        for ( ConditionMetadata conditionMetadata : stateMachine.getDeclaredConditions() ) {
            conditions.add(conditionMetadata);
        }
        for ( StateMachineMetadata item : stateMachine.getCompositeStateMachines() ) {
            for ( ConditionMetadata conditionMetadata : item.getDeclaredConditions() ) {
                conditions.add(conditionMetadata);
            }
        }
        getCondition(stateMachine.getSuperStateMachine(), conditions);
    }

    @Override
    public ConditionMetadata getCondtion(Object conditionKey) {
        if ( null != this.conditionMap.get(conditionKey) ) {
            return this.conditionMap.get(conditionKey);
        }
        for ( StateMachineMetaBuilder item : this.compositeStateMachineList ) {
            if ( null != item.getCondtion(conditionKey) ) {
                return item.getCondtion(conditionKey);
            }
        }
        if ( hasSuper() ) {
            return getSuperStateMachine().getCondtion(conditionKey);
        }
        return null;
    }

    @Override
    public boolean hasCondition(Object conditionKey) {
        if ( this.conditionMap.containsKey(conditionKey) ) {
            return true;
        }
        for ( StateMachineMetaBuilder stateMachineMetaBuilder : this.getCompositeStateMachines() ) {
            if ( stateMachineMetaBuilder.hasCondition(conditionKey) ) {
                return true;
            }
        }
        if ( hasSuper() ) {
            return getSuperStateMachine().hasCondition(conditionKey);
        }
        return false;
    }
}
