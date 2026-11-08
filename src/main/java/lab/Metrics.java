package lab;

public final class Metrics {
    public long calls;
    public long cells;
    public long comparisons;
    public long allocatedCells;

    public void reset() {
        calls = 0;
        cells = 0;
        comparisons = 0;
        allocatedCells = 0;
    }
}
