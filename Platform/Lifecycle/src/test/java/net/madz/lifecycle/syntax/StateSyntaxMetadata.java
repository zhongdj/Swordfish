package net.madz.lifecycle.syntax;

import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.PCS1.States.PCS1_B.CTransitions.PCS1_CX;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.PCS1.Transitions.PCS1_X;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.PCS1.Transitions.PCS1_Y;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S2.States.D;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S3.Transitions.Y;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.Conditions.CompareWithZero;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.States.I;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.States.J;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.Transitions.Z;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S4.Utils.ConcreteCondition;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S5.Transitions.S5_Start;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S5_Super.Transitions.S5_Super_Start;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S6.Transitions.S6_Start;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S7.Transitions.S7_X;

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

            @Conditional(judger = ConcreteCondition.class, condition = CompareWithZero.class)
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
    @StateMachine
    static interface S5_Super {

        @StateSet
        static interface States {

            @Function(transition = S5_Super_Start.class, value = { S5_Super_B.class })
            static interface S5_Super_A {}
            @End
            static interface S5_Super_B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface S5_Super_Start {}
        }
    }
    @StateMachine
    static interface S5 extends S5_Super {

        @StateSet
        static interface states extends S5_Super.States {

            @Function(transition = S5_Start.class, value = { D.class })
            static interface S5_A {}
        }
        @TransitionSet
        static interface Transitions {

            static interface S5_Start {}
        }
    }
    @StateMachine
    static interface S6 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = S6_Start.class, value = { S6_B.class })
            static interface S6_A {}
            @End
            static interface S6_B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface S6_Start {}
        }
    }
    @StateMachine
    static interface S7 extends S6 {

        @StateSet
        static interface States extends S6.States {

            @Function(transition = S7_X.class, value = { S6_B.class })
            static interface S7_A {}
        }
        @TransitionSet
        static interface Transitions extends S6.Transitions {

            static interface S7_X {}
        }
    }
    @StateMachine
    static interface PCS1 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = PCS1_X.class, value = PCS1_B.class)
            static interface PCS1_A {}
            @CompositeStateMachine
            @Function(transition = PCS1_Y.class, value = PCS1_C.class)
            static interface PCS1_B {

                @StateSet
                static interface CStates {

                    @Initial
                    @Function(transition = PCS1_CX.class, value = PCS1_CB.class)
                    static interface PCS1_CA {}
                    @Function(transition = PCS1_CX.class, value = PCS1_CC.class)
                    static interface PCS1_CB {}
                    @End
                    @ShortCut(PCS1_C.class)
                    static interface PCS1_CC {}
                }
                @TransitionSet
                static interface CTransitions {

                    static interface PCS1_CX {}
                }
            }
            @End
            static interface PCS1_C {}
        }
        @TransitionSet
        static interface Transitions {

            static interface PCS1_X {}
            static interface PCS1_Y {}
        }
    }
}
