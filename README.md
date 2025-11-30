# advent-of-code-2024-kotlin

This repository contains my solutions for [Advent of Code 2025](https://adventofcode.com/2025)
in [Kotlin](https://kotlinlang.org).

## How to run solutions

> [!NOTE]
>
> All task input files (`src/*.txt`) and test data are excluded from the repository with `.gitignore` – we should not
> post them publicly, as [Eric Wastl requested for](https://twitter.com/ericwastl/status/1465805354214830081).

Before running a day file bring your input into the `src` folder.  
For the most days it's going to be just 2 files `DayXX.txt` and `DayXX_test.txt` but there are exceptions where more
input files required.
See inside interested day for exact required input files.  
See more about project structure in [project template](#project-template).

**Example**

```
.
├── README.md               README file
│   ....
└── src
    ├── Day01.kt            Day 01 implementation
    ├── Utils.kt            A set of utility methods shared across your days
    │                       
    │                       (create those files manually)
    ├── Day01.txt           Day 01 input data
    └── Day01_test.txt      Day 01 test input data
```

## About Advent of Code

Advent of Code – An annual event of Christmas-oriented programming challenges started December 2015.  
Every year since then, beginning on the first day of December, a programming puzzle is published every day for
twenty-five days.
You can solve the puzzle and provide an answer using the language of your choice.

## Project template

This repository is based on the following project
template - https://github.com/kotlin-hands-on/advent-of-code-kotlin-template