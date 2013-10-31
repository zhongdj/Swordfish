package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class RelationSyntaxPositiveTest extends RelationSyntaxMetadata {

    @Test
    public void test_relation_syntax_positive_standalone() throws VerificationException {
        @LifecycleRegistry(PStandalone.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        
        new Registry();
    }
    
    
}
