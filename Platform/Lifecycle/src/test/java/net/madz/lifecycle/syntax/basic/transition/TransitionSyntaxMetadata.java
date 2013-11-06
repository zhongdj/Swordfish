package net.madz.lifecycle.syntax.basic.transition;

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
import net.madz.lifecycle.syntax.basic.transition.TransitionSyntaxMetadata.S1.Conditions.S1_Condition_A;
import net.madz.lifecycle.syntax.basic.transition.TransitionSyntaxMetadata.S1.Conditions.S1_Condition_B;
import net.madz.lifecycle.syntax.basic.transition.TransitionSyntaxMetadata.S1.States.S1_State_B;
import net.madz.lifecycle.syntax.basic.transition.TransitionSyntaxMetadata.S1.States.S1_State_C;

public class TransitionSyntaxMetadata extends BaseMetaDataTest {

    @StateMachine
    static interface S1 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.S1_Transition_X.class, value = { S1_State_B.class, S1_State_C.class })
            static interface S1_State_A {}
            @End
            static interface S1_State_B {}
            @End
            static interface S1_State_C {}
        }
        @TransitionSet
        static interface Transitions {

            @Conditional(condition = S1_Condition_B.class, judger = VolumeMeasurableTransition.class)
            static interface S1_Transition_X {}
        }
        @ConditionSet
        static interface Conditions {

            static interface S1_Condition_A {

                boolean isVolumeLeft();
            }
            static interface S1_Condition_B {}
        }
        public static class VolumeMeasurableTransition implements ConditionalTransition<S1_Condition_A> {

            @Override
            public Class<?> doConditionJudge(S1_Condition_A t) {
                if ( t.isVolumeLeft() ) {
                    return S1_State_B.class;
                } else {
                    return S1_State_C.class;
                }
            }
        }
    }
}
