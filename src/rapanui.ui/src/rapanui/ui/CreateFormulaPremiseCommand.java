package rapanui.ui;

import java.util.function.Supplier;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;

public class CreateFormulaPremiseCommand extends UICommand<ProofEnvironment> {
	private static final Parser parser = new Parser();

	private final Supplier<String> premiseSupplier;

	protected CreateFormulaPremiseCommand(ProofEnvironment target, Supplier<String> premiseSupplier) {
		super(target);
		this.premiseSupplier = premiseSupplier;
	}

	@Override
	public void execute() {
		target.addPremise(parser.parseFormula(premiseSupplier.get()));
	}
}
