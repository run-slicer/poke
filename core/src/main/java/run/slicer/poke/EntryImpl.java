package run.slicer.poke;

import org.jspecify.annotations.Nullable;

public record EntryImpl(@Nullable String name, byte[] data) implements Entry {
}
