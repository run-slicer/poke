package run.slicer.poke;

public interface Analyzer {
    static Builder builder() {
        return new AnalyzerImpl.Builder();
    }

    byte[] analyze(byte[] b);

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
