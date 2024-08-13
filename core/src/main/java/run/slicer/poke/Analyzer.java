package run.slicer.poke;

import java.util.List;

public interface Analyzer {
    static Builder builder() {
        return new AnalyzerImpl.Builder();
    }

    List<? extends Entry> analyze(Iterable<? extends Entry> entries);

    default byte[] analyze(byte[] b) {
        return this.analyze(Entry.of(null, b)).getFirst().data();
    }

    default List<? extends Entry> analyze(Entry... entries) {
        return this.analyze(List.of(entries));
    }

    interface Builder {
        Builder passes(int passes);

        Builder verify(boolean verify);

        default Builder verify() {
            return this.verify(true);
        }

        Builder optimize(boolean optimize);

        default Builder optimize() {
            return this.optimize(true);
        }

        Analyzer build();
    }
}
