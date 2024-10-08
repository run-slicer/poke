package run.slicer.poke.js;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.typedarrays.Uint8Array;
import run.slicer.poke.Analyzer;

public class Main {
    static {
        // effectively disable parallel processing support
        // the proper fix would be to stop proguard-core from casting threads to its own objects,
        // but I really can't be bothered to stub that all out
        System.setProperty("parallel.threads", "1");
    }

    @JSExport
    public static JSPromise<Uint8Array> analyze(@JSByRef byte[] data, Options options) {
        return analyze0(data, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options);
    }

    private static JSPromise<Uint8Array> analyze0(byte[] data, Options options) {
        return new JSPromise<>((resolve, reject) -> {
            new Thread(() -> {
                try {
                    final Analyzer analyzer = Analyzer.builder()
                            .passes(options.passes())
                            .optimize(options.optimize())
                            .verify(options.verify())
                            .inline(options.inline())
                            .build();

                    resolve.accept(wrapByteArray(analyzer.analyze(data)));
                } catch (Throwable e) {
                    reject.accept(e);
                }
            }).start();
        });
    }

    @JSBody(params = {"data"}, script = "return new Uint8Array(data);")
    private static native Uint8Array wrapByteArray(@JSByRef byte[] data);
}
