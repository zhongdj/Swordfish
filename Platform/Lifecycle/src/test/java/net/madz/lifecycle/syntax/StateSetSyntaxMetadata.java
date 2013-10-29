package net.madz.lifecycle.syntax;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.syntax.StateSetSyntaxMetadata.Positive.Transitions.T;

public class StateSetSyntaxMetadata extends BaseMetaDataTest {

    @StateMachine
    protected static interface Positive {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = T.class, value = B.class)
            static interface A {};
            @End
            static interface B {};
        }
        @TransitionSet
        static interface Transitions {

            static interface T {};
        }
    }
    @StateMachine
    protected static interface Negative_No_InnerClasses {}
    @StateMachine
    protected static interface Negative_No_StateSet_Aand_TransitionSet {

        static interface States {

            @Initial
            @Function(transition = T.class, value = B.class)
            static interface A {};
            @End
            static interface B {};
        }
        static interface Transitions {

            static interface T {};
        }
    }
}