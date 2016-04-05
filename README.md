# rapanui – Relational Algebra Proof Assistant N' User Interface

rapanui is an interactive proof assistant for relational algebra. It was developed by Dominik Klumpp in 2015/2016 in the course of a practical project (Praxismodul), supervised by Dr. Martin Müller, at the Chair for Programming Methodology and Multimedia Information Systems as part of the Bachelor Course in Informatics at the University of Augsburg.

rapanui is licensed under the MIT license (see [LICENSE](https://github.com/maul-esel/rapanui/blob/master/LICENSE)).

## Concept
rapanui assists the user in conducting proofs in relational algebra. To this end, the user inputs a set of premises and chooses a start term. The application then suggests possible transformations of that term. Once the user applies one of them to the term, new suggestions for the resulting term are offered. This process is repeated until the user has proven the proposition he wishes to proof.

rapanui offers an intuitive graphical user interface. During each step, the suggested transformations are explained thoroughly, which helps the user – specifically first-semester students – improve his or her understanding of relational algebra.

The basis for the suggested transformations is a library of known axioms and theorems of relational algebra, which is specified in an easily understandable textual syntax and can thus easily be modified.

## Quick Start

```
git clone https://github.com/maul-esel/rapanui.git
cd rapanui
./gradlew eclipse run
```

rapanui requires Java 8 to be installed (Eclipse is not required).

## Repository Structure

The `doc/` folder contains documentation on both usage and internal aspects of rapanui.

In `src/` there are 4 primary components, each in its own subdirectory:
* `rapanui.dsl`, a parser for formulas, terms and the theorem library DSL
* `rapanui.core`, the primary logic
* `rapanui.ui`, the user interface and actual application
* `rapanui.library`, the default library of theorems and definitions

In the first three of these subfolders, `build/` contains the gradle build artefacts including generated javadoc and packaged JAR files. After running `./gradlew shadowJar`, `src/rapanui.ui/build/libs/` contains a runnable JAR that includes all dependencies.
