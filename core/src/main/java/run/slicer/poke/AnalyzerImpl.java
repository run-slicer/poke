package run.slicer.poke;

import proguard.*;
import proguard.classfile.ClassPool;
import proguard.classfile.ProgramClass;
import proguard.classfile.io.ProgramClassReader;
import proguard.classfile.io.ProgramClassWriter;
import proguard.classfile.pass.PrimitiveArrayConstantIntroducer;
import proguard.classfile.util.PrimitiveArrayConstantReplacer;
import proguard.optimize.LineNumberTrimmer;
import proguard.optimize.Optimizer;
import proguard.optimize.peephole.LineNumberLinearizer;
import proguard.preverify.PreverificationClearer;
import proguard.preverify.Preverifier;
import proguard.preverify.SubroutineInliner;

import java.io.*;
import java.util.List;

record AnalyzerImpl(Configuration config) implements Analyzer {
    @Override
    public byte[] analyze(byte[] b) {
        final var clazz = new ProgramClass();
        clazz.accept(new ProgramClassReader(new DataInputStream(new ByteArrayInputStream(b))));

        final var view = new AppView(new ClassPool(clazz), new ClassPool());

        final boolean willOptimize = config.optimize && config.optimizationPasses > 0;
        if (config.preverify || willOptimize) {
            new PreverificationClearer().execute(view);
        }

        if (config.preverify) {
            new SubroutineInliner(config).execute(view);
        }
        if (willOptimize) {
            new PrimitiveArrayConstantIntroducer().execute(view);
            this.optimize(view);
            new LineNumberLinearizer().execute(view);
            clazz.accept(new PrimitiveArrayConstantReplacer());
        }
        if (config.preverify) {
            new Preverifier(config).execute(view);
        }

        if (config.preverify || willOptimize) {
            new LineNumberTrimmer().execute(view);
        }

        final var output = new ByteArrayOutputStream();
        clazz.accept(new ProgramClassWriter(new DataOutputStream(output)));

        return output.toByteArray();
    }

    private void optimize(AppView view) {
        final var optimizer = new Optimizer(config);
        for (int i = 0; i < config.optimizationPasses; i++) {
            try {
                optimizer.execute(view);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    static final class Builder implements Analyzer.Builder {
        private int passes = 1;
        private boolean verify = false;
        private boolean optimize = false;

        Builder() {
        }

        @Override
        public Builder passes(int passes) {
            if (passes < 0) {
                throw new IllegalArgumentException("passes < 0");
            }

            this.passes = passes;
            return this;
        }

        @Override
        public Builder verify(boolean verify) {
            this.verify = verify;
            return this;
        }

        @Override
        public Builder optimize(boolean optimize) {
            this.optimize = optimize;
            return this;
        }

        @Override
        public Analyzer build() {
            final var config = new Configuration();

            config.optimize = this.optimize;
            config.preverify = this.verify;
            config.keep = List.of();
            config.optimizationPasses = this.passes;
            config.optimizations = List.of("method/propagation/*", "method/inlining/*", "code/*");

            return new AnalyzerImpl(config);
        }
    }
}
