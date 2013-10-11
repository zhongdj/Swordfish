package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.Conditions.VolumeMeasurable;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.States.Ongoing;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.States.VolumeLeftEmpty;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.Transitions.AdjustTotalVolume;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.Transitions.ConfirmFinish;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.Transitions.CreateServiceOrder;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.Utils.VolumeMeasurableTransition;

@StateMachine
public interface SummaryPlanLifecycleMeta {

    @StateSet
    public static class States {

        @Functions(value = { @Function(transition = CreateServiceOrder.class, value = { Ongoing.class, VolumeLeftEmpty.class }),
                @Function(transition = AdjustTotalVolume.class, value = { Ongoing.class, VolumeLeftEmpty.class }),
                @Function(transition = ConfirmFinish.class, value = { Done.class }) })
        @Initial
        public static class Ongoing {
        }

        @Functions(value = { @Function(transition = AdjustTotalVolume.class, value = { Ongoing.class, VolumeLeftEmpty.class }),
                @Function(transition = ConfirmFinish.class, value = { Done.class }) })
        public static class VolumeLeftEmpty {
        }

        @End
        public static class Done {
        }
    }

    @TransitionSet
    public static class Transitions {

        public static class CreateServiceOrder extends VolumeMeasurableTransition {
        }

        public static class AdjustTotalVolume extends VolumeMeasurableTransition {
        }

        public static class ConfirmFinish {
        }
    }

    @ConditionSet
    public static class Conditions {

        public static interface VolumeMeasurable {

            boolean isVolumeLeft();
        }
    }

    public static class Utils {

        public static class VolumeMeasurableTransition implements ConditionalTransition<VolumeMeasurable> {

            @Override
            public Class<?> doConditionJudge(VolumeMeasurable t) {
                if ( t.isVolumeLeft() ) {
                    return Ongoing.class;
                } else {
                    return VolumeLeftEmpty.class;
                }
            }
        }
    }
}
