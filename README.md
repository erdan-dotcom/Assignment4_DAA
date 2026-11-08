# Assignment 4: Dynamic Programming and String Algorithms

Student: Orynbassarov Yerdan  
Group: SE-2525

## Overview

This project implements three versions of Levenshtein edit distance and two string-search algorithms.

Edit distance supports insertion, deletion and replacement, each with cost 1. String search returns all matching start positions in increasing order, including overlapping matches.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer
- Python 3
- Matplotlib

## Algorithms

| Class | Purpose | Time complexity | Auxiliary space |
|---|---|---|---|
| Recursive | Edit distance without memoization | Exponential in the worst case | O(n + m) recursion stack |
| Table | Bottom-up edit distance | O(nm + n + m) | O((n + 1)(m + 1)) |
| TwoRows | Edit distance using two rows | O(nm + n + m) | O(m + 1) |
| Naive | Try every possible match position | O((n - m + 1)m) when m <= n | O(1), excluding output |
| KMP | Prefix-based string search | O(n + m) | O(m), excluding output |

Here, n is the first string or text length, and m is the second string or pattern length.

For nonempty edit-distance inputs, the DP time complexity is normally written as O(nm). Boundary initialization also handles empty strings.

## DP Design

State: D(i, j) is the minimum number of edits needed to transform the first i characters of a into the first j characters of b.

Base cases: D(0, j) = j and D(i, 0) = i.

Transition: if the last characters are equal, D(i, j) = D(i - 1, j - 1). Otherwise, D(i, j) = 1 + min(D(i - 1, j), D(i, j - 1), D(i - 1, j - 1)).

Recursive evaluates the recurrence directly without storing results. Table stores every state. TwoRows stores only the previous and current rows because the transition needs no older rows.

The Java implementations intentionally contain no comments. The DP design is documented here and in REPORT.md; this differs from the assignment's request to place it in code comments.

## Build and Test

Run these commands from the project root:

```bash
mvn clean test
```

The test suite contains 245 JUnit test cases.

Coverage includes:

- All required edit-distance reference values.
- Empty strings and equal strings.
- Agreement between all three versions on 100 random short string pairs.
- Edit-distance symmetry and a length-difference lower bound.
- Required LPS reference arrays.
- An additional brute-force LPS check on 100 random patterns.
- All required search references and overlapping matches.
- Search agreement with an independent reference on 100 random texts.
- Empty-pattern exceptions.
- Patterns longer than the text.
- Counter resets and selected exact counter values.

## Run the Experiment

```bash
java -cp target/classes lab.Experiment
```

Alternatively, run the main method of lab.Experiment in IntelliJ IDEA with the project root as the working directory.

The experiment creates:

- results/results.csv

It runs 76 configurations three times each, producing 228 data rows.

## Generate Plots

On Windows:

```bash
py -m pip install matplotlib
py plotter.py
```

On systems using the python3 command:

```bash
python3 -m pip install matplotlib
python3 plotter.py
```

The script creates:

- results/plots/01_recursion.png
- results/plots/02_dp_work.png
- results/plots/03_dp_storage.png
- results/plots/04_search.png

Each plot includes a title, labeled axes, a legend and a takeaway sentence.

## Experiment Inputs

Recursive edit distance uses n = 2, 4, 6, 8.

Table and TwoRows use n = 2, 4, 6, 8, 64, 128, 256, 512.

Edit-distance inputs are equal strings, independent random strings and A repeated n times against B repeated n times.

String search uses n = 512, 2048, 8192, 32768 and m = n / 16.

Search inputs are independent random strings and a difficult partial-match case: text A^n and pattern A^(m-1)B.

The random alphabet is A, B, C, D. The generator uses the deterministic seed 42 + n for each size.

## CSV Columns

| Column | Meaning |
|---|---|
| algorithm | Implementation name |
| input | Input type |
| n | First string or text length |
| m | Second string or pattern length |
| run | Repetition number, from 1 to 3 |
| time_ms | Duration of this run in milliseconds |
| median_ms | Median duration of the three runs |
| calls | Recursive calls, including base cases |
| cells | Computed interior DP cells |
| comparisons | Character comparisons, including KMP preprocessing |
| allocated_cells | Allocated integer-array entries |
| result | Edit distance or number of matches |

Counters are incremented inside the algorithms.

Allocated entries are a storage indicator, not a measurement of total JVM memory. Object headers, references, recursion stacks and result lists are excluded.

Timing includes the algorithm call and its result validation. It excludes input generation, reference-result generation and CSV writing.

## Reproducibility and Limitations

Inputs, answers and operation counts are deterministic. Timing depends on hardware, JVM optimization, allocation and operating-system activity.

The experiment uses three runs per configuration and reports their median. It is a small educational benchmark without a dedicated JVM warm-up phase.

The plotting script selects one run per configuration because the experiment verifies that all three runs have identical counters.

Recursive edit distance is deliberately limited to small inputs. The experiment does not claim that n = 8 is the largest possible input on every machine.

## AI-Use Acknowledgement

OpenAI ChatGPT was used to assist with implementation, test design, plotting and documentation. The supplied implementation was compiled and its JUnit tests were executed in the assistant's environment. Local results can be reproduced using the commands above.

## Submission

Include the source files, pom.xml, plotter.py, README.md, REPORT.md, results/results.csv and results/plots/.

Use branch main and tag the final submission v1.0.

Submit the GitHub repository link and the archive requested by the assignment through Moodle.