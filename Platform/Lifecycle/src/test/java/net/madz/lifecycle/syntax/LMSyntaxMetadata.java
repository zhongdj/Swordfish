package net.madz.lifecycle.syntax;

import net.madz.lifecycle.StateConverter;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.Corrupt;
import net.madz.lifecycle.annotations.action.Recover;
import net.madz.lifecycle.annotations.action.Redo;
import net.madz.lifecycle.annotations.state.Converter;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.PS1.Transitions.S1_X;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.S2.Transitions.NS1_X;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.S2.Transitions.NS1_Y;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.S2.Transitions.NS1_Z;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.S3.Transitions.S3_X;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.S3.Transitions.S3_Y;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.S3.Transitions.S3_Z;

public class LMSyntaxMetadata extends BaseMetaDataTest {

    @StateMachine
    static interface PS1 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = S1_X.class, value = { S1_B.class })
            static interface S1_A {}
            @End
            static interface S1_B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface S1_X {}
        }
    }
    @LifecycleMeta(PS1.class)
    static interface PLM_1 {

        @Transition(S1_X.class)
        void test();
    }
    @StateMachine
    static interface S2 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NS1_X.class, value = { NS1_B.class })
            static interface NS1_A {}
            @Function(transition = NS1_Y.class, value = { NS1_C.class })
            static interface NS1_B {}
            @Function(transition = NS1_Z.class, value = { NS1_C.class })
            static interface NS1_C {}
            @End
            static interface NS1_D {}
        }
        @TransitionSet
        static interface Transitions {

            static interface NS1_X {}
            static interface NS1_Y {}
            static interface NS1_Z {}
        }
    }
    // Positive: all transitions are covered by methods
    @LifecycleMeta(S2.class)
    static interface PLM_2 {

        @Transition(NS1_X.class)
        void m1();

        @Transition(NS1_Y.class)
        void m2();

        @Transition(NS1_Z.class)
        void m3();
    }
    // Positive: all transitions are covered by methods, using default method
    // name
    @LifecycleMeta(S2.class)
    static interface PLM_3 {

        @Transition
        public void nS1_X();

        @Transition
        public void nS1_Y();

        @Transition
        public void nS1_Z();
    }
    // Transition NS1_Z has no binding method in LM
    @LifecycleMeta(S2.class)
    static interface NLM_1 {

        @Transition(NS1_X.class)
        public void m1();

        @Transition(NS1_Y.class)
        public void m2();
    }
    // Transition NS1_Z has no method in LM
    @LifecycleMeta(S2.class)
    static interface NLM_2 {

        @Transition(NS1_X.class)
        public void m1();

        @Transition(NS1_X.class)
        public void m2();

        @Transition(NS1_Y.class)
        public void m3();
    }
    @LifecycleMeta(S2.class)
    static interface NLM_3 {

        @Transition
        public void nS1_Xyz(); // Method nS1_Xyz can not bind to any transition
                               // in S2.

        @Transition
        public void nS1_X();

        @Transition
        public void nS1_Y();

        @Transition
        public void nS1_Z();
    }
    @LifecycleMeta(S2.class)
    static interface NLM_4 {

        // Use other state machine's transition
        @Transition(S1_X.class)
        public void nS1_X();

        @Transition
        public void nS1_Y();

        @Transition
        public void nS1_Z();
    }
    @StateMachine
    static interface S3 {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = S3_X.class, value = { S3_B.class })
            static interface S3_A {}
            @Function(transition = S3_Y.class, value = { S3_C.class })
            static interface S3_B {}
            @Function(transition = S3_Z.class, value = { S3_D.class })
            static interface S3_C {}
            @End
            static interface S3_D {}
        }
        @TransitionSet
        static interface Transitions {

            @Corrupt
            static interface S3_X {}
            @Redo
            static interface S3_Y {}
            @Recover
            static interface S3_Z {}
        }
    }
    // Positive LM: Corrupt, Redo, Recover transition can bind to only 1 method.
    @LifecycleMeta(S3.class)
    static interface PLM_4 {

        @Transition
        void s3_X();

        @Transition
        void s3_Y();

        @Transition
        void s3_Z();
    }
    // Negative LM: Redo transition binds to more than 1 method
    @LifecycleMeta(S3.class)
    static interface NLM_5 {

        @Transition
        void s3_X();

        @Transition
        void s3_Y();

        @Transition(S3_Y.class)
        void s3_Y2();

        @Transition
        void s3_Z();
    }
    @LifecycleMeta(PS1.class)
    static interface PStateIndicatorInterface {

        @Transition(S1_X.class)
        void doX();

        @StateIndicator
        String getState();
    }
    @LifecycleMeta(PS1.class)
    static interface PStateIndicatorConverterInterface {

        @Transition(S1_X.class)
        void doX();

        @StateIndicator
        @Converter(StateConverterImpl.class)
        Integer getState();
    }
    public static class StateConverterImpl implements StateConverter<Integer> {

        @Override
        public String toState(Integer t) {
            switch (t.intValue()) {
                case 1:
                    return PS1.States.S1_A.class.getSimpleName();
                case 2:
                    return PS1.States.S1_B.class.getSimpleName();
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public Integer fromState(String state) {
            switch (state) {
                case "S1_A":
                    return 1;
                case "S2_A":
                    return 2;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
    @LifecycleMeta(PS1.class)
    static interface NStateIndicator {

        @Transition(S1_X.class)
        void doX();

        @StateIndicator
        String getState();

        // Should not have public stateSetter
        void setState(String state);
    }
    @LifecycleMeta(PS1.class)
    static interface NStateIndicatorConverter {

        @Transition(S1_X.class)
        void doX();

        @StateIndicator
        @Converter(StateConverterImpl.class)
        Integer getState();

        // Should not have public stateSetter
        void setState(Integer state);
    }
    @LifecycleMeta(PS1.class)
    static class PrivateStateFieldClass {

        @StateIndicator
        private String state;

        @Transition(S1_X.class)
        public void doX() {}
    }
    @LifecycleMeta(PS1.class)
    static class NPublicStateFieldClass {

        @StateIndicator
        public String state;

        @Transition(S1_X.class)
        public void doX() {}
    }
    @LifecycleMeta(PS1.class)
    static class PrivateStateFieldConverterClass {

        @StateIndicator
        @Converter(StateConverterImpl.class)
        private Integer state;

        @Transition(S1_X.class)
        public void doX() {}
    }
    @LifecycleMeta(PS1.class)
    static class NPublicStateFieldConverterClass {

        @StateIndicator
        @Converter(StateConverterImpl.class)
        public String state;

        @Transition(S1_X.class)
        public void doX() {}
    }
    @LifecycleMeta(PS1.class)
    static class PrivateStateSetterClass {

        private String state;

        @Transition(S1_X.class)
        public void doX() {}

        @StateIndicator
        public String getState() {
            return state;
        }

        @SuppressWarnings("unused")
        private void setState(String state) {
            this.state = state;
        }
    }
    @LifecycleMeta(PS1.class)
    static class NPublicStateSetterClass {

        private String state;

        @Transition(S1_X.class)
        public void doX() {}

        @StateIndicator
        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
