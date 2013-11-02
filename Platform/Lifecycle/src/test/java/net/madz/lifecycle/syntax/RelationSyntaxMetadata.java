package net.madz.lifecycle.syntax;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.ErrorMessage;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.Parent;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.relation.ValidWhile;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Overrides;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.POwningStateMachine.Transitions.OwningX;
import net.madz.lifecycle.syntax.RelationSyntaxMetadata.POwningStateMachine.Transitions.OwningY;

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
            @InboundWhile(on = { RelatedSM.States.RB.class }, relation = PStandalone.Relations.PR.class,
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
            @ValidWhile(on = { RelatedSM.States.RB.class }, relation = PStandalone.Relations.PR.class,
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
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
            @ValidWhile(on = { RelatedSM.States.RB.class }, relation = PStandalone.Relations.PR.class)
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
    static interface NStandalone4 {

        static String error = Errors.RELATIONSET_MULTIPLE;

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NStandalone4.Transitions.NX.class, value = NStandalone4.States.NB.class)
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

            @RelateTo(NStandalone4.Transitions.NX.class)
            static interface NR {}
        }
        @RelationSet
        static interface Relations2 {

            @RelateTo(NStandalone4.Transitions.NX.class)
            static interface NR {}
        }
    }
    @StateMachine
    static interface Super {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Super.Transitions.SX.class, value = SB.class)
            @InboundWhile(relation = Super.Relations.SR.class, on = { RelatedSM.States.RB.class },
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
            @ValidWhile(relation = Super.Relations.SR.class, on = { RelatedSM.States.RB.class },
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
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

            @Function(transition = Super.Transitions.SX.class, value = NC2C.class)
            static interface NCA extends Super.States.SA {}
            @Function(transition = NChild2.Transitions.NC2X.class, value = SB.class)
            @InboundWhile(relation = Super.Relations.SR.class, on = { InvalidRelationReferenceSM.States.B.class })
            static interface NC2C {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {

            static interface NC2X {};
        }
    }
    @StateMachine
    static interface NChild3 extends Super {

        static String error = Errors.RELATION_VALIDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET;

        @StateSet
        static interface States extends Super.States {

            @Function(transition = Super.Transitions.SX.class, value = NC3C.class)
            static interface NC3A extends Super.States.SA {}
            @Function(transition = NChild3.Transitions.NC3X.class, value = SB.class)
            @InboundWhile(relation = PStandalone.Relations.PR.class, on = { InvalidRelationReferenceSM.States.B.class })
            static interface NC3C {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {

            static interface NC3X {};
        }
    }
    @StateMachine
    static interface NChild4 extends Super {

        static String error = Errors.RELATION_ON_ATTRIBUTE_OF_VALIDWHILE_NOT_MACHING_RELATION;

        @StateSet
        static interface States extends Super.States {

            @Function(transition = Super.Transitions.SX.class, value = NC4C.class)
            static interface NC4A extends Super.States.SA {}
            @Function(transition = NChild4.Transitions.NC4X.class, value = SB.class)
            @InboundWhile(relation = Super.Relations.SR.class, on = { InvalidRelationReferenceSM.States.B.class })
            static interface NC4C {}
        }
        @TransitionSet
        static interface Transitions extends Super.Transitions {

            static interface NC4X {};
        }
    }
    @StateMachine
    static interface NStandalone5 {

        static String error = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID;

        @StateSet
        static interface States {

            @Initial
            @Function(transition = NStandalone5.Transitions.N5X.class, value = N5B.class)
            @InboundWhile(relation = NStandalone5.Relations.N5R.class, on = { RelatedSM.States.RB.class },
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { InvalidRelationReferenceSM.States.A.class }) })
            @ValidWhile(relation = NStandalone5.Relations.N5R.class, on = { RelatedSM.States.RB.class },
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_VALIDWHILE_INVALID,
                            states = { InvalidRelationReferenceSM.States.A.class }) })
            static interface N5A {}
            @End
            static interface N5B {}
        }
        @TransitionSet
        static interface Transitions {

            static interface N5X {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(RelatedSM.class)
            static interface N5R {}
        }
    }
    @StateMachine
    static interface PStandaloneParent {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = PStandaloneParent.Transitions.PPX.class, value = PStandaloneParent.States.PPB.class)
            @InboundWhile(on = { RelatedSM.States.RB.class }, relation = PStandaloneParent.Relations.PPR.class,
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
            @ValidWhile(on = { RelatedSM.States.RB.class }, relation = PStandaloneParent.Relations.PPR.class,
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
            static interface PPA {}
            @End
            static interface PPB {}
        }
        @TransitionSet
        static interface Transitions {

            static interface PPX {}
        }
        @RelationSet
        static interface Relations {

            @Parent
            @RelateTo(RelatedSM.class)
            static interface PPR {}
        }
    }
    @StateMachine
    static interface POwningStateMachine {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = OwningX.class, value = OwningB.class)
            static interface OwningA {}
            @CompositeStateMachine
            @Function(transition = OwningY.class, value = OwningC.class)
            static interface OwningB {

                @StateSet
                static interface CStates {

                    @Initial
                    @Function(transition = OwningB.CTransitions.CompositeX.class,
                            value = OwningB.CStates.CompositeB.class)
                    @InboundWhile(on = { RelatedSM.States.RB.class }, relation = OwningB.CRelations.PCS1R.class,
                            otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                                    code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                                    states = { RelatedSM.States.RA.class }) })
                    static interface CompositeA {}
                    @Function(transition = OwningB.CTransitions.CompositeX.class,
                            value = OwningB.CStates.CompositeC.class)
                    @InboundWhile(on = { RelatedSM.States.RB.class }, relation = OwningB.CRelations.PCS1R.class,
                            otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                                    code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                                    states = { RelatedSM.States.RA.class }) })
                    static interface CompositeB {}
                    @End
                    @ShortCut(OwningC.class)
                    static interface CompositeC {}
                }
                @TransitionSet
                static interface CTransitions {

                    static interface CompositeX {}
                }
                @RelationSet
                static interface CRelations {

                    @Parent
                    @RelateTo(RelatedSM.class)
                    static interface PCS1R {}
                }
            }
            @End
            static interface OwningC {}
        }
        @TransitionSet
        static interface Transitions {

            static interface OwningX {}
            static interface OwningY {}
        }
    }
    @StateMachine
    static interface PParentRelationSuper {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = PParentRelationSuper.Transitions.PPX.class,
                    value = PParentRelationSuper.States.PPB.class)
            @InboundWhile(on = { RelatedSM.States.RB.class }, relation = PParentRelationSuper.Relations.PPR.class,
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
            @ValidWhile(on = { RelatedSM.States.RB.class }, relation = PParentRelationSuper.Relations.PPR.class,
                    otherwise = { @ErrorMessage(bundle = Errors.SYNTAX_ERROR_BUNDLE,
                            code = Errors.RELATION_OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID,
                            states = { RelatedSM.States.RA.class }) })
            static interface PPA {}
            @End
            static interface PPB {}
        }
        @TransitionSet
        static interface Transitions {

            static interface PPX {}
        }
        @RelationSet
        static interface Relations {

            @Parent
            @RelateTo(RelatedSM.class)
            static interface PPR {}
        }
    }
    @StateMachine
    static interface PParentRlationChild extends PParentRelationSuper {

        @RelationSet
        static interface Relations {

            @Parent
            @RelateTo(RelatedSM.class)
            @Overrides
            static interface PCR {}
        }
    }
}
