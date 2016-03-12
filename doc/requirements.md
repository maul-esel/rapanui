## 1 Requirements

### 1.1 Concept and Features

***rapanui*** assists the user in proving theorems of relational algebra using a simple and intuitive graphical user interface.

To this end, the user first inputs a set of premises and an initial term into the graphical user interface. The software then offers possible transformations of the term and displays detailed proof of their correctness. Once the user chooses one of these suggestions, the software applies it to the term, resulting in a new term. Subsequently, the software in turn offers possible transformations of that term. This process repeats until the user reaches the desired term.

The user can execute multiple such conclusion processes at the same time, for example to prove lemmata. The software uses such lemmata to generate the transformation suggestions. Furthermore, the user can switch between multiple proofs with different premises.

Amongst other strategies, the software uses a rule system of known theorems of relational algebra to generate the transformation suggestions. This rule system is not statically programmed into the software, but is specified in an easily understandable textual syntax and is loaded dynamically. This allows the user to modify these theorems.

The rule system also allows definition of commonly used concepts such as "reflexive", "equivalence relation" etc. This allows these concepts to be referenced later instead of always repeating their definition.

### 1.2 Technology and Implementation

The application is developed in Java 8. For the UI, the Swing framework included with Java is used. To model the syntax and generate AST classes, a lexer and a parser from a grammar, the Eclipse Xtext framework is used. Further details follow in 3.3.1 and 6.1 respectively.

### 1.3 Comparison with Other Software

A quick comparison with other tools for interactive proofs, particularly with KIV, Isabelle and Coq shows the following:

All three projects are much more complex. They don't focus on relational algebra, but rather they support extensive formulas form different areas of mathematics, working with different data types and concrete sets, functions etc. This project in contrast confines itself to relational algebra. Furthermore, usability especially for beginners (e.g. first semester students) is a key aspect.

Like this application, all three also use a textual DSL for specification. However, they are much more extensive. Especially Isabelle has a very flexible, customizable language, which allows to user to define new notations within the DSL. Overall, Isabelle has a very modular design, which allows the functionality to be organized in many different packages including data types, axioms and in some cases even proof strategies.

Like this application, the applications also come with extensive libraries of known theorems as basis for proofs. In the case of KIV, those include the theorem's proofs (specified in the DSL), whereas rapanui's library does not.

In addition to interactive proofs of mathematical formulas, it is also possible to verify proofs and algorithms specified in the respective DSL.

#### Sources:
  * "KIV: overview and VerifyThis competition" (2012), Gidon Ernst et. al.
  * Isabelle Website (isabelle.in.tum.de)
  * Coq Webiste (coq.inria.fr)
