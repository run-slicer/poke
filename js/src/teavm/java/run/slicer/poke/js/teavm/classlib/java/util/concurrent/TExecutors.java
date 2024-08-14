package run.slicer.poke.js.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ThreadFactory;

public class TExecutors {
    @SuppressWarnings("DataFlowIssue")
    public static TExecutorService newFixedThreadPool(int ignored0, ThreadFactory ignored1) {
        return (TExecutorService) ((Object) new DummyExecutorService());
    }
}
