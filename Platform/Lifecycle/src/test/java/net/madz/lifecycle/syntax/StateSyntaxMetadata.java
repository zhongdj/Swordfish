package net.madz.lifecycle.syntax;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S3.Transitions.Y;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.Conditions.CompareWithZero;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.States.I;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.States.J;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.Transitions.Z;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.Utils.ConcreteCondition;

public class StateSyntaxMetadata extends BaseMetaDataTest {

    @StateMachine
    static interface S1 {

        @StateSet
        static interface States {

            @Initial
            static interface A {}
            @End
            static interface B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface X {}
        }
    }
    @StateMachine
    static interface S2 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = net.madz.lifecycle.syntax.StateSyntaxMetadata.S1.Transitions.X.class,
                    value = { D.class })
            static interface C {}
            @End
            static interface D {}
        }
        @TransitionSet
        static interface Transitions {

            static interface X {}
        }
    }
    @StateMachine
    static interface S3 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Y.class, value = { F.class, G.class })
            static interface E {}
            @Function(transition = Y.class, value = { G.class })
            static interface F {}
            @End
            static interface G {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Y {}
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

            @Conditional(judger = ConcreteCondition.class, condition=CompareWithZero.class)
            static interface Z {}
        }
        @ConditionSet
        public static interface Conditions {

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
