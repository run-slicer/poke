package run.slicer.poke.cli;

import picocli.CommandLine;
import run.slicer.poke.Analyzer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "poke",
        mixinStandardHelpOptions = true,
        version = BuildParameters.VERSION,
        description = "A Java library for performing bytecode normalization and generic deobfuscation."
)
public final class Main implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "The class file to be analyzed.")
    private Path input;

    @CommandLine.Parameters(index = "1", description = "The analyzed class file destination.")
    private Path output;

    @CommandLine.Option(names = {"-p", "--passes"}, description = "The amount of optimization passes.", defaultValue = "1")
    private int passes;

    @CommandLine.Option(names = "--optimize", description = "Performs optimizations.", negatable = true)
    private boolean optimize;

    @CommandLine.Option(names = "--verify", description = "Performs preemptive verification and correction.", negatable = true)
    private boolean verify;

    @Override
    public Integer call() throws Exception {
        Files.createDirectories(this.output.getParent());

        Files.write(
                this.output,
                Analyzer.builder()
                        .passes(this.passes)
                        .optimize(this.optimize)
                        .verify(this.verify)
                        .build()
                        .analyze(Files.readAllBytes(this.input)),
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );

        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
