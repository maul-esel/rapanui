package rapanui.ui;

import java.util.function.Supplier;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;

public class CreateFormulaPremiseAction extends ActionBase<ProofEnvironment> {
	private static final long serialVersionUID = 1L;
	private static final Parser parser = new Parser();

	private final Supplier<String> premiseSupplier;

	protected CreateFormulaPremiseAction(ProofEnvironment target, Supplier<String> premiseSupplier) {
		super(target);
		this.premiseSupplier = premiseSupplier;
	}

	@Override
	public void execute() {
		target.addPremise(parser.parseFormula(premiseSupplier.get()));
	}
}
