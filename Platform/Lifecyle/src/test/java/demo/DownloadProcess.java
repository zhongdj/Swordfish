package demo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.madz.lifecycle.IReactiveObject;

public class DownloadProcess implements IDownloadProcess, IReactiveObject {

    private static final class DemoRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (this) {
                        wait(500L);
                    }
                }
            } catch (InterruptedException ex) {

            }

        }
    }

    private static final class Segment implements Serializable {

        private static final long serialVersionUID = 6637203548006150257L;
        /* package */long startOffset;
        /* package */long endOffset;
        /* package */long wroteBytes;

        /* package */Segment(long startOffset, long endOffset) {
            super();
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public long getStartOffset() {
            return startOffset;
        }

        public long getEndOffset() {
            return endOffset;
        }

        public long getWroteOffset() {
            return wroteBytes;
        }

        public void writtenBytes(long bytes) {
            if (startOffset + wroteBytes == endOffset) {
                throw new IllegalStateException("This segment receiving bytes after been finished");
            }
            if (startOffset + wroteBytes > endOffset) {
                throw new IllegalStateException("Overwrite happened.");
            }
            wroteBytes += bytes;
        }
    }

    public static class DownloadRequest implements Serializable {

        private static final long serialVersionUID = 821976542154139230L;
        /* package */final String url;
        /* package */final String referenceUrl;
        /* package */final String localFileName;

        public DownloadRequest(String url, String referenceUrl, String localFileName) {
            super();
            this.url = url;
            this.referenceUrl = referenceUrl;
            this.localFileName = localFileName;
        }

        public String getUrl() {
            return url;
        }

        public String getReferenceUrl() {
            return referenceUrl;
        }

        public String getLocalFileName() {
            return localFileName;
        }

    }

    private static final long serialVersionUID = -2206411843392592595L;

    private final DownloadRequest request;
    private StateEnum state;
    private int id;
    private long contentLength;
    private final List<Segment> segments = new ArrayList<Segment>();

    private int numberOfThreads = 1;
    private transient ExecutorService threadPool = null;

    public DownloadProcess(DownloadRequest request) {
        this(request, 1);
    }

    public DownloadProcess(DownloadRequest request, int numberOfThreads) {
        super();
        this.request = request;
        this.numberOfThreads = numberOfThreads;
        this.state = StateEnum.New;
    }

    @Override
    public StateEnum getState() {
        return this.state;
    }

    void setState(StateEnum state) {
        this.state = state;
    }

    @Override
    public void activate() {
        prepare();
    }

    @Override
    public void inactivate() {
        System.out.println("inactivate");
    }

    @Override
    public void start() {
        // tasking with segments
        threadPool.submit(new DemoRunnable());
        threadPool.submit(new DemoRunnable());
    }

    @Override
    public void resume() {
        prepare();
    }

    @Override
    public void pause() {
        stop();
    }

    @Override
    public void finish() {
        threadPool.shutdownNow();
    }

    @Override
    public void err() {
        stop();
    }

    @Override
    public void receive(long bytes) {

    }

    @Override
    public void restart() {
        stop();
        prepare();
    }

    @Override
    public void remove(boolean both) {
        stop();
        if (both) {
            deleteFile();
        }
    }

    @Override
    public void prepare() {

        if (null != threadPool) {
            threadPool.shutdownNow();
        }

        threadPool = Executors.newFixedThreadPool(2, new ThreadFactory() {

            private int counter = 1;

            @Override
            public Thread newThread(Runnable runnable) {
                final File target = new File(getLocalFileName());
                StringBuilder sb = new StringBuilder();
                sb.append(target.getName()).append(" Downloading Thread-").append(counter++);
                /*
                 * runnable = (Runnable)
                 * Proxy.newProxyInstance(getClass().getClassLoader(), new
                 * Class[] { Runnable.class }, new InvocationHandler() {
                 * 
                 * @Override public Object invoke(Object proxy, Method method,
                 * Object[] args) throws Throwable { try { method.invoke(proxy,
                 * args); // TODO propagate sub task events } catch (Exception
                 * ex) { throw new IllegalStateException(ex); } finally {
                 * 
                 * } return null; } });
                 */

                return new Thread(runnable, sb.toString());
            }
        });

        // Create segments
    }

    private void deleteFile() {
        final File downloadedFile = new File(request.localFileName);
        if (downloadedFile.exists()) {
            if (!downloadedFile.delete()) {
                throw new IllegalStateException("Cannot delete file: " + request.localFileName);
            }
        }
    }

    private void stop() {

        if (null != threadPool) {
            if (!threadPool.isShutdown() && !threadPool.isTerminated()) {
                threadPool.shutdownNow();
            }
            threadPool = null;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getUrl() {
        return request.url;
    }

    @Override
    public String getReferenceUrl() {
        return request.referenceUrl;
    }

    @Override
    public String getLocalFileName() {
        return request.localFileName;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

}
