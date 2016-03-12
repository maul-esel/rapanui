## 3 Rule System

### 3.1 DSL Implementation using Xtext and Xcore

The rule system specification as well as parsing of terms and formulas are implemented with a domain-specific language (DSL), which is built using the Eclipse Xtext framework. This allows the generation of lexer, parser, AST classes and many related services from a simple grammar.

To have the ability to customize the AST classes, they are not generated by Xtext but instead specified using the Xcore language.

The AST data types which are actually used to pass instances are only interfaces, the actual generated implementation classes are never used explicitly.

Xtext also generates an Eclipse plugin which enables IDE features such as syntax highlighting, auto-format, validation and much more. Some of these however require more custom code to be added.

### 3.2 Supported Terms and Formulas

***rapanui*** supports a limited set of terms and formulas of relational algebra.

#### Terms

All terms' values are homogenous binary relations over the same fixed, but arbitrary set.

There are 3 constants:

* the empty set `∅`
* the identity relation `I`
* the universal relation `Π`

The capital letters `A` to `H` and `J` to `Z` denote variables. The scope of a variable is always the entire proof (in the application) or the containing rule (in the rule system).

Applicable operators are:

* converse `R˘`
* complement `Rᶜ`
* transitive closure `R⁺`
* reflexive transitive closure `R*`
* intersection `R ∩ S`
* union `R ∪ S`
* set difference `R \ S`
* composition `R ; S `

Unary operators have higher precedence than binary operators. Among the binary operators, the precedence order is composition (highest precedence), set difference, intersection and union (lowest precedence). A chain of operators is interpreted from left to right, so `R ; S ; T*` is read as `(R ; S) ; (T*)`. To override either of these, use parentheses: `R ; (S ; T)*`.

#### Formulas

The only allowed formulas are equality (`R = S`) and inclusion (`R ⊆ S`) of two terms.

#### Limitations

Many of these limitations can be overcome by replacing a formula with one or multiple equivalent formulas. For example, one can show that the basic set is empty by showing `I = ∅` or `Π = ∅`.

### 3.3 Definitions and Definition References

The rule system contains definitions. A definition consists of a set of premises, a "target" variable and a name. The syntax looks like this:

    define R as "equivalence relation"
      iff I ⊆ R
      and R ; R ⊆ R
      and R˘ ⊆ R

Here, the target variable is `R`, the name is `equivalence relation` and the premises are `I ⊆ R`, `R ; R ⊆ R` and `R˘ ⊆ R`.

If all of a definition's premises, with the target variable substituted for a given term `t`, are true, then that definition applies to `t`. In the example above, if `I ⊆ S* ; T`, `(S* ; T) ; (S* ; T) ⊆ S* ; T` and `(S* ; T)˘ ⊆ S* ; T` are true, then `S* ; T` is `reflexive`.

A statement of the form `<term> is "<definition name>"` is called a definition reference. It can be used both in the rule system itself and as premise in the application as a placeholder for the definition's premises where the target variable was substituted for the given term. So in the example above, `T ; R* is "equivalence relation"` is a placeholder for `I ⊆ T ; R*`, `(T ; R*) ; (T ; R*) ⊆ T ; R*` and `(T ; R*)˘ ⊆ T ; R*`.

The superordinate concept of formulas and definition references is "predicate" (i.e. a predicate can either be a formula or a definition reference).

### 3.4 Rules

Rules represent known theorems of relational algebra which are used to derive correct transformations to suggest to the user.

A rule consists of a set of zero or more premises, a set of one or more conclusions and a name. Any predicate (i.e. a formula or a definition reference) can be used as a premise or a conclusion.

In this context, rules with zero premises are called axioms:

    axiom "equality is reflexive"
      always M = M

and rules with at least one premise are called theorems:

    theorem "inclusion is antisymmetric"
      if M ⊆ N
      and N ⊆ M
      then M = N
      and N = M

Their semantic meaning is that if **all** the predicates are true, then **all** the conclusions are also true. So if a rule has premises `{ p1, p2, ..., pn }` and conclusions `{ c1, c2, ..., cm }`, then it represents the mathematical formula

    (p1 ∧ p2 ∧ ... ∧ pn) ⇒ (c1 ∧ c2 ∧ ... ∧ cm)