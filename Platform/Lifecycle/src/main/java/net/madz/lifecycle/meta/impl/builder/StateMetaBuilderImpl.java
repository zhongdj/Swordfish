package net.madz.lifecycle.meta.impl.builder;

import java.util.ArrayList;

import net.madz.common.Dumper;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
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

public class StateMetaBuilderImpl extends AnnotationBasedMetaBuilder<StateMetaBuilder, StateMachineMetaBuilder>
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
        final StateMachineMetaBuilder compositeStateMachine = new StateMachineMetaBuilderImpl("CompositeStateMachine."
                + stateClass.getSimpleName());
        compositeStateMachine.setComposite(true);
        compositeStateMachine.setOwningState(this);
        this.compositeState = true;
        this.compositeStateMachine = compositeStateMachine;
        compositeStateMachine.build(stateClass);
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
}
