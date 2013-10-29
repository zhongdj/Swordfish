package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import net.madz.common.Dumper;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;
import net.madz.verification.VerificationFailureSet;

public class StateMachineMetaBuilderImpl extends AnnotationBasedMetaBuilder<StateMachineMetadata, StateMachineMetadata>
        implements StateMachineMetaBuilder {

    public StateMachineMetaBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, name);
    }

    public StateMachineMetaBuilderImpl(String name) {
        this(null, name);
    }

    @Override
    public boolean hasSuper() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMachineMetadata getSuperStateMachine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasParent() {
        // TODO Auto-generated method stub
        return false;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata getState(Object stateKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata getInitialState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata[] getFinalStates() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata[] getTransitionSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata getTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata getStateSynchronizationTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isComposite() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMachineMetadata getOwningStateMachine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata getCompositeState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata[] getShortcutStateSet() {
        // TODO Auto-generated method stub
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
    public StateMachineMetaBuilder build(Class<?> clazz) throws VerificationException {
        verifySyntax(clazz);
        return this;
    }

    private void verifySyntax(Class<?> clazz) throws VerificationException {
        verifyStateMachineDefinition(clazz);
        verifyRequiredComponents(clazz);
    }

    private void verifyRequiredComponents(Class<?> clazz) throws VerificationException {
        final String stateSetPath = clazz.getName() + ".StateSet";
        final String transitionSetPath = clazz.getName() + ".TransitionSet";
        if ( hasSuper(clazz) ) {
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

    private boolean hasSuper(Class<?> clazz) {
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
        }
    }

    private VerificationException newVerificationException(String dottedPathName, String errorCode, Object[] args) {
        return new VerificationException(new VerificationFailure(this, dottedPathName, errorCode,
                BundleUtils.getBundledMessage(getClass(), Errors.SYNTAX_ERROR_BUNDLE, errorCode, args)));
    }
}
