package lab;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.IntSupplier;

public final class Experiment {
    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("results"));

        Path output = Path.of("results", "results.csv");

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output))) {
            writer.println(
                    "algorithm,input,n,m,run,time_ms,median_ms,"
                            + "calls,cells,comparisons,allocated_cells,result"
            );

            int[] editSizes = {2, 4, 6, 8, 64, 128, 256, 512};
            String[] editTypes = {"equal", "worst", "random"};

            for (int n : editSizes) {
                for (String type : editTypes) {
                    String[] pair = Generator.edit(n, type);

                    int expected = new Table().distance(pair[0], pair[1]);

                    List<EditDistance> algorithms = new ArrayList<>(
                            List.of(new Table(), new TwoRows())
                    );

                    if (n <= 8) {
                        algorithms.add(new Recursive());
                    }

                    for (EditDistance algorithm : algorithms) {
                        measure(
                                writer,
                                algorithm.getClass().getSimpleName(),
                                type,
                                n,
                                n,
                                algorithm.metrics(),
                                () -> {
                                    int result = algorithm.distance(pair[0], pair[1]);

                                    if (result != expected) {
                                        throw new AssertionError("Edit distance disagreement");
                                    }

                                    return result;
                                }
                        );
                    }
                }
            }

            int[] searchSizes = {512, 2048, 8192, 32768};
            String[] searchTypes = {"random", "worst"};

            for (int n : searchSizes) {
                for (String type : searchTypes) {
                    String[] pair = Generator.search(n, type);

                    List<Integer> expected = new Naive().search(pair[0], pair[1]);

                    for (StringSearch algorithm : List.of(new Naive(), new KMP())) {
                        measure(
                                writer,
                                algorithm.getClass().getSimpleName(),
                                type,
                                n,
                                pair[1].length(),
                                algorithm.metrics(),
                                () -> {
                                    List<Integer> result = algorithm.search(pair[0], pair[1]);

                                    if (!result.equals(expected)) {
                                        throw new AssertionError("String search disagreement");
                                    }

                                    return result.size();
                                }
                        );
                    }
                }
            }
        }

        System.out.println("Experiment completed: 228 raw runs, 76 configurations");
        System.out.println("Results: " + output.toAbsolutePath());
    }

    private static void measure(
            PrintWriter writer,
            String algorithm,
            String input,
            int n,
            int m,
            Metrics metrics,
            IntSupplier action
    ) {
        long[] times = new long[3];
        String[] counters = new String[3];

        int result = 0;

        for (int run = 0; run < 3; run++) {
            long start = System.nanoTime();

            result = action.getAsInt();

            times[run] = System.nanoTime() - start;

            counters[run] = metrics.calls + ","
                    + metrics.cells + ","
                    + metrics.comparisons + ","
                    + metrics.allocatedCells;
        }

        if (!counters[0].equals(counters[1])
                || !counters[0].equals(counters[2])) {
            throw new AssertionError("Counters differ between repeated runs");
        }

        long[] sorted = times.clone();
        Arrays.sort(sorted);

        double median = sorted[1] / 1_000_000.0;

        for (int run = 0; run < 3; run++) {
            writer.printf(
                    Locale.ROOT,
                    "%s,%s,%d,%d,%d,%.9f,%.9f,%s,%d%n",
                    algorithm,
                    input,
                    n,
                    m,
                    run + 1,
                    times[run] / 1_000_000.0,
                    median,
                    counters[run],
                    result
            );
        }
    }
}
