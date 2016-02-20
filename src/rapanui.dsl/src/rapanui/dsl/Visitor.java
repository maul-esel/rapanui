package rapanui.dsl;

public interface Visitor {
	default void visit(RuleSystem ruleSystem) {}
	default void visit(Rule rule) {}
	default void visit(Definition definition) {}

	default void visit(Formula formula) {};
	default void visit(Equation equation) { visit((Formula)equation); }
	default void visit(Inclusion inclusion) { visit((Formula)inclusion); }
	default void visit(DefinitionReference reference) { visit((Formula)reference); }

	default void visit(Term term) {};
	default void visit(VariableReference variable) { visit((Term)variable); }
	default void visit(ConstantReference constant) { visit((Term)constant); }
	default void visit(BinaryOperation operation) { visit((Term)operation); }
	default void visit(UnaryOperation operation) { visit((Term)operation); }
}
