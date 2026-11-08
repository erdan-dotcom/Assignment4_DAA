package lab;

public final class TwoRows implements EditDistance {
    private final Metrics metrics = new Metrics();

    @Override
    public Metrics metrics() {
        return metrics;
    }

    @Override
    public int distance(String a, String b) {
        metrics.reset();

        int n = a.length();
        int m = b.length();

        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];

        metrics.allocatedCells += previous.length;
        metrics.allocatedCells += current.length;

        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= n; i++) {
            current[0] = i;

            for (int j = 1; j <= m; j++) {
                metrics.cells++;
                metrics.comparisons++;

                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;

                int deletion = previous[j] + 1;
                int insertion = current[j - 1] + 1;
                int replacement = previous[j - 1] + cost;

                current[j] = Math.min(
                        deletion,
                        Math.min(insertion, replacement)
                );
            }

            int[] temporary = previous;
            previous = current;
            current = temporary;
        }

        return previous[m];
    }
}
