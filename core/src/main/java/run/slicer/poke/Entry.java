package run.slicer.poke;

import org.jspecify.annotations.Nullable;

public interface Entry {
    static Entry of(@Nullable String name, byte[] data) {
        return new EntryImpl(name, data);
    }

    @Nullable
    String name();

    default Entry withName(String name) {
        return new EntryImpl(name, this.data());
    }

    byte[] data();

    default Entry withData(byte[] data) {
        return new EntryImpl(this.name(), data);
    }
}
