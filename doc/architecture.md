## 2 Architecture

The project consists of 4 primary components: a parser, the core logic, the user interface, and the library of known theorems.

The parser package, `rapanui.dsl`, models the abstract syntax tree (AST) for terms, formulas and the rule system DSL. Besides actually parsing input, it provides related services such as dynamically creating a syntax tree, analyzing it, rewriting it, or serializing it back to a string.

The core logic, which is located in the `rapanui.core` package, contains the basic concept classes as described in 4. and the suggestion generation algorithms as described in 5. While it does not directly parse input, the core uses other functionality provided by the parser package and works its AST classes.

The `rapanui.ui` package contains the user interface. It parses user input (using `rapanui.dsl`) before passing it on to the core logic, displays the core concepts defined in `rapanui.core` and maps UI events to calls to their methods.

`rapanui.library` does not contain any executable code, but only the default rule system specified in the DSL.
