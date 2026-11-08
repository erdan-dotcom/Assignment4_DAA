package lab;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {
    private List<EditDistance> edits() {
        return List.of(new Recursive(), new Table(), new TwoRows());
    }

    private List<StringSearch> searches() {
        return List.of(new Naive(), new KMP());
    }

    @TestFactory
    Stream<DynamicTest> editReferences() {
        String[][] cases = {
                {"kitten", "sitting", "3"},
                {"flaw", "lawn", "2"},
                {"intention", "execution", "5"},
                {"", "abc", "3"},
                {"abc", "abc", "0"},
                {"abc", "", "3"},
                {"", "", "0"},
                {"a", "b", "1"}
        };

        return edits().stream().flatMap(algorithm ->
                Arrays.stream(cases).map(testCase ->
                        DynamicTest.dynamicTest(
                                algorithm.getClass().getSimpleName()
                                        + Arrays.toString(testCase),
                                () -> assertEquals(
                                        Integer.parseInt(testCase[2]),
                                        algorithm.distance(testCase[0], testCase[1])
                                )
                        )
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> randomEdit100() {
        Random random = new Random(42);

        return IntStream.range(0, 100).mapToObj(index -> {
            String a = Generator.random(random.nextInt(7), random);
            String b = Generator.random(random.nextInt(7), random);

            return DynamicTest.dynamicTest("edit random " + index, () -> {
                int expected = new Recursive().distance(a, b);

                assertEquals(expected, new Table().distance(a, b));
                assertEquals(expected, new TwoRows().distance(a, b));

                assertTrue(expected >= Math.abs(a.length() - b.length()));

                assertEquals(expected, new TwoRows().distance(b, a));
            });
        });
    }

    @Test
    void lpsReferences() {
        KMP kmp = new KMP();

        assertArrayEquals(
                new int[]{0, 1, 2, 0, 1, 2, 3, 3},
                kmp.lps("AAACAAAA")
        );

        assertArrayEquals(
                new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4},
                kmp.lps("ABABCABAB")
        );

        assertArrayEquals(new int[0], kmp.lps(""));
        assertArrayEquals(new int[]{0}, kmp.lps("A"));
    }

    @TestFactory
    Stream<DynamicTest> searchReferences() {
        String[] texts = {
                "ABABDABACDABABCABAB",
                "AAAAA",
                "ABABABA",
                "",
                "ABC",
                "ABC",
                "ABC",
                "AAAA"
        };

        String[] patterns = {
                "ABABCABAB",
                "AA",
                "ABA",
                "A",
                "ABCD",
                "Z",
                "ABC",
                "A"
        };

        List<List<Integer>> expected = List.of(
                List.of(10),
                List.of(0, 1, 2, 3),
                List.of(0, 2, 4),
                List.of(),
                List.of(),
                List.of(),
                List.of(0),
                List.of(0, 1, 2, 3)
        );

        return searches().stream().flatMap(algorithm ->
                IntStream.range(0, texts.length).mapToObj(index ->
                        DynamicTest.dynamicTest(
                                algorithm.getClass().getSimpleName()
                                        + " reference " + index,
                                () -> assertEquals(
                                        expected.get(index),
                                        algorithm.search(texts[index], patterns[index])
                                )
                        )
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> randomSearch100() {
        Random random = new Random(42);

        return IntStream.range(0, 100).mapToObj(index -> {
            String text = Generator.random(random.nextInt(100), random);
            String pattern = Generator.random(1 + random.nextInt(12), random);

            return DynamicTest.dynamicTest("search random " + index, () -> {
                List<Integer> expected = new ArrayList<>();

                for (int start = 0; start <= text.length() - pattern.length(); start++) {
                    if (text.startsWith(pattern, start)) {
                        expected.add(start);
                    }
                }

                assertEquals(expected, new Naive().search(text, pattern));
                assertEquals(expected, new KMP().search(text, pattern));
            });
        });
    }

    @Test
    void emptyPattern() {
        for (StringSearch algorithm : searches()) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> algorithm.search("abc", "")
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> algorithm.search("", "")
            );
        }
    }

    @Test
    void countersReset() {
        for (EditDistance algorithm : edits()) {
            algorithm.distance("ab", "cd");

            long calls = algorithm.metrics().calls;
            long cells = algorithm.metrics().cells;

            algorithm.distance("ab", "cd");

            assertEquals(calls, algorithm.metrics().calls);
            assertEquals(cells, algorithm.metrics().cells);
        }

        for (StringSearch algorithm : searches()) {
            algorithm.search("AAAA", "AB");

            long comparisons = algorithm.metrics().comparisons;

            algorithm.search("AAAA", "AB");

            assertEquals(comparisons, algorithm.metrics().comparisons);
        }
    }

    @Test
    void exactCounters() {
        Table table = new Table();
        TwoRows twoRows = new TwoRows();

        table.distance("abc", "de");
        twoRows.distance("abc", "de");

        assertEquals(6, table.metrics().cells);
        assertEquals(6, twoRows.metrics().cells);

        assertEquals(12, table.metrics().allocatedCells);
        assertEquals(6, twoRows.metrics().allocatedCells);

        Naive naive = new Naive();
        naive.search("AAAAA", "AAB");

        assertEquals(9, naive.metrics().comparisons);
    }

    @Test
    void randomLpsReference() {
        Random random = new Random(19);
        KMP kmp = new KMP();

        for (int trial = 0; trial < 100; trial++) {
            String pattern = Generator.random(1 + random.nextInt(30), random);

            int[] expected = new int[pattern.length()];

            for (int i = 0; i < pattern.length(); i++) {
                for (int length = 1; length <= i; length++) {
                    String prefix = pattern.substring(0, length);
                    String suffix = pattern.substring(i - length + 1, i + 1);

                    if (prefix.equals(suffix)) {
                        expected[i] = length;
                    }
                }
            }

            assertArrayEquals(expected, kmp.lps(pattern));
        }
    }
}
