package rapanui.dsl;

public interface Visitor {
	default void visit(RuleSystem ruleSystem) {}
	default void visit(Rule rule) {}
	default void visit(Definition definition) {}

	default void visit(Formula formula) {};
	default void visit(Equation equation) {};
	default void visit(Inclusion inclusion) {};
	default void visit(DefinitionReference reference) {};

	default void visit(Term term) {};
	default void visit(VariableReference variable) {};
	default void visit(ConstantReference constant) {};
	default void visit(BinaryOperation operation) {};
	default void visit(UnaryOperation operation) {};
}
