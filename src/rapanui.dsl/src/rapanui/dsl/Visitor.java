package rapanui.dsl;

public interface Visitor {
	default void visit(RuleSystem ruleSystem) {}
	default void visit(Rule rule) {}
	default void visit(Definition definition) {}

	default void visit(Predicate predicate) {};
	default void visit(DefinitionReference reference) { visit((Predicate)reference); }
	default void visit(Formula formula) { visit((Predicate)formula); };

	default void visit(Term term) {};
	default void visit(VariableReference variable) { visit((Term)variable); }
	default void visit(ConstantReference constant) { visit((Term)constant); }
	default void visit(BinaryOperation operation) { visit((Term)operation); }
	default void visit(UnaryOperation operation) { visit((Term)operation); }
}
