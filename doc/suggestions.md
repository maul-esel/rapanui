# Suggestion Generation
## Approaches
There are 4 basic approaches to suggestions, as reflected by the `Justification` subclasses and `SuggestionStrategy` implementations. They are based on the environment's premises, conclusions previously made by the user, application of the given rules and equality of subterms respectively.

Below, the term "formula type" means definition reference, inclusion or equality. The "relevant sides" of a formula are for definition references the left (the term to which the definition is applied), for inclusion also the left, and for equations both left- and right-hand side of the equals sign.

### 1. Premise suggestions
A simple approach is to iterate through all (resolved) premises in the given environment. If the input term equals one of a premise's relevant sides, the premise can either immediately be converted into a suggestion, or, if it's an equation with the term appearing on the right, only has to be reversed.

### 2. Previous conclusions
If the input term appears anywhere in a conclusion, all terms that are, according to the conclusion, equal or greater can be paired with the input term to create a suggestion.

### 3. Subterm equality
It is possible to iterate through all subterms of the input term and search for equality suggestions for each such subterm (recursion). For each result, the corresponding subterm can be replaced in the input term, and original and modified term form a valid suggestion.

### 4. Rule applications
Suggestions can also be obtained by iterating through all (resolved) conclusions of all rules in the rule system collection. If one of the relevant sides is structurally compatible with the input term, matching variable translations are sought. For each such translation, the premises are also translated. Before the suggestion can be made, the premises now also have to be justified.

## Abstraction
Any algorithm for justifying the premises will use the same principles as described above. Therefore it would be useful to implement them together. The difference is that here, not only one "relevant side" and a formula type are given, but a complete formula.

Since neither in case of suggestions, nor in principles 3 and 4 a definition reference would be searched for (but instead its resolved premises, which are either equations or inclusions), a search request is basically a tuple `(t1, rel, t2)` where one of the terms or the relation between them (or both) can be unknown/arbitrary:
* A search by the user would have the form `(t1, ?, ?)` or `(t1, =, ?)` depending on the filter settings,
* a search by the algorithm in 3. would also have the latter form,
* and a search to justify premises would be the full `(t1, rel, t2)`, where `rel` is either `=` or `?`.  (Note: since equality implies inclusion, it would be possible to use `?` (i.e. `null`) instead of `?` - if not, this would require an additional recursion step via the appropriate rule). 

The algorithms above would require some more or less minor adjustments to fit such a request.

## Recursion depth
Another problem is the potentially occuring infinite recursion in the last two principles. To avoid this, a recursion depth limit must be set.

## Interfaces
A `JustificationRequest` class has to be implemented, with attributes and getters for the three components of the field, and possibly helper methods to test if a formula in some way "matches" the request (structurally or completely). To abstract further, maybe an interface with only those helper methods can be introduced later.

To account for such general requests, the `SuggestionStrategy` interface has to be adjusted (and renamed to `JustificationFinder`). It should now take a `JustificationRequest` instance and a `ProofEnvironment`.

In order to provide client code with a simpler interface and to efficiently support the recursion, a search manager should be implemented, like the `SuggestionFinder` class right now. Given a `JustificationFinder`, it should accept a search request in the old form of a `ConclusionProcess` and a `FormulaType`, and in return emit transformations instead of justifications.

The `JustificationFinder` given to a `SuggestionFinder` would typically be an aggregation of the above strategies. The employed sub-strategies would receive this aggregation in their constructor, so they can delegate to it.

There should be a static method that creates a new `SuggestionFinder` and configures it with the implemented strategies.