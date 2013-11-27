package net.madz.lifecycle.engine;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.engine.StateSetterTestMetadata.SetterTestStateMachine.Transitions.Do;

public class StateSetterTestMetadata extends EngineTestBase {

    @StateMachine
    static interface SetterTestStateMachine {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.Do.class, value = Done.class)
            static interface New {}
            @End
            static interface Done {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Do {}
        }
    }
    @LifecycleMeta(SetterTestStateMachine.class)
    public static interface LazySetterBusinessInterface {

        @StateIndicator
        String getState();

        @Transition(Do.class)
        void doIt();
    }
    @net.madz.lifecycle.annotations.ReactiveObject
    public static class LazySetterBusinessImpl implements LazySetterBusinessInterface {

        private String state = SetterTestStateMachine.States.New.class.getSimpleName();

        @Override
        public String getState() {
            return state;
        }

        @SuppressWarnings("unused")
        private void setState(String state) {
            this.state = state;
        }

        @Override
        @Transition(Do.class)
        public void doIt() {}
    }
    @LifecycleMeta(SetterTestStateMachine.class)
    public static class EagerSetterBusinessImpl {

        private String state = SetterTestStateMachine.States.New.class.getSimpleName();

        public String getState() {
            return state;
        }

        @SuppressWarnings("unused")
        private void setState(String state) {
            this.state = state;
        }

        @Transition(Do.class)
        public void doIt() {}
    }
}
