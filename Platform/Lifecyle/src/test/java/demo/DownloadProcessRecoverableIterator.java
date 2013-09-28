package demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.madz.lifecycle.IRecoverableIterator;
import net.madz.lifecycle.meta.StateMachineMetaData;
import demo.IDownloadProcess.StateEnum;
import demo.IDownloadProcess.TransitionEnum;

public class DownloadProcessRecoverableIterator implements IRecoverableIterator<IDownloadProcess> {

    private final Iterator<IDownloadProcess> iterator;
    private final StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> stateMachineMetaData;

    public DownloadProcessRecoverableIterator(StateMachineMetaData<IDownloadProcess, StateEnum, TransitionEnum> metaData) {
        this.stateMachineMetaData = metaData;
        final List<IDownloadProcess> downloads = getSampleProcesses();
        iterator = downloads.iterator();
    }

    public List<IDownloadProcess> getSampleProcesses() {
        final ArrayList<IDownloadProcess> result = new ArrayList<IDownloadProcess>();
        ObjectInputStream ois = null;
        try {
            final File persistent = new File("dataStore");
            System.out.println(persistent.getAbsolutePath());
            if (!persistent.exists()) {
                persistent.createNewFile();
            }
            ois = new ObjectInputStream(new FileInputStream(persistent));
            List<IDownloadProcess> saved = (List<IDownloadProcess>) ois.readObject();
            result.addAll(saved);
        } catch (Exception e) {
            // throw new IllegalStateException(e);
            return new ArrayList<IDownloadProcess>();
        } finally {
            if (null != ois) {
                try {
                    ois.close();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public IDownloadProcess next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
