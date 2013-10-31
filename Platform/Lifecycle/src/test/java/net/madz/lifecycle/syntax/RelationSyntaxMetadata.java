package net.madz.lifecycle.syntax;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;

public class RelationSyntaxMetadata extends BaseMetaDataTest {

    @StateMachine
    static interface InvalidRelationReferenceSM {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = InvalidRelationReferenceSM.Transitions.X.class, value = B.class)
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
            @Function(transition = RelatedSM.Transitions.RX.class, value = RB.class)
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
            @Function(transition = PStandalone.Transitions.PX.class, value = PB.class)
            @InboundWhile(on = { RelatedSM.States.RB.class }, relation = PStandalone.Relations.PR.class)
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

            @RelateTo(RelatedSM.class)
            static interface PR {}
        }
    }
    @StateMachine
    static interface NStandalone {

        static String error = Errors.RELATION_INBOUNDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET;

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NStandalone.Transitions.NX.class, value = NB.class)
            @InboundWhile(on = { RelatedSM.States.RB.class }, relation = PStandalone.Relations.PR.class)
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

            @RelateTo(RelatedSM.class)
            static interface NR {}
        }
    }
    @StateMachine
    static interface NStandalone2 {

        static String error = Errors.RELATION_ON_ATTRIBUTE_OF_INBOUNDWHILE_NOT_MATCHING_RELATION;

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NStandalone2.Transitions.NX.class, value = NStandalone2.States.NB.class)
            @InboundWhile(on = { InvalidRelationReferenceSM.States.B.class },
                    relation = NStandalone2.Relations.NR.class)
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

            @RelateTo(RelatedSM.class)
            static interface NR {}
        }
    }
    @StateMachine
    static interface NStandalone3 {

        static String error = Errors.RELATION_RELATED_TO_REFER_TO_NON_STATEMACHINE;

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NStandalone3.Transitions.NX.class, value = NStandalone3.States.NB.class)
            @InboundWhile(on = { InvalidRelationReferenceSM.States.B.class },
                    relation = NStandalone2.Relations.NR.class)
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

            @RelateTo(NStandalone3.Transitions.NX.class)
            static interface NR {}
        }
    }
    @StateMachine
    static interface Super {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Super.Transitions.SX.class, value = SB.class)
            @InboundWhile(relation = Super.Relations.SR.class, on = { RelatedSM.States.RB.class })
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

            @RelateTo(RelatedSM.class)
            static interface SR {}
        }
    }
    @StateMachine
    static interface PChild extends Super {

        @StateSet
        static interface States extends Super.States {

            @Function(transition = Super.Transitions.SX.class, value = CC.class)
            static interface CA extends Super.States.SA {}
            @Function(transition = PChild.Transitions.PCX.class, value = SB.class)
            @InboundWhile(relation = Super.Relations.SR.class, on = { RelatedSM.States.RB.class })
            static interface CC {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {

            static interface PCX {};
        }
    }
    @StateMachine
    static interface NChild extends Super {

        static String error = Errors.RELATION_INBOUNDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET;

        @StateSet
        static interface States extends Super.States {

            @Function(transition = Super.Transitions.SX.class, value = NCC.class)
            static interface NCA extends Super.States.SA {}
            @Function(transition = NChild.Transitions.NCX.class, value = SB.class)
            @InboundWhile(relation = PStandalone.Relations.PR.class, on = { InvalidRelationReferenceSM.States.B.class })
            static interface NCC {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {

            static interface NCX {};
        }
    }
    @StateMachine
    static interface NChild2 extends Super {

        static String error = Errors.RELATION_ON_ATTRIBUTE_OF_INBOUNDWHILE_NOT_MATCHING_RELATION;

        @StateSet
        static interface States extends Super.States {

            @Function(transition = Super.Transitions.SX.class, value = NCC.class)
            static interface NCA extends Super.States.SA {}
            @Function(transition = NChild2.Transitions.NCX.class, value = SB.class)
            @InboundWhile(relation = Super.Relations.SR.class, on = { InvalidRelationReferenceSM.States.B.class })
            static interface NCC {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {

            static interface NCX {};
        }
    }
}
