package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import net.madz.common.DottedPath;
import net.madz.common.Dumper;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.relation.ErrorMessage;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.InboundWhiles;
import net.madz.lifecycle.annotations.relation.ValidWhile;
import net.madz.lifecycle.annotations.relation.ValidWhiles;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Overrides;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.instance.ErrorMessageObject;
import net.madz.lifecycle.meta.instance.FunctionMetadata;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMetaBuilderImpl extends InheritableAnnotationMetaBuilderBase<StateMetadata, StateMachineMetadata>
        implements StateMetaBuilder {

    private boolean end;
    private boolean initial;
    private boolean compositeState;
    private StateMachineMetadata compositeStateMachine;
    private StateMetadata owningState;
    private LinkedList<RelationMetadata> validWhileRelations = new LinkedList<RelationMetadata>();
    private LinkedList<RelationMetadata> inboundWhileRelations = new LinkedList<RelationMetadata>();
    private HashMap<Object, FunctionMetadata> functionMetadataMap = new HashMap<>();
    private ArrayList<TransitionMetadata> possibleTransitionList = new ArrayList<>();
    private ArrayList<FunctionMetadata> functionMetadataList = new ArrayList<>();
    private HashMap<Object, TransitionMetadata> possibleTransitionMap = new HashMap<>();
    private StateMetadata shortcutState;
    private boolean corrupted;

    protected StateMetaBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, "StateSet." + name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasRedoTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionMetadata getRedoTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasRecoverTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionMetadata getRecoverTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasCorruptTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionMetadata getCorruptTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public StateMachineMetadata getStateMachine() {
        return parent;
    }

    @Override
    public String getSimpleName() {
        return getDottedPath().getName();
    }

    @Override
    public boolean isInitial() {
        return initial;
    }

    @Override
    public boolean isFinal() {
        return end;
    }

    @Override
    public TransitionMetadata[] getPossibleTransitions() {
        return this.possibleTransitionList.toArray(new TransitionMetadata[0]);
    }

    @Override
    public FunctionMetadata[] getDeclaredFunctionMetadata() {
        return this.functionMetadataList.toArray(new FunctionMetadata[0]);
    }

    @Override
    public TransitionMetadata getTransition(Object transitionKey) {
        TransitionMetadata transitionMetadata = null;
        if ( this.parent.isComposite() ) {
            transitionMetadata = this.parent.getOwningState().getTransition(transitionKey);
        }
        if ( isOverriding() || !hasSuper() ) {
            if ( null == transitionMetadata ) {
                transitionMetadata = getDeclaredPossibleTransition(transitionKey);
            }
            if ( null == transitionMetadata ) {
                return null;
            } else {
                return transitionMetadata;
            }
        } else {// if ( hasSuper() && !isOverriding() ) {
            if ( null == transitionMetadata ) {
                transitionMetadata = getDeclaredPossibleTransition(transitionKey);
            }
            if ( null != transitionMetadata ) {
                return transitionMetadata;
            } else {
                return this.getSuper().getTransition(transitionKey);
            }
        }
    }

    private TransitionMetadata getDeclaredPossibleTransition(Object transitionKey) {
        return possibleTransitionMap.get(transitionKey);
    }

    @Override
    public boolean isTransitionValid(Object transitionKey) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasInboundWhiles() {
        return null != this.inboundWhileRelations && this.inboundWhileRelations.size() > 0;
    }

    @Override
    public RelationMetadata[] getDeclaredInboundWhiles() {
        return this.inboundWhileRelations.toArray(new RelationMetadata[0]);
    }

    @Override
    public RelationMetadata[] getInboundWhiles() {
        final ArrayList<RelationMetadata> result = new ArrayList<>();
        getInboundWhileRelationMetadataRecursively(this, result);
        return result.toArray(new RelationMetadata[0]);
    }

    @Override
    public boolean hasValidWhiles() {
        return null != this.validWhileRelations && this.validWhileRelations.size() > 0;
    }

    @Override
    public RelationMetadata[] getValidWhiles() {
        final ArrayList<RelationMetadata> result = new ArrayList<>();
        getValidWhileRelationMetadataRecursively(this, result);
        return result.toArray(new RelationMetadata[0]);
    }

    private void getValidWhileRelationMetadataRecursively(StateMetadata stateMetadata,
            ArrayList<RelationMetadata> result) {
        final RelationMetadata[] declaredValidWhiles = stateMetadata.getDeclaredValidWhiles();
        for ( final RelationMetadata relationMetadata : declaredValidWhiles ) {
            result.add(relationMetadata);
        }
        if ( parent.isComposite() ) {
            for ( final RelationMetadata relationMetadata : parent.getOwningState().getValidWhiles() ) {
                result.add(relationMetadata);
            }
        }
        if ( isOverriding() ) {
            return;
        } else {
            if ( null != stateMetadata.getSuper() ) {
                getValidWhileRelationMetadataRecursively(stateMetadata.getSuper(), result);
            }
        }
    }

    private void getInboundWhileRelationMetadataRecursively(StateMetadata stateMetadata,
            ArrayList<RelationMetadata> result) {
        final RelationMetadata[] declaredInboundWhiles = stateMetadata.getDeclaredInboundWhiles();
        for ( final RelationMetadata relationMetadata : declaredInboundWhiles ) {
            result.add(relationMetadata);
        }
        if ( parent.isComposite() ) {
            for ( final RelationMetadata relationMetadata : parent.getOwningState().getInboundWhiles() ) {
                result.add(relationMetadata);
            }
        }
        if ( isOverriding() ) {
            return;
        } else {
            if ( null != stateMetadata.getSuper() ) {
                getValidWhileRelationMetadataRecursively(stateMetadata.getSuper(), result);
            }
        }
    }

    @Override
    public RelationMetadata[] getDeclaredValidWhiles() {
        return this.validWhileRelations.toArray(new RelationMetadata[0]);
    }

    @Override
    public boolean isCompositeState() {
        return compositeState;
    }

    @Override
    public StateMetadata getOwningState() {
        return owningState;
    }

    @Override
    public StateMachineMetadata getCompositeStateMachine() {
        return compositeStateMachine;
    }

    @Override
    public StateMetadata getLinkTo() {
        return shortcutState;
    }

    @Override
    public StateMetaBuilder build(Class<?> clazz, StateMachineMetadata parent) throws VerificationException {
        verifyBasicSyntax(clazz);
        configureSupperState(clazz);
        configureStateType(clazz);
        configureShortcutState(clazz, parent);
        addKeys(clazz);
        return this;
    }

    private void configureStateType(Class<?> clazz) {
        if ( isOverriding() ) {
            for ( Annotation anno : clazz.getDeclaredAnnotations() ) {
                if ( Initial.class == anno.annotationType() ) {
                    this.initial = true;
                } else if ( End.class == anno.annotationType() ) {
                    this.end = true;
                }
            }
        } else {
            if ( null != clazz.getAnnotation(Initial.class) ) {
                this.initial = true;
            } else if ( null != clazz.getAnnotation(End.class) ) {
                this.end = true;
            }
        }
    }

    private void configureShortcutState(Class<?> clazz, StateMachineMetadata parent) {
        if ( !parent.isComposite() ) return;
        if ( !isFinal() ) return;
        final ShortCut shortCut = clazz.getAnnotation(ShortCut.class);
        this.shortcutState = parent.getOwningStateMachine().getState(shortCut.value());
    }

    private void configureSupperState(Class<?> clazz) throws VerificationException {
        if ( clazz.isInterface() ) {
            final Class<?>[] interfaces = clazz.getInterfaces();
            if ( interfaces.length > 0 ) {
                this.setSuper(findStateMetadata(interfaces[0]));
                if ( null == this.getSuper() ) {
                    throw newVerificationException(getDottedPath(),
                            SyntaxErrors.STATE_SUPER_CLASS_IS_NOT_STATE_META_CLASS, clazz, interfaces[0]);
                }
                if ( null != this.getSuper() && null != clazz.getAnnotation(Overrides.class) ) {
                    this.setOverriding(true);
                } else {
                    this.setOverriding(false);
                }
            } else {
                this.setOverriding(false);
                this.setSuper(null);
            }
        } else {
            final Class<?> superStateClass = clazz.getSuperclass();
            if ( null != superStateClass && !Object.class.equals(superStateClass) ) {
                this.setSuper(findStateMetadata(superStateClass));
                if ( null == this.getSuper() ) {
                    throw newVerificationException(getDottedPath(),
                            SyntaxErrors.STATE_SUPER_CLASS_IS_NOT_STATE_META_CLASS, clazz, superStateClass);
                }
            } else {
                this.setSuper(null);
            }
            if ( null != clazz.getAnnotation(Overrides.class) ) {
                this.setOverriding(true);
            } else {
                this.setOverriding(false);
            }
        }
    }

    private void verifyBasicSyntax(Class<?> clazz) throws VerificationException {
        verifyShortcutSyntax(clazz);
    }

    private void verifyShortcutSyntax(Class<?> clazz) throws VerificationException {
        if ( !parent.isComposite() ) {
            return;
        }
        if ( isFinalState(clazz) && !isShortCut(clazz) ) {
            throw newVerificationException(getDottedPath(),
                    SyntaxErrors.COMPOSITE_STATEMACHINE_FINAL_STATE_WITHOUT_SHORTCUT, clazz);
        } else if ( isShortCut(clazz) && !isFinalState(clazz) ) {
            throw newVerificationException(getDottedPath(), SyntaxErrors.COMPOSITE_STATEMACHINE_SHORTCUT_WITHOUT_END,
                    clazz);
        } else if ( isShortCut(clazz) ) {
            final ShortCut shortCut = clazz.getAnnotation(ShortCut.class);
            final Class<?> targetStateClass = shortCut.value();
            StateMetadata found = findStateMetadata(targetStateClass, parent.getOwningStateMachine());
            if ( null == found ) {
                throw newVerificationException(getDottedPath(),
                        SyntaxErrors.COMPOSITE_STATEMACHINE_SHORTCUT_STATE_INVALID, shortCut, clazz, targetStateClass);
            }
        }
    }

    private boolean isShortCut(Class<?> clazz) {
        return null != clazz.getAnnotation(ShortCut.class);
    }

    @Override
    public void configureFunctions(Class<?> stateClass) throws VerificationException {
        for ( Function function : verifyFunctions(stateClass) ) {
            verifyFunction(stateClass, function);
            configureFunction(this, function);
        }
    }

    @Override
    public void configureCompositeStateMachine(Class<?> stateClass) throws VerificationException {
        final CompositeStateMachine csm = stateClass.getAnnotation(CompositeStateMachine.class);
        if ( null == csm ) {
            return;
        }
        this.compositeState = true;
        this.compositeStateMachine = parent.getRegistry().loadStateMachineMetadata(stateClass, parent);
    }

    private ArrayList<Function> verifyFunctions(Class<?> stateClass) throws VerificationException {
        if ( isFinalState(stateClass) ) {
            return new ArrayList<>();
        }
        final ArrayList<Function> functionList = new ArrayList<>();
        final HashSet<Class<?>> transitionClassSet = new HashSet<>();
        if ( null != stateClass.getAnnotation(Function.class) ) {
            final Function function = stateClass.getAnnotation(Function.class);
            addFunction(stateClass, functionList, transitionClassSet, function);
        } else if ( null != stateClass.getAnnotation(Functions.class) ) {
            for ( Function function : stateClass.getAnnotation(Functions.class).value() ) {
                addFunction(stateClass, functionList, transitionClassSet, function);
            }
        }
        if ( 0 == functionList.size() && null != this.getSuper() ) {
            return new ArrayList<>();
        }
        if ( 0 == functionList.size() && !this.compositeState ) {
            throw newVerificationException(getDottedPath().getAbsoluteName(),
                    SyntaxErrors.STATE_NON_FINAL_WITHOUT_FUNCTIONS, stateClass.getName());
        }
        return functionList;
    }

    private void addFunction(Class<?> stateClass, final ArrayList<Function> functionList,
            final HashSet<Class<?>> transitionClassSet, Function function) throws VerificationException {
        if ( transitionClassSet.contains(function.transition()) || !isOverriding()
                && superStateHasFunction(function.transition()) ) {
            throw newVerificationException(getDottedPath(),
                    SyntaxErrors.STATE_DEFINED_MULTIPLE_FUNCTION_REFERRING_SAME_TRANSITION, stateClass,
                    function.transition());
        } else {
            functionList.add(function);
            transitionClassSet.add(function.transition());
        }
    }

    private boolean superStateHasFunction(Class<?> transitionClass) {
        for ( StateMetadata metadata = isOverriding() ? null : getSuper(); null != metadata; metadata = metadata
                .isOverriding() ? null : metadata.getSuper() ) {
            if ( null != metadata.getDeclaredFunctionMetadata(transitionClass) ) {
                return true;
            }
        }
        return false;
    }

    private void configureFunction(StateMetaBuilderImpl parent, Function function) {
        final TransitionMetadata transition = parent.getParent().getTransition(function.transition());
        Class<?>[] value = function.value();
        final LinkedList<StateMetadata> nextStates = new LinkedList<>();
        for ( Class<?> item : value ) {
            nextStates.add(parent.getParent().getState(item));
        }
        final FunctionMetadata functionMetadata = new FunctionMetadata(parent, transition, nextStates);
        this.functionMetadataList.add(functionMetadata);
        this.possibleTransitionList.add(transition);
        final Iterator<Object> iterator = transition.getKeySet().iterator();
        while ( iterator.hasNext() ) {
            final Object next = iterator.next();
            this.functionMetadataMap.put(next, functionMetadata);
            this.possibleTransitionMap.put(next, transition);
        }
    }

    private void verifyFunction(Class<?> stateClass, Function function) throws VerificationException {
        Class<?> transitionClz = function.transition();
        Class<?>[] stateCandidates = function.value();
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        TransitionMetadata transition = parent.getTransition(transitionClz);
        if ( null == transition ) {
            if ( this.parent.isComposite() ) {
                if ( null != this.parent.getOwningStateMachine().getTransition(transitionClz) ) {
                    failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                            SyntaxErrors.FUNCTION_TRANSITION_REFERENCE_BEYOND_COMPOSITE_STATE_SCOPE, function,
                            stateClass.getName(), transitionClz.getName()));
                } else {
                    failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                            SyntaxErrors.FUNCTION_INVALID_TRANSITION_REFERENCE, function, stateClass.getName(),
                            transitionClz.getName()));
                }
            } else {
                failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                        SyntaxErrors.FUNCTION_INVALID_TRANSITION_REFERENCE, function, stateClass.getName(),
                        transitionClz.getName()));
            }
        }
        if ( 0 == stateCandidates.length ) {
            failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                    SyntaxErrors.FUNCTION_WITH_EMPTY_STATE_CANDIDATES, function, stateClass.getName(),
                    transitionClz.getName()));
        } else if ( 1 < stateCandidates.length ) {
            if ( !transition.isConditional() ) {
                failureSet.add(newVerificationFailure(transition.getDottedPath().getAbsoluteName(),
                        SyntaxErrors.FUNCTION_CONDITIONAL_TRANSITION_WITHOUT_CONDITION, function, stateClass.getName(),
                        transitionClz.getName()));
            }
        }
        for ( int i = 0; i < stateCandidates.length; i++ ) {
            final Class<?> stateCandidateClass = stateCandidates[i];
            StateMetadata stateMetadata = findStateMetadata(stateCandidateClass);
            if ( null == stateMetadata ) {
                failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                        SyntaxErrors.FUNCTION_NEXT_STATESET_OF_FUNCTION_INVALID, function, stateClass.getName(), parent
                                .getDottedPath().getAbsoluteName(), stateCandidateClass.getName()));
            }
        }
        if ( 0 < failureSet.size() ) {
            throw new VerificationException(failureSet);
        }
    }

    private StateMetadata findStateMetadata(final Class<?> stateCandidateClass) {
        return findStateMetadata(stateCandidateClass, this.parent);
    }

    private StateMetadata findStateMetadata(final Class<?> stateCandidateClass, StateMachineMetadata stateMachine) {
        StateMetadata stateMetadata = null;
        for ( StateMachineMetadata sm = stateMachine; sm != null && null == stateMetadata; sm = sm
                .getSuperStateMachine() ) {
            stateMetadata = sm.getDeclaredState(stateCandidateClass);
        }
        return stateMetadata;
    }

    private boolean isFinalState(Class<?> stateClass) {
        return null != stateClass.getAnnotation(End.class);
    }

    @Override
    public void configureRelations(Class<?> clazz) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        for ( InboundWhile inboundWhile : findInboundWhiles(clazz) ) {
            verifyInboundWhile(inboundWhile, clazz, failureSet);
        }
        for ( ValidWhile validWhile : findDeclaredValidWhiles(clazz) ) {
            verifyValidWhile(validWhile, clazz, failureSet);
        }
        if ( 0 < failureSet.size() ) {
            throw new VerificationException(failureSet);
        }
        for ( InboundWhile inboundWhile : findInboundWhiles(clazz) ) {
            RelationMetadata relationMetadata = configureRelation(findRelatedStateMachine(inboundWhile.relation()),
                    "InboundWhiles." + inboundWhile.relation().getSimpleName(), inboundWhile.relation(),
                    getOnStates(findRelatedStateMachine(inboundWhile.relation()), inboundWhile.on()),
                    configureErrorMessageObjects(inboundWhile.otherwise(), inboundWhile.relation()));
            this.inboundWhileRelations.add(relationMetadata);
        }
        for ( ValidWhile validWhile : findDeclaredValidWhiles(clazz) ) {
            RelationMetadata relationMetadata = configureRelation(findRelatedStateMachine(validWhile.relation()),
                    "ValidWhiles." + validWhile.relation().getSimpleName(), validWhile.relation(),
                    getOnStates(findRelatedStateMachine(validWhile.relation()), validWhile.on()),
                    configureErrorMessageObjects(validWhile.otherwise(), validWhile.relation()));
            this.validWhileRelations.add(relationMetadata);
        }
    }

    private LinkedList<StateMetadata> getOnStates(StateMachineMetadata stateMachineMetadata, Class<?>[] on) {
        final LinkedList<StateMetadata> onStates = new LinkedList<>();
        for ( Class<?> clz : on ) {
            onStates.add(stateMachineMetadata.getState(clz.getSimpleName()));
        }
        return onStates;
    }

    private RelationMetadata configureRelation(StateMachineMetadata relatedStateMachine, String name,
            Class<?> relationClass, LinkedList<StateMetadata> onStates, LinkedList<ErrorMessageObject> errorObjects)
            throws VerificationException {
        return new RelationMetaBuilderImpl(this, name, onStates, errorObjects, relatedStateMachine).build(
                relationClass, this);
    }

    private LinkedList<ErrorMessageObject> configureErrorMessageObjects(ErrorMessage[] otherwise, Class<?> clz) {
        LinkedList<ErrorMessageObject> errorObjects = new LinkedList<ErrorMessageObject>();
        for ( ErrorMessage item : otherwise ) {
            LinkedList<StateMetadata> errorStates = new LinkedList<>();
            Class<?>[] states = item.states();
            for ( Class<?> stateClz : states ) {
                errorStates.add(this.getParent().getState(stateClz));
            }
            errorObjects.add(new ErrorMessageObject(item.bundle(), clz, item.code(), errorStates
                    .toArray(new StateMetadata[0])));
        }
        return errorObjects;
    }

    private void verifyValidWhile(ValidWhile validWhile, Class<?> clazz, VerificationFailureSet failureSet) {
        final Class<?>[] relatedStateClasses = validWhile.on();
        final Class<?> relationClass = validWhile.relation();
        final ErrorMessage[] errorMessages = validWhile.otherwise();
        verifyRelation(validWhile, relatedStateClasses, relationClass, errorMessages, clazz, failureSet);
    }

    private void verifyInboundWhile(InboundWhile inboundWhile, Class<?> clazz, VerificationFailureSet failureSet) {
        final Class<?>[] relatedStateClasses = inboundWhile.on();
        final Class<?> relationClass = inboundWhile.relation();
        final ErrorMessage[] errorMessages = inboundWhile.otherwise();
        verifyRelation(inboundWhile, relatedStateClasses, relationClass, errorMessages, clazz, failureSet);
    }

    private void verifyRelation(Annotation a, final Class<?>[] relatedStateClasses, final Class<?> relationClass,
            final ErrorMessage[] errorMessages, Class<?> stateClass, VerificationFailureSet failureSet) {
        if ( !hasRelation(relationClass) ) {
            final String errorCode;
            if ( a instanceof InboundWhile ) {
                errorCode = SyntaxErrors.RELATION_INBOUNDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET;
            } else {
                errorCode = SyntaxErrors.RELATION_VALIDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET;
            }
            failureSet.add(newVerificationFailure(getDottedPath(), errorCode, relationClass, stateClass,
                    parent.getDottedPath()));
            return;
        }
        final StateMachineMetadata relatedStateMachine = findRelatedStateMachine(relationClass);
        verifyOnRelatedStates(a, relatedStateClasses, stateClass, failureSet, relatedStateMachine);
        verifyErrorMessages(a, errorMessages, stateClass, failureSet, relatedStateMachine);
    }

    private void verifyOnRelatedStates(Annotation a, final Class<?>[] relatedStateClasses, Class<?> stateClass,
            VerificationFailureSet failureSet, final StateMachineMetadata relatedStateMachine) {
        for ( final Class<?> relateStateClass : relatedStateClasses ) {
            if ( null == findStateMetadata(relateStateClass, relatedStateMachine) ) {
                if ( a instanceof InboundWhile ) {
                    failureSet.add(newVerificationFailure(getInboundWhilePath(relateStateClass),
                            SyntaxErrors.RELATION_ON_ATTRIBUTE_OF_INBOUNDWHILE_NOT_MATCHING_RELATION, a, stateClass,
                            relatedStateMachine.getDottedPath()));
                } else {
                    failureSet.add(newVerificationFailure(getValidWhilePath(relateStateClass),
                            SyntaxErrors.RELATION_ON_ATTRIBUTE_OF_VALIDWHILE_NOT_MACHING_RELATION, a, stateClass,
                            relatedStateMachine.getDottedPath()));
                }
            }
        }
    }

    private DottedPath getValidWhilePath(final Class<?> relateStateClass) {
        return getDottedPath().append(ValidWhile.class.getSimpleName()).append(relateStateClass.getSimpleName());
    }

    private DottedPath getInboundWhilePath(final Class<?> relateStateClass) {
        return getDottedPath().append(InboundWhile.class.getSimpleName()).append(relateStateClass.getSimpleName());
    }

    private void verifyErrorMessages(Annotation a, final ErrorMessage[] errorMessages, Class<?> stateClass,
            VerificationFailureSet failureSet, final StateMachineMetadata relatedStateMachine) {
        for ( ErrorMessage error : errorMessages ) {
            for ( final Class<?> relateStateClass : error.states() ) {
                if ( null == findStateMetadata(relateStateClass, relatedStateMachine) ) {
                    if ( a instanceof InboundWhile ) {
                        failureSet.add(newVerificationFailure(getInboundWhilePath(relateStateClass),
                                SyntaxErrors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID, a, stateClass,
                                relatedStateMachine.getDottedPath()));
                    } else {
                        failureSet.add(newVerificationFailure(getValidWhilePath(relateStateClass),
                                SyntaxErrors.RELATION_OTHERWISE_ATTRIBUTE_OF_VALIDWHILE_INVALID, a, stateClass,
                                relatedStateMachine.getDottedPath()));
                    }
                }
            }
        }
    }

    private boolean hasRelation(final Class<?> relationClass) {
        boolean result = false;
        for ( StateMachineMetadata smd = parent; !result && smd != null; smd = smd.getSuperStateMachine() ) {
            result = smd.hasRelation(relationClass);
            if ( !result && smd.isComposite() ) {
                result = smd.getOwningStateMachine().hasRelation(relationClass);
            }
            if ( result ) {
                return result;
            }
        }
        return result;
    }

    private StateMachineMetadata findRelatedStateMachine(Class<?> relationClass) {
        StateMachineMetadata relatedSm = null;
        for ( StateMachineMetadata smd = parent; relatedSm == null && smd != null; smd = smd.getSuperStateMachine() ) {
            relatedSm = smd.getRelatedStateMachine(relationClass);
            if ( null == relatedSm && getParent().isComposite() ) {
                relatedSm = getParent().getOwningStateMachine().getRelatedStateMachine(relationClass);
            }
        }
        return relatedSm;
    }

    private ArrayList<ValidWhile> findDeclaredValidWhiles(Class<?> clazz) {
        final ArrayList<ValidWhile> validWhileList = new ArrayList<>();
        Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();
        ValidWhiles validWhiles = null;
        ValidWhile validWhile = null;
        for ( Annotation annotation : declaredAnnotations ) {
            if ( ValidWhiles.class == annotation.annotationType() ) {
                validWhiles = (ValidWhiles) annotation;
            }
            if ( ValidWhile.class == annotation.annotationType() ) {
                validWhile = (ValidWhile) annotation;
            }
        }
        if ( null != validWhiles ) {
            for ( ValidWhile valid : validWhiles.value() ) {
                validWhileList.add(valid);
            }
        } else if ( null != validWhile ) {
            validWhileList.add(validWhile);
        }
        return validWhileList;
    }

    private ArrayList<InboundWhile> findInboundWhiles(Class<?> clazz) {
        final ArrayList<InboundWhile> inboundWhileList = new ArrayList<>();
        final InboundWhiles inboundWhiles = clazz.getAnnotation(InboundWhiles.class);
        final InboundWhile inboundWhile = clazz.getAnnotation(InboundWhile.class);
        if ( null != inboundWhiles ) {
            for ( InboundWhile inbound : inboundWhiles.value() ) {
                inboundWhileList.add(inbound);
            }
        } else if ( null != inboundWhile ) {
            inboundWhileList.add(inboundWhile);
        }
        return inboundWhileList;
    }

    @Override
    public FunctionMetadata getDeclaredFunctionMetadata(Object functionKey) {
        return this.functionMetadataMap.get(functionKey);
    }

    @Override
    public boolean hasMultipleStateCandidatesOn(Object transitionKey) {
        FunctionMetadata functionMetadata = null;
        if ( parent.isComposite() ) {
            functionMetadata = parent.getOwningState().getDeclaredFunctionMetadata(transitionKey);
        }
        if ( isOverriding() || !hasSuper() ) {
            if ( null == functionMetadata ) {
                functionMetadata = getDeclaredFunctionMetadata(transitionKey);
            }
            if ( null == functionMetadata ) {
                throw new IllegalArgumentException("Invalid Key or Key not registered: " + transitionKey);
            }
            if ( 1 < functionMetadata.getNextStates().size() ) {
                return true;
            } else {
                return false;
            }
        } else {// if ( hasSuper() && !isOverriding() ) {
            if ( null == functionMetadata ) {
                functionMetadata = this.getDeclaredFunctionMetadata(transitionKey);
            }
            if ( null != functionMetadata ) {
                if ( functionMetadata.getNextStates().size() > 1 ) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return this.getSuper().hasMultipleStateCandidatesOn(transitionKey);
            }
        }
    }

    public FunctionMetadata getFunctionMetadata(Object functionKey) {
        FunctionMetadata functionMetadata = null;
        if ( parent.isComposite() ) {
            functionMetadata = parent.getOwningState().getDeclaredFunctionMetadata(functionKey);
            if ( null != functionMetadata ) {
                return functionMetadata;
            }
        }
        if ( isOverriding() || !hasSuper() ) {
            return getDeclaredFunctionMetadata(functionKey);
        } else {// if ( hasSuper() && !isOverriding() ) {
            functionMetadata = this.getDeclaredFunctionMetadata(functionKey);
            if ( null != functionMetadata ) {
                return functionMetadata;
            } else {
                return this.getSuper().getFunctionMetadata(functionKey);
            }
        }
    }
}
