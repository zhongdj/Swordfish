package net.madz.lifecycle.syntax.lm.callback;

import net.madz.bcel.intercept.LifecycleInterceptor;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.Condition;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.callback.PreStateChange;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.syntax.BaseMetaDataTest;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.Conditions.S1_Condition_A;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.States.S1_State_B;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.States.S1_State_C;

public abstract class CallbackTestBase extends BaseMetaDataTest {

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

            @Conditional(condition = S1_Condition_A.class, judger = VolumeMeasurableTransition.class, postEval = true)
            static interface S1_Transition_X {}
        }
        @ConditionSet
        static interface Conditions {

            static interface S1_Condition_A {

                boolean isVolumeLeft();
            }
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
    @LifecycleMeta(S1.class)
    static class NLM_With_PreStateChange_To_Possible_Next_State {

        /***
         * prestatechange
         * conditional
         * pre-evaluate
         * from
         * ok
         * to
         * ok
         * post-evaluate
         * from
         * ok
         * to
         * ???
         */
        private String state;

        @Transition
        public void s1_Transition_X() {}

        @Condition(S1.Conditions.S1_Condition_A.class)
        public S1_Condition_A getConditionA() {
            return null;
        }

        public String getState() {
            return state;
        }

        @SuppressWarnings("unused")
        private void setState(String state) {
            this.state = state;
        }

        @PreStateChange(to = S1_State_C.class)
        public void interceptStateChange(LifecycleInterceptor<NLM_With_PreStateChange_To_Possible_Next_State> context) {
            System.out.println("The callback method will not be invoked.");
        }
    }
    
    
}
