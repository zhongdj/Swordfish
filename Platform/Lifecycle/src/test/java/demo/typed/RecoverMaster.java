package demo.typed;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.event.ILifeCycleEventListener;
import net.madz.event.LifeCycleEvent;
import net.madz.lifecycle.impl.TransitionInvocationHandler;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.StateMetaData;
import net.madz.lifecycle.meta.TransitionMetaData;
import net.madz.lifecycle.meta.impl.StateMachineMetaDataBuilderImpl;
import net.madz.verification.VerificationFailureSet;

import com.google.common.eventbus.Subscribe;

import demo.typed.IDownloadProcess.StateEnum;
import demo.typed.IDownloadProcess.TransitionEnum;

public class RecoverMaster implements ILifeCycleEventListener {

    public static final Logger LOGGER = Logger.getLogger(RecoverMaster.class.getName());
    final StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> machineMetaData = buildStateMachineMetaData();

    @Override
    public void onLifeCycleEvent(LifeCycleEvent event) {
        LOGGER.log(Level.INFO, event.toString());
        switch (event) {
        case INIT_EVENT:
            break;
        case STARTUP_EVENT:
            corrupt(event);
            break;
        case READY:
            recover(event);
            break;
        case SHUTDOWN_EVENT:
            break;
        case TERMINATION_EVENT:
            break;
        }
    }

    @Subscribe
    public void recover(LifeCycleEvent event) {
        if (event != LifeCycleEvent.READY) {
            return;
        }
        final DownloadProcessRecoverableIterator iterator = new DownloadProcessRecoverableIterator(machineMetaData);
        final ArrayList<IDownloadProcess> allDownloadProcess = new ArrayList<IDownloadProcess>();
        IDownloadProcess downloadProcess = null;
        while (iterator.hasNext()) {
            downloadProcess = iterator.next();
            allDownloadProcess.add(downloadProcess);
            downloadProcess = (IDownloadProcess) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { IDownloadProcess.class },
                    new TransitionInvocationHandler<IDownloadProcess, IDownloadProcess.StateEnum, IDownloadProcess.TransitionEnum>(downloadProcess));
            StateEnum state = downloadProcess.getState();
            LOGGER.info("recovering download process " + downloadProcess + ".state=" + state);
            StateMetaData<IDownloadProcess, StateEnum> stateMetaData = machineMetaData.getStateMetaData(state);
            TransitionMetaData recoverTransitionMetaData = stateMetaData.getRecoverTransitionMetaData();
            if (null != recoverTransitionMetaData && null != recoverTransitionMetaData.getTransitionMethod()) {
                try {
                    recoverTransitionMetaData.getTransitionMethod().invoke(downloadProcess);
                    state = downloadProcess.getState();
                    LOGGER.info("recovered download process " + downloadProcess + ".state=" + state);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Recover Process Failed");
                }
            }
        }
        StoreHelper.save(allDownloadProcess);
    }

    @Subscribe
    public void corrupt(LifeCycleEvent event) {
        if (event != LifeCycleEvent.STARTUP_EVENT) {
            return;
        }
        final DownloadProcessRecoverableIterator iterator = new DownloadProcessRecoverableIterator(machineMetaData);
        IDownloadProcess downloadProcess = null;
        final ArrayList<IDownloadProcess> allDownloadProcess = new ArrayList<IDownloadProcess>();
        while (iterator.hasNext()) {
            downloadProcess = iterator.next();
            allDownloadProcess.add(downloadProcess);
            downloadProcess = (IDownloadProcess) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { IDownloadProcess.class },
                    new TransitionInvocationHandler<IDownloadProcess, IDownloadProcess.StateEnum, IDownloadProcess.TransitionEnum>(downloadProcess));
            StateEnum state = downloadProcess.getState();
            LOGGER.info("corrupting download process " + downloadProcess + " from " + state);
            StateMetaData<IDownloadProcess, StateEnum> stateMetaData = machineMetaData.getStateMetaData(state);
            TransitionMetaData corruptTransitionMetaData = stateMetaData.getCorruptTransitionMetaData();
            if (null != corruptTransitionMetaData && null != corruptTransitionMetaData.getTransitionMethod()) {
                try {
                    corruptTransitionMetaData.getTransitionMethod().invoke(downloadProcess);
                    state = downloadProcess.getState();
                    LOGGER.info("Corrupted download process " + downloadProcess + " to " + state);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Corrupt Process Failed");
                }
            }
        }
        StoreHelper.save(allDownloadProcess);

    }

    private static StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> buildStateMachineMetaData() {
        final StateMachineMetaDataBuilderImpl builder = new StateMachineMetaDataBuilderImpl(null, "StateMachine");
        @SuppressWarnings("unchecked")
        final StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> machineMetaData = (StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum>) builder
                .build(null, IDownloadProcess.class);
        VerificationFailureSet verificationSet = new VerificationFailureSet();
        machineMetaData.verifyMetaData(verificationSet);
        if (verificationSet.hasVerificationFailures()) {
            throw new IllegalStateException("StateMachineMetaData has verifiation failures");
        }
        return machineMetaData;
    }
}
