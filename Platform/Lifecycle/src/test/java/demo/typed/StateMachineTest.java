package demo.typed;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.madz.common.Dumper;
import net.madz.event.LifeCycleEvent;
import net.madz.event.LifeCycleEventUtils;
import net.madz.lifecycle.impl.TransitionInvocationHandler;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.impl.StateMachineMetaDataBuilderImpl;
import net.madz.verification.VerificationFailureSet;
import demo.typed.DownloadProcess.DownloadRequest;
import demo.typed.IDownloadProcess.StateEnum;
import demo.typed.IDownloadProcess.TransitionEnum;

import org.junit.Test;

public class StateMachineTest {

    @Test
    public void should_in_right_state_after_transition() {
        LifeCycleEventUtils.notify(LifeCycleEvent.INIT_EVENT);

        LifeCycleEventUtils.notify(LifeCycleEvent.STARTUP_EVENT);

        LifeCycleEventUtils.notify(LifeCycleEvent.READY);

        final Dumper dumper = new Dumper(System.out);

        final StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> machineMetaData = testBuildStateMachineMetaData(dumper);

        final DownloadProcess process = createSampleProcess();

        testTransition(dumper, process, machineMetaData);

        // testRecover(machineMetaData, iProcess);

        LifeCycleEventUtils.notify(LifeCycleEvent.SHUTDOWN_EVENT);

        LifeCycleEventUtils.notify(LifeCycleEvent.TERMINATION_EVENT);

//        Assert.assertEquals("\nDumping State Machine Meta Data\n" +
//                "\n" +
//                "StateMachineMetaDataImpl[name=demo.IDownloadProcess.StateMachine, states={New=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.New, state=New, type=Initial], Queued=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Queued, state=Queued, type=Running], Started=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Started, state=Started, type=Running], InactiveQueued=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.InactiveQueued, state=InactiveQueued, type=Corrupted], InactiveStarted=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.InactiveStarted, state=InactiveStarted, type=Corrupted], Paused=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Paused, state=Paused, type=Stopped], Failed=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Failed, state=Failed, type=Stopped], Finished=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Finished, state=Finished, type=Stopped], Removed=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Removed, state=Removed, type=End]}, transition={Activate=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Activate, type=Recover, transition=Activate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.activate()], Inactivate=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Inactivate, type=Corrupt, transition=Inactivate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.inactivate()], Err=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Err, type=Other, transition=Err, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.err()], Prepare=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Prepare, type=Other, transition=Prepare, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.prepare()], Start=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Start, type=Other, transition=Start, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.start()], Resume=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Resume, type=Other, transition=Resume, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.resume()], Pause=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Pause, type=Other, transition=Pause, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.pause()], Finish=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Finish, type=Other, transition=Finish, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.finish()], Receive=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Receive, type=Other, transition=Receive, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.receive(long)], Restart=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Restart, type=Redo, transition=Restart, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.restart()], Remove=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Remove, type=Other, transition=Remove, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.remove(boolean)]}]\n" +
//                "   allStates={\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.New, state=New, type=Initial]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Removed, state=Removed, type=End]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Queued, state=Queued, type=Running]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Started, state=Started, type=Running]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.InactiveQueued, state=InactiveQueued, type=Corrupted]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.InactiveStarted, state=InactiveStarted, type=Corrupted]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Paused, state=Paused, type=Stopped]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Failed, state=Failed, type=Stopped]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Finished, state=Finished, type=Stopped]\n" +
//                "   }\n" +
//                "   allTransitions={\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Activate, type=Recover, transition=Activate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.activate()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Inactivate, type=Corrupt, transition=Inactivate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.inactivate()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Err, type=Other, transition=Err, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.err()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Prepare, type=Other, transition=Prepare, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.prepare()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Start, type=Other, transition=Start, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.start()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Resume, type=Other, transition=Resume, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.resume()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Pause, type=Other, transition=Pause, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.pause()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Finish, type=Other, transition=Finish, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.finish()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Receive, type=Other, transition=Receive, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.receive(long)]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Restart, type=Redo, transition=Restart, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.restart()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Remove, type=Other, transition=Remove, timeout=30000, transitionMethod=public abstract void demo.IDownloadProcess.remove(boolean)]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Inactivate, type=Corrupt, transition=Inactivate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.inactivate()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Activate, type=Recover, transition=Activate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.activate()]\n" +
//                "      TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Restart, type=Redo, transition=Restart, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.restart()]\n" +
//                "   }\n" +
//                "   initialState=StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.New, state=New, type=Initial]\n" +
//                "   allFinalStates={\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Removed, state=Removed, type=End]\n" +
//                "   }\n" +
//                "   allTransientStates={\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Queued, state=Queued, type=Running]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Started, state=Started, type=Running]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.InactiveQueued, state=InactiveQueued, type=Corrupted]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.InactiveStarted, state=InactiveStarted, type=Corrupted]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Paused, state=Paused, type=Stopped]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Failed, state=Failed, type=Stopped]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Finished, state=Finished, type=Stopped]\n" +
//                "   }\n" +
//                "   allRunningStates={\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Queued, state=Queued, type=Running]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Started, state=Started, type=Running]\n" +
//                "   }\n" +
//                "   allStoppedStates={\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Paused, state=Paused, type=Stopped]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Failed, state=Failed, type=Stopped]\n" +
//                "      StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Finished, state=Finished, type=Stopped]\n" +
//                "   }\n" +
//                "   allWaitingStates={\n" +
//                "   }\n" +
//                "   corruptTransition=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Inactivate, type=Corrupt, transition=Inactivate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.inactivate()]\n" +
//                "   recoverTransition=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Activate, type=Recover, transition=Activate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.activate()]\n" +
//                "   redoTransition=TransitionMetaDataImpl [dottedPath=demo.IDownloadProcess.StateMachine.Activate, type=Recover, transition=Activate, timeout=3000, transitionMethod=public abstract void demo.IDownloadProcess.activate()]\n" +
//                "\n" +
//                "Test Transition\n" +
//                "\n" +
//                "From = StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.New, state=New, type=Initial]\n" +
//                "To   = StateMetaDataImpl [path=demo.IDownloadProcess.StateMachine.Queued, state=Queued, type=Running]\n", dumper.toString());
    }

    private DownloadProcess createSampleProcess() {
        DownloadRequest r = new DownloadRequest("", "", null);
        final DownloadProcess process = new DownloadProcess(r, 3);
        final List<IDownloadProcess> list = new ArrayList<IDownloadProcess>();
        list.add(process);
        StoreHelper.save(list);
        return process;
    }

    private StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> testBuildStateMachineMetaData(Dumper dumper) {
        dumper.println("");
        dumper.println("Dumping State Machine Meta Data");
        dumper.println("");
        final StateMachineMetaDataBuilderImpl builder = new StateMachineMetaDataBuilderImpl(null, "StateMachine");
        @SuppressWarnings("unchecked")
        final StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> machineMetaData = (StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum>) builder
                .build(null, IDownloadProcess.class);
        machineMetaData.dump(dumper);
        VerificationFailureSet verificationSet = new VerificationFailureSet();
        machineMetaData.verifyMetaData(verificationSet);
        verificationSet.dump(dumper);
        return machineMetaData;
    }

    private void testTransition(Dumper dumper, final DownloadProcess process,
                                       final StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> machineMetaData) {
        dumper.println("");
        dumper.println("Test Transition");
        dumper.println("");

        @SuppressWarnings("rawtypes")
        IDownloadProcess iProcess = (IDownloadProcess) Proxy.newProxyInstance(StateMachineTest.class.getClassLoader(), new Class[]{IDownloadProcess.class},
                new TransitionInvocationHandler(process));

        dumper.print("From = ");
        machineMetaData.getStateMetaData(iProcess.getState()).dump(dumper);
        iProcess.prepare();
        dumper.print("To   = ");
        machineMetaData.getStateMetaData(iProcess.getState()).dump(dumper);
    }
}
