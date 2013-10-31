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
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS2.States.NCS2_B.CTransitions.NCS2_CX;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS2.Transitions.NCS2_X;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS2.Transitions.NCS2_Y;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS3.States.NCS3_B.CTransitions.NCS3_CX;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS3.Transitions.NCS3_X;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS3.Transitions.NCS3_Y;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS4.States.NCS4_B.CTransitions.NCS4_CX;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS4.Transitions.NCS4_X;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS4.Transitions.NCS4_Y;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NSC1.States.NSC1_B.CTransitions.NSC1_CX;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NSC1.States.NSC1_C;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NSC1.Transitions.NSC1_X;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NSC1.Transitions.NSC1_Y;
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

            @Initial
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
    @StateMachine
    static interface NSC1 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NSC1_X.class, value = NSC1_B.class)
            static interface NSC1_A {}
            @CompositeStateMachine
            @Function(transition = NSC1_Y.class, value = NSC1_C.class)
            static interface NSC1_B {

                @StateSet
                static interface CStates {

                    @Initial
                    @Function(transition = NSC1_CX.class, value = NSC1_CB.class)
                    static interface NSC1_CA {}
                    @Function(transition = NSC1_X.class, value = NSC1_CC.class)
                    static interface NSC1_CB {}
                    @End
                    @ShortCut(NSC1_C.class)
                    static interface NSC1_CC {}
                }
                @TransitionSet
                static interface CTransitions {

                    static interface NSC1_CX {}
                }
            }
            @End
            static interface NSC1_C {}
        }
        @TransitionSet
        static interface Transitions {

            static interface NSC1_X {}
            static interface NSC1_Y {}
        }
    }
    @StateMachine
    static interface NCS2 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NCS2_X.class, value = NCS2_B.class)
            static interface NCS2_A {}
            @CompositeStateMachine
            @Function(transition = NCS2_Y.class, value = NCS2_C.class)
            static interface NCS2_B {

                @StateSet
                static interface CStates {

                    @Initial
                    @Function(transition = NCS2_CX.class, value = NCS2_CC.class)
                    static interface NCS2_CA {}
                    @End
                    @ShortCut(NSC1_C.class)
                    static interface NCS2_CC {}
                }
                @TransitionSet
                static interface CTransitions {

                    static interface NCS2_CX {}
                }
            }
            @End
            static interface NCS2_C {}
        }
        @TransitionSet
        static interface Transitions {

            static interface NCS2_X {}
            static interface NCS2_Y {}
        }
    }
    
    @StateMachine
    static interface NCS3 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NCS3_X.class, value = NCS3_B.class)
            static interface NCS3_A {}
            @CompositeStateMachine
            @Function(transition = NCS3_Y.class, value = NCS3_C.class)
            static interface NCS3_B {

                @StateSet
                static interface CStates {

                    @Initial
                    @Function(transition = NCS3_CX.class, value = NCS3_CB.class)
                    static interface NCS3_CA {}
                    @Function(transition = NCS3_X.class, value = NCS3_CC.class)
                    static interface NCS3_CB {}
                    @End
                    static interface NCS3_CC {}
                }
                @TransitionSet
                static interface CTransitions {

                    static interface NCS3_CX {}
                }
            }
            @End
            static interface NCS3_C {}
        }
        @TransitionSet
        static interface Transitions {

            static interface NCS3_X {}
            static interface NCS3_Y {}
        }
    }
    
    @StateMachine
    static interface NCS4 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NCS4_X.class, value = NCS4_B.class)
            static interface NCS4_A {}
            @CompositeStateMachine
            @Function(transition = NCS4_Y.class, value = NCS4_C.class)
            static interface NCS4_B {

                @StateSet
                static interface CStates {

                    @Initial
                    @Function(transition = NCS4_CX.class, value = NCS4_CB.class)
                    static interface NCS4_CA {}
                    @Function(transition = NCS4_X.class, value = NCS4_CC.class)
                    static interface NCS4_CB {}
                    @ShortCut(NCS4_C.class)
                    static interface NCS4_CC {}
                    @End
                    static interface NCS4_CD {}
                }
                @TransitionSet
                static interface CTransitions {

                    static interface NCS4_CX {}
                }
            }
            @End
            static interface NCS4_C {}
        }
        @TransitionSet
        static interface Transitions {

            static interface NCS4_X {}
            static interface NCS4_Y {}
        }
    }
}
