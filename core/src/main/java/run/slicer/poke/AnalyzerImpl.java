package run.slicer.poke;

import proguard.AppView;
import proguard.Configuration;
import proguard.classfile.ClassPool;
import proguard.classfile.ProgramClass;
import proguard.classfile.io.ProgramClassReader;
import proguard.classfile.io.ProgramClassWriter;
import proguard.classfile.pass.PrimitiveArrayConstantIntroducer;
import proguard.classfile.util.PrimitiveArrayConstantReplacer;
import proguard.optimize.LineNumberTrimmer;
import proguard.optimize.peephole.LineNumberLinearizer;
import proguard.preverify.PreverificationClearer;
import proguard.preverify.Preverifier;
import proguard.preverify.SubroutineInliner;
import run.slicer.poke.proguard.Optimizer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

record AnalyzerImpl(Configuration config) implements Analyzer {
    @Override
    public List<? extends Entry> analyze(Iterable<? extends Entry> entries) {
        final Map<Entry, ProgramClass> classes = new HashMap<>();
        for (final Entry entry : entries) {
            final var clazz = new ProgramClass();
            classes.put(entry, clazz);

            clazz.accept(new ProgramClassReader(new DataInputStream(new ByteArrayInputStream(entry.data()))));
        }

        final var pool = new ClassPool(classes.values());
        final var view = new AppView(pool, new ClassPool());

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
            pool.classesAccept(new PrimitiveArrayConstantReplacer());
        }
        if (config.preverify) {
            new Preverifier(config).execute(view);
        }

        if (config.preverify || willOptimize) {
            new LineNumberTrimmer().execute(view);
        }

        return classes.entrySet()
                .stream()
                .map(e -> {
                    final var output = new ByteArrayOutputStream();
                    e.getValue().accept(new ProgramClassWriter(new DataOutputStream(output)));

                    return e.getKey().withData(output.toByteArray());
                })
                .toList();
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
        private static final boolean PEEPHOLE = Boolean.parseBoolean(
                System.getProperty("run.slicer.poke.peephole", "true")
        );
        private static final boolean EVALUATION = Boolean.parseBoolean(
                System.getProperty("run.slicer.poke.evaluation", "true")
        );

        private int passes = 1;
        private boolean verify = false;
        private boolean optimize = false;
        private boolean inline = false;

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
        public Builder inline(boolean inline) {
            this.inline = inline;
            return this;
        }

        @Override
        public Analyzer build() {
            final var config = new Configuration();

            config.optimize = this.optimize;
            config.preverify = this.verify;
            config.optimizationPasses = this.passes;

            final List<String> optimizations = new ArrayList<>(List.of(
                    "field/*",
                    "method/generalization/*",
                    "method/specialization/*",
                    "method/propagation/*",
                    "code/merging",
                    "code/removal/*",
                    "code/allocation/*"
            ));

            if (this.inline) {
                optimizations.add("method/inlining/*");
            }
            if (PEEPHOLE) {
                optimizations.add("code/simplification/variable");
                optimizations.add("code/simplification/arithmetic");
                optimizations.add("code/simplification/cast");
                optimizations.add("code/simplification/field");
                optimizations.add("code/simplification/branch");
                optimizations.add("code/simplification/object");
                optimizations.add("code/simplification/string");
                optimizations.add("code/simplification/math");
            }
            if (EVALUATION) {
                optimizations.add("code/simplification/advanced");
            }
            config.optimizations = optimizations;

            return new AnalyzerImpl(config);
        }
    }
}
