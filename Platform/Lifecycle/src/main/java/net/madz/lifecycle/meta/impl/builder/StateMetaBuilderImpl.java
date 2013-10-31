package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

import net.madz.common.DottedPath;
import net.madz.common.Dumper;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.relation.ErrorMessage;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.InboundWhiles;
import net.madz.lifecycle.annotations.relation.ValidWhile;
import net.madz.lifecycle.annotations.relation.ValidWhiles;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMetaBuilderImpl extends AnnotationMetaBuilderBase<StateMetaBuilder, StateMachineMetaBuilder>
        implements StateMetaBuilder {

    private boolean end;
    private boolean initial;
    private boolean compositeState;
    private StateMachineMetaBuilder compositeStateMachine;
    private StateMetadata owningState;

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
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public StateMachineMetaBuilder getStateMachine() {
        return parent;
    }

    @Override
    public String getSimpleName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata onTransition(TransitionMetadata transition) {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata getTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isTransitionValid(Object transitionKey) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isOverriding() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMetadata getSuperStateMetadata() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasInboundWhiles() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RelationMetadata[] getInboundWhiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasValidWhiles() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RelationMetadata[] getValidWhiles() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent) throws VerificationException {
        verifyBasicSyntax(clazz);
        addKeys(clazz);
        return this;
    }

    private void verifyBasicSyntax(Class<?> clazz) throws VerificationException {
        verifyShortcutSyntax(clazz);
    }

    private void verifyShortcutSyntax(Class<?> clazz) throws VerificationException {
        if ( !parent.isComposite() ) {
            return;
        }
        if ( isFinalState(clazz) && !isShortCut(clazz) ) {
            throw newVerificationException(getDottedPath(), Errors.COMPOSITE_STATEMACHINE_FINAL_STATE_WITHOUT_SHORTCUT,
                    clazz);
        } else if ( isShortCut(clazz) && !isFinalState(clazz) ) {
            throw newVerificationException(getDottedPath(), Errors.COMPOSITE_STATEMACHINE_SHORTCUT_WITHOUT_END, clazz);
        } else if ( isShortCut(clazz) ) {
            final ShortCut shortCut = clazz.getAnnotation(ShortCut.class);
            final Class<?> targetStateClass = shortCut.value();
            StateMetadata found = findStateMetadata(targetStateClass, parent.getOwningStateMachine());
            if ( null == found ) {
                throw newVerificationException(getDottedPath(), Errors.COMPOSITE_STATEMACHINE_SHORTCUT_STATE_INVALID,
                        shortCut, clazz, targetStateClass);
            }
        }
    }

    private boolean isShortCut(Class<?> clazz) {
        return null != clazz.getAnnotation(ShortCut.class);
    }

    @Override
    public void configureFunctions(Class<?> clazz) throws VerificationException {
        verifyFunctions(clazz);
    }

    @Override
    public void configureCompositeStateMachine(Class<?> stateClass) throws VerificationException {
        final CompositeStateMachine csm = stateClass.getAnnotation(CompositeStateMachine.class);
        if ( null == csm ) {
            return;
        }
        final StateMachineMetaBuilder compositeStateMachine = new StateMachineMetaBuilderImpl(this.parent,
                "CompositeStateMachine." + stateClass.getSimpleName());
        compositeStateMachine.setComposite(true);
        compositeStateMachine.setOwningState(this);
        this.compositeState = true;
        this.compositeStateMachine = compositeStateMachine;
        compositeStateMachine.build(stateClass, this.parent);
    }

    private void verifyFunctions(Class<?> stateClass) throws VerificationException {
        if ( isFinalState(stateClass) ) {
            return;
        }
        final ArrayList<Function> functionList = new ArrayList<>();
        if ( null != stateClass.getAnnotation(Function.class) ) {
            functionList.add(stateClass.getAnnotation(Function.class));
        } else if ( null != stateClass.getAnnotation(Functions.class) ) {
            for ( Function function : stateClass.getAnnotation(Functions.class).value() ) {
                functionList.add(function);
            }
        }
        if ( 0 == functionList.size() ) {
            throw newVerificationException(getDottedPath().getAbsoluteName(), Errors.STATE_NON_FINAL_WITHOUT_FUNCTIONS,
                    stateClass.getName());
        }
        for ( Function function : functionList ) {
            verifyFunction(stateClass, function);
        }
    }

    private void verifyFunction(Class<?> stateClass, Function function) throws VerificationException {
        Class<?> transitionClz = function.transition();
        Class<?>[] stateCandidates = function.value();
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        TransitionMetadata transition = findTransitionMetadata(transitionClz);
        if ( null == transition ) {
            if ( this.parent.isComposite() ) {
                if ( null != findTransitionMetadata(transitionClz, this.parent.getOwningStateMachine()) ) {
                    failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                            Errors.FUNCTION_TRANSITION_REFERENCE_BEYOND_COMPOSITE_STATE_SCOPE, function,
                            stateClass.getName(), transitionClz.getName()));
                } else {
                    failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                            Errors.FUNCTION_INVALID_TRANSITION_REFERENCE, function, stateClass.getName(),
                            transitionClz.getName()));
                }
            } else {
                failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                        Errors.FUNCTION_INVALID_TRANSITION_REFERENCE, function, stateClass.getName(),
                        transitionClz.getName()));
            }
        }
        if ( 0 == stateCandidates.length ) {
            failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                    Errors.FUNCTION_WITH_EMPTY_STATE_CANDIDATES, function, stateClass.getName(),
                    transitionClz.getName()));
        } else if ( 1 < stateCandidates.length ) {
            if ( !transition.isConditional() ) {
                failureSet.add(newVerificationFailure(transition.getDottedPath().getAbsoluteName(),
                        Errors.FUNCTION_CONDITIONAL_TRANSITION_WITHOUT_CONDITION, function, stateClass.getName(),
                        transitionClz.getName()));
            }
        }
        for ( int i = 0; i < stateCandidates.length; i++ ) {
            final Class<?> stateCandidateClass = stateCandidates[i];
            StateMetadata stateMetadata = findStateMetadata(stateCandidateClass);
            if ( null == stateMetadata ) {
                failureSet.add(newVerificationFailure(getDottedPath().getAbsoluteName(),
                        Errors.FUNCTION_NEXT_STATESET_OF_FUNCTION_INVALID, function, stateClass.getName(), parent
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
            stateMetadata = sm.getState(stateCandidateClass);
        }
        return stateMetadata;
    }

    private TransitionMetadata findTransitionMetadata(Class<?> transitionClz) {
        return findTransitionMetadata(transitionClz, this.parent);
    }

    private TransitionMetadata findTransitionMetadata(Class<?> transitionClz, StateMachineMetadata stateMachine) {
        TransitionMetadata transition = null;
        for ( StateMachineMetadata sm = stateMachine; sm != null && null == transition; sm = sm.getSuperStateMachine() ) {
            transition = sm.getTransition(transitionClz);
        }
        return transition;
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
        for ( ValidWhile validWhile : findValidWhiles(clazz) ) {
            verifyValidWhile(validWhile, clazz, failureSet);
        }
        if ( 0 < failureSet.size() ) {
            throw new VerificationException(failureSet);
        }
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
            failureSet.add(newVerificationFailure(getDottedPath(),
                    Errors.RELATION_INBOUNDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET, relationClass, stateClass,
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
                            Errors.RELATION_ON_ATTRIBUTE_OF_INBOUNDWHILE_NOT_MATCHING_RELATION, a, stateClass,
                            relatedStateMachine.getDottedPath()));
                } else {
                    failureSet.add(newVerificationFailure(getValidWhilePath(relateStateClass),
                            Errors.RELATION_ON_ATTRIBUTE_OF_VALIDWHILE_NOT_MACHING_RELATION, a, stateClass,
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
                                Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID, a, stateClass,
                                relatedStateMachine.getDottedPath()));
                    } else {
                        failureSet.add(newVerificationFailure(getValidWhilePath(relateStateClass),
                                Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_VALIDWHILE_INVALID, a, stateClass,
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
        }
        return result;
    }

    private StateMachineMetadata findRelatedStateMachine(Class<?> relationClass) {
        StateMachineMetadata relatedSm = null;
        for ( StateMachineMetadata smd = parent; relatedSm == null && smd != null; smd = smd.getSuperStateMachine() ) {
            relatedSm = smd.getRelatedStateMachine(relationClass);
        }
        return relatedSm;
    }

    private ArrayList<ValidWhile> findValidWhiles(Class<?> clazz) {
        final ArrayList<ValidWhile> validWhileList = new ArrayList<>();
        final ValidWhiles validWhiles = clazz.getAnnotation(ValidWhiles.class);
        final ValidWhile validWhile = clazz.getAnnotation(ValidWhile.class);
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
}
