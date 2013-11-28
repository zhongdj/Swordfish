package net.madz.lifecycle.meta.impl.builder.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.util.MethodScanCallback;
import net.madz.util.StringUtil;

public final class TransitionMethodScanner implements MethodScanCallback {

    private final TransitionMetadata transition;

    public TransitionMethodScanner(final TransitionMetadata transition) {
        this.transition = transition;
    }

    private ArrayList<Method> transitionMethodList = new ArrayList<Method>();

    @Override
    public boolean onMethodFound(Method method) {
        final Transition transitionAnno = method.getAnnotation(Transition.class);
        if ( null != transitionAnno ) {
            if ( Null.class != transitionAnno.value() ) {
                if ( transitionAnno.value().getSimpleName().equals(transition.getDottedPath().getName()) ) {
                    transitionMethodList.add(method);
                }
            } else {
                if ( StringUtil.toUppercaseFirstCharacter(method.getName()).equals(transition.getDottedPath().getName()) ) {
                    transitionMethodList.add(method);
                }
            }
        }
        return false;
    }

    public Method[] getTransitionMethods() {
        return transitionMethodList.toArray(new Method[0]);
    }
}