package run.slicer.poke.js.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ExecutionException;

public interface TFuture<V> {
    V get() throws InterruptedException, ExecutionException;
}
