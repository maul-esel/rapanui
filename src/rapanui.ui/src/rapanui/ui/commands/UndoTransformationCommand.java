package rapanui.ui.commands;

import java.util.Collection;

import rapanui.core.ConclusionProcess;
import rapanui.core.Transformation;
import rapanui.ui.models.ConclusionProcessModel;

public class UndoTransformationCommand extends AbstractCommand implements ConclusionProcessModel.Observer {
	private static final long serialVersionUID = 1L;

	private final ConclusionProcessModel model;
	private final ConclusionProcess conclusion;

	public UndoTransformationCommand(ConclusionProcessModel model, ConclusionProcess conclusion) {
		super("\u27F2", "Letzter Schritt rückgängig");
		this.conclusion = conclusion;
		this.model = model;

		updateEnabled();
		model.addObserver(this);
	}

	@Override
	public void execute() {
		model.undoTransformation();
	}

	@Override
	protected boolean canExecute() {
		return conclusion.getTransformations().length > 0;
	}

	@Override
	public void transformationAdded(Transformation transformation) {
		updateEnabled();
	}

	@Override
	public void transformationRemoved(Transformation transformation) {
		updateEnabled();
	}

	@Override public void activated() {}
	@Override public void deactivated() {}
	@Override public void titleChanged(String newTitle) {}
	@Override public void highlightRequested(Collection<Transformation> transformations) {}
	@Override public void unhighlightRequested() {}
}
