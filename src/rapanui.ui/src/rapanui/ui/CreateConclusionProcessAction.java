package rapanui.ui;

import java.util.function.Supplier;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;

public class CreateConclusionProcessAction extends ActionBase<ProofEnvironment> {
	private static final long serialVersionUID = 1L;
	private static final Parser parser = new Parser();

	private final Supplier<String> startTermSupplier;

	public CreateConclusionProcessAction(ProofEnvironment target, Supplier<String> startTermSupplier) {
		super(target);
		this.startTermSupplier = startTermSupplier;
	}

	@Override
	public void execute() {
		target.addConclusion(parser.parseTerm(startTermSupplier.get()));
	}
}
