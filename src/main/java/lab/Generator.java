package lab;

import java.util.Random;

public final class Generator {
    public static String random(int length, Random random) {
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            result.append((char) ('A' + random.nextInt(4)));
        }

        return result.toString();
    }

    public static String[] edit(int n, String type) {
        Random random = new Random(42L + n);

        if (type.equals("worst")) {
            return new String[]{
                    "A".repeat(n),
                    "B".repeat(n)
            };
        }

        if (type.equals("equal")) {
            String value = random(n, random);

            return new String[]{value, value};
        }

        return new String[]{
                random(n, random),
                random(n, random)
        };
    }

    public static String[] search(int n, String type) {
        Random random = new Random(42L + n);

        int m = n / 16;

        if (type.equals("worst")) {
            return new String[]{
                    "A".repeat(n),
                    "A".repeat(m - 1) + "B"
            };
        }

        return new String[]{
                random(n, random),
                random(m, random)
        };
    }
}