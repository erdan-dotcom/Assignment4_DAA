package lab;

import java.util.Objects;

public final class Recursive implements EditDistance {
    private final Metrics metrics = new Metrics();

    @Override
    public Metrics metrics() {
        return metrics;
    }

    @Override
    public int distance(String a, String b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        metrics.reset();

        return solve(a, b, a.length(), b.length());
    }

    private int solve(String a, String b, int i, int j) {
        metrics.calls++;

        if (i == 0) {
            return j;
        }

        if (j == 0) {
            return i;
        }

        metrics.comparisons++;

        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return solve(a, b, i - 1, j - 1);
        }

        int deletion = solve(a, b, i - 1, j);
        int insertion = solve(a, b, i, j - 1);
        int replacement = solve(a, b, i - 1, j - 1);

        return 1 + Math.min(deletion, Math.min(insertion, replacement));
    }
}