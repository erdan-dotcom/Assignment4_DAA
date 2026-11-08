from pathlib import Path
import csv

import matplotlib

matplotlib.use("Agg")

import matplotlib.pyplot as plt


ROOT = Path(__file__).resolve().parent
CSV_PATH = ROOT / "results" / "results.csv"
OUTPUT_DIR = ROOT / "results" / "plots"


def load_results():
    if not CSV_PATH.exists():
        raise FileNotFoundError(
            f"Results file not found: {CSV_PATH}. Run lab.Experiment first."
        )

    with CSV_PATH.open(encoding="utf-8", newline="") as file:
        rows = list(csv.DictReader(file))

    rows = [row for row in rows if row["run"] == "1"]

    integer_columns = (
        "n",
        "m",
        "calls",
        "cells",
        "comparisons",
        "allocated_cells",
    )

    for row in rows:
        for column in integer_columns:
            row[column] = int(row[column])

    return rows


def create_plot(
        rows,
        filename,
        algorithms,
        metric,
        title,
        ylabel,
        takeaway,
        logarithmic_x,
):
    figure, axes = plt.subplots(figsize=(10, 6))

    for algorithm in algorithms:
        if algorithm == "Recursive":
            input_types = ["equal", "worst", "random"]
        elif algorithm in ["Naive", "KMP"]:
            input_types = ["random", "worst"]
        else:
            input_types = ["worst"]

        for input_type in input_types:
            selected = [
                row
                for row in rows
                if row["algorithm"] == algorithm
                   and row["input"] == input_type
            ]

            selected.sort(key=lambda row: row["n"])

            style = "--" if algorithm in ["TwoRows", "KMP"] else "-"

            axes.plot(
                [row["n"] for row in selected],
                [row[metric] for row in selected],
                marker="o",
                linestyle=style,
                label=f"{algorithm} / {input_type}",
            )

    if logarithmic_x:
        axes.set_xscale("log")

    axes.set_yscale("log")
    axes.set_xlabel("Input length n (characters)")
    axes.set_ylabel(ylabel)
    axes.set_title(title)
    axes.grid(True, which="both", alpha=0.2)
    axes.legend()

    figure.text(
        0.5,
        0.025,
        takeaway,
        ha="center",
        fontsize=9,
        wrap=True,
    )

    figure.tight_layout(rect=(0, 0.08, 1, 1))

    output_path = OUTPUT_DIR / filename
    figure.savefig(output_path, dpi=180)

    plt.close(figure)

    print(output_path)


def main():
    rows = load_results()
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    plots = [
        (
            "01_recursion.png",
            ["Recursive"],
            "calls",
            "Recursive edit distance",
            "Recursive calls (count)",
            "Equal strings follow a diagonal; mismatches create rapidly growing repeated work.",
            False,
        ),
        (
            "02_dp_work.png",
            ["Table", "TwoRows"],
            "cells",
            "Dynamic programming work",
            "Computed DP cells (count)",
            "Both DP implementations compute the same n squared cells; curves overlap.",
            True,
        ),
        (
            "03_dp_storage.png",
            ["Table", "TwoRows"],
            "allocated_cells",
            "Dynamic programming storage",
            "Allocated integer entries (count)",
            "The table uses quadratic storage; two rows use linear storage, excluding JVM headers.",
            True,
        ),
        (
            "04_search.png",
            ["Naive", "KMP"],
            "comparisons",
            "String search comparisons",
            "Character comparisons (count)",
            "Long partial matches amplify Naive work; KMP stays linear, including LPS construction.",
            True,
        ),
    ]

    for specification in plots:
        create_plot(rows, *specification)


if __name__ == "__main__":
    main()