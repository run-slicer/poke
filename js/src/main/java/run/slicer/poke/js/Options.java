package run.slicer.poke.js;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public interface Options extends JSObject {
    @JSBody(script = "return this.passes || 0;")
    int passes();

    @JSBody(script = "return Boolean(this.optimize);")
    boolean optimize();

    @JSBody(script = "return Boolean(this.verify);")
    boolean verify();

    @JSBody(script = "return Boolean(this.inline);")
    boolean inline();
}
