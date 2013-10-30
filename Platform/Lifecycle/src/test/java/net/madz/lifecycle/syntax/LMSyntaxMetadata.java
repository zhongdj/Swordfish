package net.madz.lifecycle.syntax;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.PS1.Transitions.S1_X;

public class LMSyntaxMetadata extends BaseMetaDataTest{

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
    static class PLM_1 {}

}
