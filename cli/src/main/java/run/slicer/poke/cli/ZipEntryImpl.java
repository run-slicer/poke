package run.slicer.poke.cli;

import run.slicer.poke.Entry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

record ZipEntryImpl(ZipFile file, ZipEntry entry) implements Entry {
    @Override
    public String name() {
        return entry.getName();
    }

    @Override
    public byte[] data() {
        try {
            return file.getInputStream(entry).readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
