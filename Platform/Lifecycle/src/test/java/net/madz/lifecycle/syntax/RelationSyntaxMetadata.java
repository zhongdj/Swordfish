package net.madz.lifecycle.syntax;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.InboundWhile;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.RelationSet;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.InvalidReferenceSM.Transitions.X;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.NChild.Transitions.NCX;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.NStandalone.Transitions.NX;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.PChild.Transitions.PCX;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.PStandalone.Relations.PR;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.PStandalone.Transitions.PX;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.RelatedSM.Transitions.RX;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.Super.Relations.SR;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.Super.Transitions.SX;

public class RelationSyntaxMetadata extends BaseMetaDataTest {

    @StateMachine
    static interface InvalidReferenceSM {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = X.class, value = B.class)
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
    static interface RelatedSM {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = RX.class, value = RB.class)
            static interface RA {}
            @End
            static interface RB {}
        }
        @TransitionSet
        static interface Transitions {

            static interface RX {}
        }
    }
    @StateMachine
    static interface PStandalone {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = PX.class, value = PB.class)
            @InboundWhile(on = { RelatedSM.States.RB.class }, relation = PR.class)
            static interface PA {}
            @End
            static interface PB {}
        }
        @TransitionSet
        static interface Transitions {

            static interface PX {}
        }
        @RelationSet
        static interface Relations {

            static interface PR {}
        }
    }
    @StateMachine
    static interface NStandalone {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NX.class, value = NB.class)
            @InboundWhile(on = { RelatedSM.States.RB.class }, relation = PR.class)
            static interface NA {}
            @End
            static interface NB {}
        }
        @TransitionSet
        static interface Transitions {

            static interface NX {}
        }
        @RelationSet
        static interface Relations {

            static interface NR {}
        }
    }
    @StateMachine
    static interface Super {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = SX.class, value = SB.class)
            @InboundWhile(relation = SR.class, on = { RelatedSM.States.RB.class })
            static interface SA {}
            @End
            static interface SB {}
        }
        @TransitionSet
        static interface Transitions {

            static interface SX {}
        }
        @RelationSet
        static interface Relations {

            static interface SR {}
        }
    }
    @StateMachine
    static interface PChild extends Super {

        @StateSet
        static interface States extends Super.States {

            @Function(transition = SX.class, value = CC.class)
            static interface CA extends Super.States.SA {}
            @Function(transition = PCX.class, value = SB.class)
            @InboundWhile(relation = SR.class, on = { RelatedSM.States.RB.class })
            static interface CC {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {

            static interface PCX {};
        }
    }
    
    @StateMachine
    static interface NChild extends Super {

        @StateSet
        static interface States extends Super.States {
            @Function(transition = SX.class, value = NCC.class)
            static interface NCA extends Super.States.SA {}
            @Function(transition = NCX.class, value = SB.class)
            @InboundWhile(relation = PR.class, on = { RelatedSM.States.RB.class })
            static interface NCC {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {
            static interface NCX {};
        }
    }
}
