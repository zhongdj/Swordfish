package net.madz.lifecycle.syntax.lm.callback;

import net.madz.bcel.intercept.LifecycleInterceptor;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.Condition;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.callback.Callbacks;
import net.madz.lifecycle.annotations.callback.PostStateChange;
import net.madz.lifecycle.annotations.callback.PreStateChange;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.syntax.BaseMetaDataTest;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.Conditions.S1_Condition_A;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.States.S1_State_B;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.States.S1_State_C;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S2.Relations.S1Relation;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S2.Transitions.Move;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S3.Conditions.S3_Condition_A;

public abstract class CallbackTestBase extends BaseMetaDataTest {

    @StateMachine
    static interface S1 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.S1_Transition_X.class, value = { S1_State_B.class, S1_State_C.class })
            @Functions({ @Function(transition = Transitions.S1_Transition_X.class, value = { S1_State_B.class, S1_State_C.class }),
                    @Function(transition = Transitions.S1_Transition_Y.class, value = { S1_State_D.class }) })
            static interface S1_State_A {}
            @End
            static interface S1_State_B {}
            @End
            static interface S1_State_C {}
            @End
            static interface S1_State_D {}
        }
        @TransitionSet
        static interface Transitions {

            @Conditional(condition = S1_Condition_A.class, judger = VolumeMeasurableTransition.class, postEval = true)
            static interface S1_Transition_X {}
            static interface S1_Transition_Y {}
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
    @StateMachine
    static interface S2 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Move.class, value = { S2_State_B.class })
            static interface S2_State_A {}
            @InboundWhile(on = { S1.States.S1_State_B.class }, relation = S1Relation.class)
            @End
            static interface S2_State_B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Move {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(S1.class)
            static interface S1Relation {}
        }
    }
    @LifecycleMeta(S1.class)
    static class S1BaseLM {

        private String state;

        @Transition
        public void s1_Transition_X() {}

        @Transition
        public void s1_Transition_Y() {}

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
    }
    @LifecycleMeta(S1.class)
    static class NLM_With_PreStateChange_To_State_With_Post_Evaluate_Non_Relational extends S1BaseLM {

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
        @PreStateChange(to = S1_State_C.class)
        public void interceptStateChange(LifecycleInterceptor<NLM_With_PreStateChange_To_State_With_Post_Evaluate_Non_Relational> context) {
            System.out.println("The callback method will not be invoked.");
        }
    }
    @LifecycleMeta(S1.class)
    static class NLM_prestatechange_from_state_invalid_non_relational extends S1BaseLM {

        @PreStateChange(from = S2.States.S2_State_A.class)
        public void interceptStateChange(LifecycleInterceptor<NLM_prestatechange_from_state_invalid_non_relational> context) {
            System.out.println("The from state is invalid.");
        }
    }
    @LifecycleMeta(S1.class)
    static class NLM_prestatechange_to_state_invalid_non_relational extends S1BaseLM {

        @PreStateChange(to = S2.States.S2_State_A.class)
        public void interceptStateChange(LifecycleInterceptor<NLM_prestatechange_to_state_invalid_non_relational> context) {
            System.out.println("The to state is invalid.");
        }
    }
    @LifecycleMeta(S1.class)
    static class NLM_poststatechange_from_state_invalid_non_relational extends S1BaseLM {

        @PostStateChange(from = S2.States.S2_State_A.class)
        public void interceptStateChange(LifecycleInterceptor<NLM_poststatechange_from_state_invalid_non_relational> context) {
            System.out.println("The from state is invalid.");
        }
    }
    @LifecycleMeta(S1.class)
    static class NLM_poststatechange_to_state_invalid_non_relational extends S1BaseLM {

        @PostStateChange(to = S2.States.S2_State_A.class)
        public void interceptStateChange(LifecycleInterceptor<NLM_poststatechange_to_state_invalid_non_relational> context) {
            System.out.println("The to state is invalid.");
        }
    }
    @LifecycleMeta(S1.class)
    static class NLM_prestatechange_relation_invalid extends S1BaseLM {

        @PreStateChange(to = S1.States.S1_State_A.class, observableName = "s1", mappedBy = "s1")
        public void interceptStateChange(LifecycleInterceptor<NLM_prestatechange_to_state_invalid_non_relational> context) {
            System.out.println("The relation is invalid.");
        }
    }
    @LifecycleMeta(S2.class)
    static class NLM_prestatechange_mappedby_invalid {

        @Relation(S1Relation.class)
        private S1BaseLM s1;
        @StateIndicator
        private String state;

        public NLM_prestatechange_mappedby_invalid() {
            state = S2.States.S2_State_A.class.getSimpleName();
        }

        @Transition
        public void move() {}

        public String getState() {
            return state;
        }

        @PreStateChange(to = S1.States.S1_State_D.class, observableName = "s1", mappedBy = "s1")
        public void interceptStateChange(LifecycleInterceptor<NLM_prestatechange_to_state_invalid_non_relational> context) {
            System.out.println("The mappedBy is invalid.");
        }
    }
    @LifecycleMeta(S1.class)
    static class NLM_poststatechange_relation_invalid extends S1BaseLM {

        @PostStateChange(to = S1.States.S1_State_A.class, observableName = "s1", mappedBy = "s1")
        public void interceptStateChange(LifecycleInterceptor<NLM_prestatechange_to_state_invalid_non_relational> context) {
            System.out.println("The relation is invalid.");
        }
    }
    @LifecycleMeta(S1.class)
    static class NLM_poststatechange_mappedby_invalid extends S1BaseLM {

        @SuppressWarnings("unused")
        private S1BaseLM s1;

        @PostStateChange(to = S2.States.S2_State_A.class, observableName = "s1", mappedBy = "s1")
        public void interceptStateChange(LifecycleInterceptor<NLM_prestatechange_to_state_invalid_non_relational> context) {
            System.out.println("The mappedBy is invalid.");
        }
    }
    @LifecycleMeta(S1.class)
    static class PLM_With_CallBacksWithDefaultValues extends S1BaseLM {

        @Callbacks
        public void interceptStates(LifecycleInterceptor<PLM_With_CallBacksWithDefaultValues> context) {}
    }
    @LifecycleMeta(S1.class)
    static class NLM_With_CallBacksWithInvalidStates extends S1BaseLM {

        @Callbacks(preStateChange = { @PreStateChange(from = S2.States.S2_State_A.class) }, postStateChange = { @PostStateChange(
                to = S2.States.S2_State_A.class) })
        public void interceptStates(LifecycleInterceptor<NLM_With_CallBacksWithInvalidStates> context) {}
    }
    @StateMachine
    static interface S3 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = S3.Transitions.Move.class, value = { S3_State_B.class, S3_State_C.class })
            static interface S3_State_A {}
            @InboundWhile(on = { S1.States.S1_State_B.class }, relation = S3.Relations.S1Relation.class)
            @End
            static interface S3_State_B {}
            @End
            static interface S3_State_C {}
        }
        @TransitionSet
        static interface Transitions {

            @Conditional(condition = S3_Condition_A.class, judger = S3.S3_VolumeMeasurableTransition.class, postEval = true)
            static interface Move {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(S1.class)
            static interface S1Relation {}
        }
        @ConditionSet
        static interface Conditions {

            static interface S3_Condition_A {

                boolean isVolumeLeft();
            }
        }
        public static class S3_VolumeMeasurableTransition implements ConditionalTransition<S3_Condition_A> {

            @Override
            public Class<?> doConditionJudge(S3_Condition_A t) {
                if ( t.isVolumeLeft() ) {
                    return S3.States.S3_State_B.class;
                } else {
                    return S3.States.S3_State_C.class;
                }
            }
        }
    }
    @LifecycleMeta(S1.class)
    public static class NLM_With_PreStateChange_To_State_With_Post_Evaluate_Relational extends S1BaseLM {

        private NLM_With_PreStateChange_To_Possible_Next_State_Relational_Observable s3;

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
        @PreStateChange(to = S3.States.S3_State_C.class, observableName = "s3", mappedBy = "s1")
        public void interceptStateChange(LifecycleInterceptor<NLM_With_PreStateChange_To_State_With_Post_Evaluate_Relational> context) {
            System.out.println("The callback method will not be invoked.");
        }
    }
    @LifecycleMeta(S3.class)
    public static class NLM_With_PreStateChange_To_Possible_Next_State_Relational_Observable implements S3_Condition_A {

        @Relation(S3.Relations.S1Relation.class)
        private NLM_With_PreStateChange_To_State_With_Post_Evaluate_Relational s1;

        @Transition
        public void move() {}

        @StateIndicator
        private String state = S3.States.S3_State_A.class.getSimpleName();

        public String getState() {
            return state;
        }

        @Override
        public boolean isVolumeLeft() {
            return false;
        }

        @Condition(S3.Conditions.S3_Condition_A.class)
        public S3_Condition_A getS3Condition_A() {
            return this;
        }
    }
}
