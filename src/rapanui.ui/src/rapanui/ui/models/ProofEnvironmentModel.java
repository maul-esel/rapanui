package rapanui.ui.models;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import rapanui.core.ConclusionProcess;
import rapanui.core.ProofEnvironment;
import rapanui.core.ProofEnvironmentObserver;
import rapanui.dsl.Formula;
import rapanui.ui.Application;
import rapanui.ui.commands.*;

public class ProofEnvironmentModel {
	private final ProofEnvironment env;

	public ProofEnvironmentModel(ProofEnvironment env, Application app) {
		assert env != null;
		this.env = env;

		definitionSelectionModel = new DefaultComboBoxModel<String>(app.getRuleSystems().getDefinitionNames());

		createFormulaPremiseCommand = new CreateFormulaPremiseCommand(env, formulaPremiseInputModel);
		createDefinitionReferencePremiseCommand = new CreateDefinitionReferencePremiseCommand(
				env, app, definitionPremiseInputModel, definitionSelectionModel);
		createConclusionCommand = new CreateConclusionCommand(env, conclusionTermInputModel);
	}

	public Formula[] getPremises() {
		return env.getPremises();
	}

	public ConclusionProcess[] getConclusions() {
		return env.getConclusions();
	}

	public void addObserver(ProofEnvironmentObserver observer) {
		env.addObserver(observer);
	}

	public ProofEnvironment getUnderlyingModel() {
		return env;
	}

	public final Document formulaPremiseInputModel = new PlainDocument();
	public final Document definitionPremiseInputModel = new PlainDocument();
	public final Document conclusionTermInputModel = new PlainDocument();

	public final ComboBoxModel<String> definitionSelectionModel;

	public final Action createFormulaPremiseCommand;
	public final Action createDefinitionReferencePremiseCommand;
	public final Action createConclusionCommand;
}
