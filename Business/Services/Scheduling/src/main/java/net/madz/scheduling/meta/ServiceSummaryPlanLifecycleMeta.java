package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Conditions.VolumeMeasurable;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.States.Ongoing;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.States.VolumeLeftEmpty;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Transitions.AdjustTotalVolume;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Transitions.ConfirmFinish;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Transitions.CreateServiceOrder;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Utils.VolumeMeasurableTransition;

@StateMachine
public interface ServiceSummaryPlanLifecycleMeta {

    @StateSet
    public static class States {

        @Initial
        @Functions({ @Function(transition = CreateServiceOrder.class, value = { Ongoing.class, VolumeLeftEmpty.class }),
                @Function(transition = AdjustTotalVolume.class, value = { Ongoing.class, VolumeLeftEmpty.class }),
                @Function(transition = Transitions.Cancel.class, value = Canceled.class), @Function(transition = ConfirmFinish.class, value = { Done.class }) })
        public static class Ongoing {}
        @Functions(value = { @Function(transition = AdjustTotalVolume.class, value = { Ongoing.class, VolumeLeftEmpty.class }),
                @Function(transition = ConfirmFinish.class, value = { Done.class }) })
        public static class VolumeLeftEmpty {}
        @End
        public static class Done {}
        @End
        public static class Canceled {}
    }
    @TransitionSet
    public static class Transitions {

        @Conditional(condition = VolumeMeasurable.class, judger = VolumeMeasurableTransition.class, postEval = true)
        public static class CreateServiceOrder {}
        @Conditional(condition = VolumeMeasurable.class, judger = VolumeMeasurableTransition.class, postEval = true)
        public static class AdjustTotalVolume {}
        public static class ConfirmFinish {}
        public static class Cancel {}
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
