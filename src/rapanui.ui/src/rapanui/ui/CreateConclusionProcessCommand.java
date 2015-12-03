package rapanui.ui;

import java.util.function.Supplier;

import rapanui.core.ProofEnvironment;
import rapanui.dsl.Parser;

public class CreateConclusionProcessCommand extends UICommand<ProofEnvironment> {
	private static final Parser parser = new Parser();

	private final Supplier<String> startTermSupplier;

	public CreateConclusionProcessCommand(ProofEnvironment target, Supplier<String> startTermSupplier) {
		super(target);
		this.startTermSupplier = startTermSupplier;
	}

	@Override
	public void execute() {
		target.addConclusion(parser.parseTerm(startTermSupplier.get()));
	}
}
