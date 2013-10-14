package net.madz.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.eclipsesource.restfuse.HttpOrderComparator;
import com.eclipsesource.restfuse.annotation.HttpTest;

public class MadzHttpUnitRunner extends MadzTestRunner {

    public MadzHttpUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        ArrayList<FrameworkMethod> result = new ArrayList<FrameworkMethod>();
        result.addAll(getTestClass().getAnnotatedMethods(HttpTest.class));
        List<FrameworkMethod> testAnnotatedMethods = getTestClass().getAnnotatedMethods(Test.class);
        for ( FrameworkMethod method : testAnnotatedMethods ) {
            if ( !result.contains(method) ) {
                result.add(method);
            }
        }
        Collections.sort(result, new HttpOrderComparator());
        return result;
    }
}
