package rapanui.ui;

import java.util.function.Supplier;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;

public class CreateFormulaPremiseCommand extends UICommand<ProofEnvironment> {
	private final Supplier<String> premiseSupplier;

	protected CreateFormulaPremiseCommand(ProofEnvironment target, Supplier<String> premiseSupplier) {
		super(target);
		this.premiseSupplier = premiseSupplier;
	}

	@Override
	public void execute() {
		target.addPremise(Parser.getInstance().parseFormula(premiseSupplier.get()));
	}
}
