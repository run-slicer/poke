package run.slicer.poke.js;

import org.teavm.jso.JSByRef;
import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSObjects;
import run.slicer.poke.Analyzer;

public class Main {
    @JSExport
    public static @JSByRef byte[] analyze(@JSByRef byte[] data, Options options) {
        return analyze0(data, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options);
    }

    private static byte[] analyze0(byte[] data, Options options) {
        final Analyzer analyzer = Analyzer.builder()
                .passes(options.passes())
                .optimize(options.optimize())
                .verify(options.verify())
                .build();

        return analyzer.analyze(data);
    }
}
