package run.slicer.poke.cli;

import picocli.CommandLine;
import run.slicer.poke.Analyzer;
import run.slicer.poke.Entry;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@CommandLine.Command(
        name = "poke",
        mixinStandardHelpOptions = true,
        version = BuildParameters.VERSION,
        description = "A Java library for performing bytecode normalization and generic deobfuscation."
)
public final class Main implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "The class/JAR file to be analyzed.")
    private Path input;

    @CommandLine.Parameters(index = "1", description = "The analyzed class/JAR file destination.")
    private Path output;

    @CommandLine.Option(names = {"-p", "--passes"}, description = "The amount of optimization passes.", defaultValue = "1")
    private int passes;

    @CommandLine.Option(names = "--optimize", description = "Performs optimizations.", negatable = true)
    private boolean optimize;

    @CommandLine.Option(names = "--verify", description = "Performs preemptive verification and correction.", negatable = true)
    private boolean verify;

    @Override
    public Integer call() throws Exception {
        final Analyzer analyzer = Analyzer.builder()
                .passes(this.passes)
                .optimize(this.optimize)
                .verify(this.verify)
                .build();

        boolean isClass = false;
        try (final var dis = new DataInputStream(Files.newInputStream(this.input))) {
            isClass = dis.readInt() == 0xcafebabe; // class file magic
        } catch (IOException ignored) {
        }

        Files.createDirectories(this.output.getParent());
        if (isClass) {
            Files.write(
                    this.output, analyzer.analyze(Files.readAllBytes(this.input)),
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
            );
        } else {
            try (final var zf = new ZipFile(this.input.toFile())) {
                final var results = analyzer.analyze(
                        zf.stream()
                                .filter(e -> !e.isDirectory() && e.getName().endsWith(".class"))
                                .map(e -> new ZipEntryImpl(zf, e))
                                .toList()
                );

                final var entries = results.stream().collect(Collectors.toMap(Entry::name, Function.identity()));
                try (final var zos = new ZipOutputStream(Files.newOutputStream(this.output))) {
                    for (final ZipEntry entry : Collections.list(zf.entries())) {
                        // TODO: copy entry metadata?
                        zos.putNextEntry(new ZipEntry(entry.getName()));

                        if (!entry.isDirectory()) {
                            final Entry pokeEntry = entries.get(entry.getName());
                            zos.write(pokeEntry != null ? pokeEntry.data() : zf.getInputStream(entry).readAllBytes());
                        }

                        zos.closeEntry();
                    }
                }
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
