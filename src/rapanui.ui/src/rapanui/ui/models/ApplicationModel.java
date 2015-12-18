package rapanui.ui.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import rapanui.core.ConclusionProcess;
import rapanui.core.ProofEnvironment;
import rapanui.core.Transformation;
import rapanui.dsl.RuleSystemCollection;
import rapanui.ui.Application;
import rapanui.ui.ApplicationObserver;
import rapanui.ui.commands.CreateEnvironmentCommand;
import rapanui.ui.commands.DeleteEnvironmentCommand;

public class ApplicationModel implements ApplicationObserver {
	private final Application app;

	private final List<Observer> observers = new LinkedList<Observer>();

	private final List<ProofEnvironmentModel> environments = new LinkedList<ProofEnvironmentModel>();
	private ProofEnvironmentModel activeEnvironment = null;

	private final Map<ProofEnvironment, String> environmentNameMap = new HashMap<ProofEnvironment, String>();
	private final Map<String, ProofEnvironmentModel> environmentModelMap = new HashMap<String, ProofEnvironmentModel>();

	// use a counter instead of counting existing ones so there are no duplicates after a deletion
	private int environmentCounter = 1;

	public ApplicationModel(Application app) {
		assert app != null;
		this.app = app;

		createEnvironmentCommand = new CreateEnvironmentCommand(app);
		deleteEnvironmentCommand = new DeleteEnvironmentCommand(this, app);

		environmentNameModel = new DefaultComboBoxModel<String>();
		environmentNameModel.addListDataListener(new ListDataListener() {
			@Override
			public void contentsChanged(ListDataEvent e) {
				activateSelectedEnvironment();
			}
			@Override
			public void intervalAdded(ListDataEvent e) {}
			@Override
			public void intervalRemoved(ListDataEvent e) {}
		});

		for (ProofEnvironment env : app.getEnvironments())
			addEnvironment(env);
		app.addObserver(this);
	}

	public final Action createEnvironmentCommand;
	public final Action deleteEnvironmentCommand;

	public final MutableComboBoxModel<String> environmentNameModel;

	public RuleSystemCollection getRuleSystems() {
		return app.getRuleSystems();
	}

	public ProofEnvironmentModel[] getEnvironments() {
		return environments.toArray(new ProofEnvironmentModel[environments.size()]);
	}

	public ProofEnvironmentModel getActiveEnvironment() {
		return activeEnvironment;
	}

	void loadSuggestions(ProofEnvironment environment, ConclusionProcess conclusion) {
		// TODO
	}

	private void activateSelectedEnvironment() {
		String name = (String)environmentNameModel.getSelectedItem();
		activeEnvironment = environmentModelMap.get(name);
		for (Observer observer : observers)
			observer.environmentActivated(activeEnvironment);
	}

	private ProofEnvironmentModel addEnvironment(ProofEnvironment environment) {
		ProofEnvironmentModel model = new ProofEnvironmentModel(this, environment);
		String name = "Beweis " + environmentCounter++;

		environments.add(model);
		environmentNameModel.addElement(name);

		environmentNameMap.put(environment, name);
		environmentModelMap.put(name, model);

		return model;
	}

	/* ****************************************** *
	 * Observer proxy                             *
	 * ****************************************** */

	public void addObserver(Observer observer) {
		assert observer != null;
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public static interface Observer {
		void environmentCreated(ProofEnvironmentModel environmentModel);
		void environmentDeleted(ProofEnvironmentModel environmentModel);
		void environmentActivated(ProofEnvironmentModel environmentModel);
		void suggestionsLoaded(Transformation[] suggestions);
	}

	@Override
	public void environmentAdded(ProofEnvironment environment) {
		ProofEnvironmentModel model = addEnvironment(environment);
		for (Observer observer : observers)
			observer.environmentCreated(model);
	}

	@Override
	public void environmentRemoved(ProofEnvironment environment) {
		String name = environmentNameMap.get(environment);
		ProofEnvironmentModel model = environmentModelMap.get(name);

		environmentNameModel.removeElement(name);

		environmentNameMap.remove(environment);
		environmentModelMap.remove(name);

		for (Observer observer : observers)
			observer.environmentDeleted(model);
	}
}
