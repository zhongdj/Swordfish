package net.madz.lifecycle.syntax;

import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.relation.ValidWhile;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.R1_S.Transitions.R1_S_X;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.R2_S.Transitions.R2_S_X;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.R3_S.Transitions.R3_S_X;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.S4.Relations.R1;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.S5.States.S5_B.S5_B_Relations.S5_B_R1;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.S5.States.S5_B.S5_B_Transitions.S5_B_X;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.S5.Transitions.S5_X;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.S6.Relations.S6_R1;
import net.madz.lifecycle.syntax.LMSyntaxRelationMetadata.S6.Relations.S6_R2;

public class LMSyntaxRelationMetadata {

    // Positive LM: Concrete all relations in SM
    @StateMachine
    static interface R1_S {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = R1_S_X.class, value = { R1_S_B.class })
            static interface R1_S_A {}
            @End
            static interface R1_S_B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface R1_S_X {}
        }
    }
    @LifecycleMeta(R1_S.class)
    static interface PLM_R1_S {

        @Transition(R1_S_X.class)
        void tm();
    }
    @StateMachine
    static interface R2_S {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = R2_S_X.class, value = { R2_S_B.class })
            static interface R2_S_A {}
            @End
            static interface R2_S_B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface R2_S_X {}
        }
    }
    @LifecycleMeta(R2_S.class)
    static interface PLM_R2_S {

        @Transition(R2_S_X.class)
        void tm();
    }
    @StateMachine
    static interface R3_S {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = R3_S_X.class, value = { R3_S_B.class })
            static interface R3_S_A {}
            @End
            static interface R3_S_B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface R3_S_X {}
        }
    }
    @LifecycleMeta(R3_S.class)
    static interface PLM_R3_S {

        @Transition(R3_S_X.class)
        void tm();
    }
    @StateMachine
    static interface S4 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = S4.Transitions.X.class, value = { S4_B.class })
            static interface S4_A {}
            @InboundWhile(on = { R1_S.States.R1_S_A.class }, relation = S4.Relations.R1.class)
            @ValidWhile(on = { R2_S.States.R2_S_A.class }, relation = S4.Relations.R2.class)
            @Function(transition = S4.Transitions.Y.class, value = { S4_C.class })
            static interface S4_B {}
            @Function(transition = S4.Transitions.Z.class, value = { S4_D.class })
            @ValidWhile(relation = S4.Relations.R3.class, on = { R3_S.States.R3_S_A.class })
            static interface S4_C {}
            @End
            static interface S4_D {}
        }
        @TransitionSet
        static interface Transitions {

            static interface X {}
            static interface Y {}
            static interface Z {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(R1_S.class)
            static interface R1 {}
            @RelateTo(R2_S.class)
            static interface R2 {}
            @RelateTo(R3_S.class)
            static interface R3 {}
        }
    }
    @LifecycleMeta(S4.class)
    static interface PLM_5 {

        @Transition(S4.Transitions.X.class)
        void tM1(@Relation(R1.class) PLM_R1_S x);

        @Transition(S4.Transitions.Y.class)
        void tM2();

        @Transition(S4.Transitions.Z.class)
        void tM3();

        @Relation(S4.Relations.R2.class)
        PLM_R2_S r2 = null;

        @Relation(S4.Relations.R3.class)
        PLM_R3_S getR3S();
    }
    // Positive LM: Concrete all relations in SM that contains
    // CompositeStateMachines
    @StateMachine
    static interface S5 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = S5_X.class, value = { S5_B.class })
            static interface S5_A {}
            @CompositeStateMachine
            static interface S5_B {

                @StateSet
                static interface S5_B_States {

                    @Initial
                    @Function(transition = S5_B_X.class, value = { S5_B_B.class })
                    @ValidWhile(on = { R1_S.States.R1_S_A.class }, relation = S5_B_R1.class)
                    static interface S5_B_A {}
                    @End
                    @ShortCut(value = S5_C.class)
                    static interface S5_B_B {}
                }
                @TransitionSet
                static interface S5_B_Transitions {

                    static interface S5_B_X {}
                }
                @RelationSet
                static interface S5_B_Relations {

                    @RelateTo(R1_S.class)
                    static interface S5_B_R1 {};
                }
            }
            @End
            static interface S5_C {}
        }
        @TransitionSet
        static interface Transitions {

            static interface S5_X {}
            static interface S5_Y {}
        }
    }
    @LifecycleMeta(S5.class)
    static interface PLM_6 {

        @Transition
        void s5_X();

        @Transition
        void s5_Y();

        @Transition
        void s5_B_X();

        @Relation(S5_B_R1.class)
        PLM_R1_S r1_S = null;
    }
    // Positive LM: Concrete all relations in SM that has super StateMachines.
    @StateMachine
    static interface S6 extends S5 {

        @StateSet
        static interface States extends S5.States {

            @ValidWhile(on = { R1_S.States.R1_S_A.class }, relation = S6_R1.class)
            static interface S6_A extends S5_A {}
            @InboundWhile(on = { R2_S.States.R2_S_A.class }, relation = S6_R2.class)
            static interface S6_B extends S5_B {}
        }
        @TransitionSet
        static interface Transitons extends S5.Transitions {}
        @RelationSet
        static interface Relations {

            @RelateTo(R1_S.class)
            static interface S6_R1 {}
            @RelateTo(R2_S.class)
            static interface S6_R2 {}
        }
    }
    @LifecycleMeta(S6.class)
    static interface PLM_7 {

        @Transition
        void s5_X();

        @Transition
        void s5_Y(@Relation(R2_S.class) PLM_R2_S rs_S);

        @Transition
        void s5_B_X();

        @Relation(S5_B_R1.class)
        PLM_R1_S r1_S = null;
    }
}
