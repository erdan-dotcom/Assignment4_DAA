package lab;

public final class Table implements EditDistance {
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

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            metrics.allocatedCells += dp[i].length;
            dp[i][0] = i;
        }

        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                metrics.cells++;
                metrics.comparisons++;

                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;

                int deletion = dp[i - 1][j] + 1;
                int insertion = dp[i][j - 1] + 1;
                int replacement = dp[i - 1][j - 1] + cost;

                dp[i][j] = Math.min(
                        deletion,
                        Math.min(insertion, replacement)
                );
            }
        }

        return dp[n][m];
    }
}