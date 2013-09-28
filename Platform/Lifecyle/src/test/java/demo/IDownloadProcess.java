package demo;



import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;
import net.madz.lifecycle.ITransition;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.Corrupt;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.action.Fail;
import net.madz.lifecycle.annotations.action.Recover;
import net.madz.lifecycle.annotations.action.Redo;
import net.madz.lifecycle.annotations.action.Timeout;
import net.madz.lifecycle.annotations.state.Corrupted;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Running;
import net.madz.lifecycle.annotations.state.Stopped;
import demo.IDownloadProcess.StateEnum;
import demo.IDownloadProcess.TransitionEnum;

import static demo.IDownloadProcess.TransitionEnum.Activate;
import static demo.IDownloadProcess.TransitionEnum.Err;
import static demo.IDownloadProcess.TransitionEnum.Finish;
import static demo.IDownloadProcess.TransitionEnum.Inactivate;
import static demo.IDownloadProcess.TransitionEnum.Pause;
import static demo.IDownloadProcess.TransitionEnum.Prepare;
import static demo.IDownloadProcess.TransitionEnum.Receive;
import static demo.IDownloadProcess.TransitionEnum.Remove;
import static demo.IDownloadProcess.TransitionEnum.Restart;
import static demo.IDownloadProcess.TransitionEnum.Resume;
import static demo.IDownloadProcess.TransitionEnum.Start;

@StateMachine(states = @StateSet(StateEnum.class), transitions = @TransitionSet(TransitionEnum.class))
public interface IDownloadProcess extends Serializable, IReactiveObject {

    public static enum TransitionEnum implements ITransition {
        @Recover
        @Timeout(3000L)
        Activate,
        @Corrupt
        @Timeout(3000L)
        Inactivate,
        @Fail
        @Timeout(3000L)
        Err,
        Prepare,
        Start,
        Resume,
        Pause,
        Finish,
        Receive,
        @Redo
        @Timeout(3000L)
        Restart,
        Remove;
    }

    public static enum StateEnum implements IState<IDownloadProcess, StateEnum> {
        /**
         * Preconditions: 
         * 
         * 1. Request Validation Passed
         * 
         * 1.1 URL Format legal
         *  
         * 1.2 File Path legal
         *   
         * 1.2.1 File does not exist
         * 
         * 1.2.2 File can be created (write) under it's directory
         * 
         * 1.2.2.1 File's directory (exists and can be written OR does not exist but can be created under it's parent directory)
         * 
         * Postconditions:
         * 
         * 1. Download Task Meta-data file is created.
         * 
         * 2. URL, Folder, filename, thread number, state = "New" are stored in the Meta-data file.
         */
        @Initial
        New(0, false, true),
        
        /**
         * Preconditions:
         *
         * 1. Download Task Meta-data file exists.
         * 
         * 2. Meta-information in the meta-data file are properly set.
         * 
         * 2.1. URL format is legal.
         * 
         * 2.2.Folder and filename is legal, as defined in New.Postconditions.
         * 
         * 2.3.Thread Number is greater than 0 and less than 20
         * 
         * 2.4.State is set legally (New, Inactive)
         *  
         * Postconditions:
         * 
         * 1. Following information should be reset and set within the meta-data file. 
         * 
         * 1.1. Total length is set
         * 
         * 1.2. resumable is set
         * 
         * 1.3. segments number is set
         * 
         * 1.4. state is set to "Prepared".
         * 
         * 2. Download Task data file is created 
         *    or re-created with deleting the pre-existing file (application aborted before update status to Prepared).
         */ 
        @Running
        Queued(1),
        
        /**
         * Preconditions:
         * 
         * 1. All necessary meta information had been set, as is mentioned above, such as: total length, resumable flag and etc. 
         * 
         * 2. Target data file is created.
         * 
         * Postconditions:
         * 
         * 1. Resources required by down load had been allocated to this downloadProcess.
         * 
         * 1.1. Download Worker Threads (stands for IO/CPU/MEM/NETWORK) 
         * 
         * 2.Download Task State is set to Started.
         */
        @Running
        Started(2),
        @Corrupted(recoverPriority = 1)
        InactiveQueued(3),
        @Corrupted(recoverPriority = 0)
        InactiveStarted(4),
        @Stopped
        Paused(5),
        @Stopped
        Failed(6),
        @Stopped
        Finished(7),
        @End
        Removed(8, true);

        static {
            New.transitionFunction.put(Prepare, Queued);
            New.transitionFunction.put(Remove, Removed);
            Queued.transitionFunction.put(Pause, Paused);
            Queued.transitionFunction.put(Start, Started);
            Queued.transitionFunction.put(Remove, Removed);
            Queued.transitionFunction.put(Inactivate, InactiveQueued);
            InactiveQueued.transitionFunction.put(Activate, Queued);
            InactiveQueued.transitionFunction.put(Remove, Removed);
            Started.transitionFunction.put(Pause, Paused);
            Started.transitionFunction.put(Receive, Started);
            Started.transitionFunction.put(Inactivate, InactiveStarted);
            Started.transitionFunction.put(Err, Failed);
            Started.transitionFunction.put(Finish, Finished);
            Started.transitionFunction.put(Remove, Removed);
            InactiveStarted.transitionFunction.put(Activate, Queued);
            InactiveStarted.transitionFunction.put(Remove, Removed);
            Paused.transitionFunction.put(Resume, New);
            Paused.transitionFunction.put(Restart, New);
            Paused.transitionFunction.put(Remove, Removed);
            Failed.transitionFunction.put(Resume, New);
            Failed.transitionFunction.put(Restart, New);
            Failed.transitionFunction.put(Remove, Removed);
            Finished.transitionFunction.put(Remove, Removed);
            Finished.transitionFunction.put(Restart, New);
        }
        final int seq;
        final boolean end;
        final boolean initial;
        final HashMap<TransitionEnum, StateEnum> transitionFunction = new HashMap<TransitionEnum, StateEnum>();

        private StateEnum(final int seq) {
            this(seq, false, false);
        }

        private StateEnum(final int seq, final boolean end) {
            this(seq, end, false);
        }

        private StateEnum(final int seq, final boolean end, final boolean initial) {
            this.seq = seq;
            this.end = end;
            this.initial = initial;
        }

        @Override
        public Map<TransitionEnum, StateEnum> getTransitionFunction() {
            return Collections.unmodifiableMap(transitionFunction);
        }

        @Override
        public Set<TransitionEnum> getOutboundTransitions() {
            return transitionFunction.keySet();
        }
    }

    /**
     * Rebuild lost states from incorrect persisted state and Enqueue
     */
    @Transition
    void activate();

    /**
     * Expected Precondition: No resource enlisted Any enlisted resource should
     * be delisted
     */
    @Transition
    void inactivate();

    /**
     * Initialize states and Enqueue
     */
    @Transition
    void prepare();

    /**
     * Living thread allocated
     */
    @Transition
    void start();

    /**
     * Rebuild states from correct persisted or in-memory state and Enqueue
     */
    @Transition
    void resume();

    /**
     * Deallocate Thread resource, Persist correct states
     */
    @Transition
    void pause();

    /**
     * Thread die naturally, persist correct states and recycle all resources
     * enlisted.
     */
    @Transition
    void finish();

    /**
     * Process aborted unexpected, persist current states and recycle all
     * resources enlisted
     */
    @Transition
    void err();

    /**
     * While processing, update working progress.
     * 
     * @param bytes
     *            received
     */
    @Transition
    void receive(long bytes);

    /**
     * Roll back all information change after create, Re-initialize states and
     * Enqueue
     */
    @Transition
    void restart();

    /**
     * Make sure enlisted resource has been delisted if there is, such as
     * thread, connection, memory, and persisted information and files.
     * 
     * @param both
     *            downloaded file and the download request/task
     */
    @Transition
    void remove(boolean both);

    @SuppressWarnings("unchecked")
    StateEnum getState();

    int getId();

    String getUrl();

    String getReferenceUrl();

    String getLocalFileName();

    long getContentLength();
}
