package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.meta.builder.StateMachineInstBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.instance.StateInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata.TransitionTypeEnum;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineInstBuilderImpl extends
        AnnotationMetaBuilderBase<StateMachineInstBuilder, StateMachineMetaBuilder> implements StateMachineInstBuilder {

    public StateMachineInstBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    public StateMachineInstBuilder build(Class<?> klass) throws VerificationException {
        verifySyntax(klass);
        return this;
    }

    private void verifySyntax(Class<?> klass) throws VerificationException {
        verifyTransitons(klass);
    }

    private void verifyTransitons(Class<?> klass) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        final TransitionMetadata[] expectedTransitionSet = this.parent.getTransitionSet();
        final Method[] declaredMethods = klass.getDeclaredMethods();
        final Map<Method, String> actualTranstionMethods = new HashMap<>();
        for ( Method method : declaredMethods ) {
            final Transition annotation = method.getAnnotation(Transition.class);
            if ( null != annotation ) {
                if ( Null.class != annotation.value() ) {
                    actualTranstionMethods.put(method, "TransitionSet." + annotation.value().getSimpleName());
                } else {
                    actualTranstionMethods.put(method, "TransitionSet." + upperFirstChar(method.getName()));
                }
            }
        }
        final Set<Entry<Method, String>> transitionMethodEntries = actualTranstionMethods.entrySet();
        // Make sure all the method's transition can be found in the SM.
        for ( Entry<Method, String> entry : transitionMethodEntries ) {
            TransitionMetadata transition = this.parent.getTransition(entry.getValue());
            Method method = entry.getKey();
            // Method method = actualTranstionMethods.get(transitionClassName);
            if ( null == transition ) {
                if ( method.getAnnotation(Transition.class).value() != Null.class ) {
                    failureSet.add(newVerificationFailure(method.getName(),
                            Errors.LM_TRANSITION_METHOD_WITH_OUTBOUNDED_TRANSITION, klass.getName(), entry.getValue(),
                            method.getName(), this.parent.getDottedPath()));
                } else {
                    failureSet.add(newVerificationFailure(method.getName(), Errors.LM_METHOD_NAME_INVALID,
                            this.parent.getDottedPath(), method.getName(), klass.getName()));
                }
            }
        }
        // Compare whether transitions are covered by methods in LM.
        final Map<TransitionMetadata, Integer> counterMap = new HashMap<>();
        for ( TransitionMetadata transition : expectedTransitionSet ) {
            String expectedTransitionName = transition.getDottedPath().getName();
            for ( Entry<Method, String> entry : transitionMethodEntries ) {
                if ( expectedTransitionName.equals( entry.getValue()) ) {
                    if ( null != counterMap.get(transition) ) {
                        counterMap.put(transition, counterMap.get(transition) + 1);
                    } else {
                        counterMap.put(transition, 1);
                    }
                }
            }
            if ( null == counterMap.get(transition) ) {
                failureSet.add(newVerificationFailure(transition.getDottedPath().getAbsoluteName(),
                        Errors.LM_TRANSITION_NOT_CONCRETED_IN_LM, klass.getSimpleName(), transition.getDottedPath()
                                .getName().split("\\.")[1], this.parent.getDottedPath().getAbsoluteName()));
            }
        }
        Set<Entry<TransitionMetadata, Integer>> entrySet = counterMap.entrySet();
        for ( Entry<TransitionMetadata, Integer> entry : entrySet ) {
            final TransitionMetadata meta = entry.getKey();
            final Integer value = entry.getValue();
            final TransitionTypeEnum type = this.parent.getTransition(meta.getDottedPath()).getType();
            if ( type == TransitionTypeEnum.Corrupt || type == TransitionTypeEnum.Recover
                    || type == TransitionTypeEnum.Redo ) {
                if ( value > 1 ) {
                    failureSet.add(newVerificationFailure(meta.getDottedPath(),
                            Errors.LM_REDO_CORRUPT_RECOVER_TRANSITION_HAS_ONLY_ONE_METHOD, meta.getDottedPath()
                                    .getName(), this.parent.getDottedPath(), klass.getName(), "@"+type.name()));
                }
            }
        }
        // Make sure transition annotated with @Corrupt,or @Redo, or @Recover
        // has only 1 method in LM.
        if ( failureSet.size() > 0 ) {
            throw new VerificationException(failureSet);
        }
    }

    private String upperFirstChar(String name) {
        if ( name.length() > 1 ) {
            return name.substring(0, 1).toUpperCase().concat(name.substring(1));
        } else if ( name.length() == 1 ) {
            return name.toUpperCase();
        }
        return name;
    }

    @Override
    public TransitionInst[] getTransitionSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionInst getTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateInst[] getStateSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateInst getState(Object stateKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Method stateGetter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Method stateSetter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMachineMetadata getTemplate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMachineInstBuilder build(Class<?> klass, StateMachineMetaBuilder parent) throws VerificationException {
        return this;
    }
}
