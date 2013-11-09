package net.madz.lifecycle.syntax.basic;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.syntax.BaseMetaDataTest;
import net.madz.lifecycle.syntax.basic.ConditionSetTest.S4.Conditions.CompareWithZero;
import net.madz.lifecycle.syntax.basic.ConditionSetTest.S4.States.I;
import net.madz.lifecycle.syntax.basic.ConditionSetTest.S4.States.J;
import net.madz.lifecycle.syntax.basic.ConditionSetTest.S4.Transitions.Z;
import net.madz.lifecycle.syntax.basic.ConditionSetTest.S4.Utils.ConcreteCondition;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class ConditionSetTest extends BaseMetaDataTest {

    @Test(expected = VerificationException.class)
    public void test_multiple_condition_set() throws VerificationException {
        @LifecycleRegistry(S4.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), SyntaxErrors.CONDITIONSET_MULTIPLE, S4.class);
            throw e;
        }
    }

    @StateMachine
    static interface S4 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Z.class, value = { I.class, J.class })
            static interface H {}
            @Function(transition = Z.class, value = { I.class, J.class })
            static interface I {}
            @End
            static interface J {}
        }
        @TransitionSet
        static interface Transitions {

            @Conditional(judger = ConcreteCondition.class, condition = CompareWithZero.class)
            static interface Z {}
        }
        @ConditionSet
        public static interface Conditions {

            public static interface CompareWithZero {

                int intValue();
            }
        }
        @ConditionSet
        public static interface Conditions2 {

            public static interface CompareWithZero {

                int intValue();
            }
        }
        public static class Utils {

            public static class ConcreteCondition implements ConditionalTransition<CompareWithZero> {

                @Override
                public Class<?> doConditionJudge(CompareWithZero t) {
                    if ( t.intValue() > 0 ) {
                        return I.class;
                    } else {
                        return J.class;
                    }
                }
            }
        }
    }
}
