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

	// needed to proxy environment removals
	private final Map<ProofEnvironment, ProofEnvironmentModel> environmentModelMap = new HashMap<ProofEnvironment, ProofEnvironmentModel>();

	// needed to activate the environment with the selected name
	private final Map<String, ProofEnvironmentModel> nameModelMap = new HashMap<String, ProofEnvironmentModel>();

	// needed for environment names (use a counter instead of counting existing ones so there are no duplicates after a deletion)
	private int environmentCounter = 1;

	public ApplicationModel(Application app) {
		assert app != null;
		this.app = app;

		createEnvironmentCommand = new CreateEnvironmentCommand(app);
		deleteEnvironmentCommand = new DeleteEnvironmentCommand(this);

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

	public void removeEnvironment(ProofEnvironmentModel environmentModel) {
		assert environmentModel != null;
		app.removeEnvironment(environmentModel.getUnderlyingModel());
	}

	void loadSuggestions(ProofEnvironment environment, ConclusionProcess conclusion) {
		// TODO
	}

	/* ****************************************** *
	 * private helper methods                     *
	 * ****************************************** */

	private void activateSelectedEnvironment() {
		String name = (String)environmentNameModel.getSelectedItem();
		activateEnvironment(nameModelMap.get(name));
	}

	private void activateEnvironment(ProofEnvironmentModel environmentModel) {
		activeEnvironment = environmentModel;
		environmentNameModel.setSelectedItem(environmentModel == null ? null : environmentModel.getName());

		for (Observer observer : observers)
			observer.environmentActivated(activeEnvironment);
	}

	private void addEnvironment(ProofEnvironment environment) {
		String name = "Beweis " + environmentCounter++;
		ProofEnvironmentModel model = new ProofEnvironmentModel(this, environment, name);

		environments.add(model);
		environmentNameModel.addElement(name);

		environmentModelMap.put(environment, model);
		nameModelMap.put(name, model);

		// notify observers about new model
		for (Observer observer : observers)
			observer.environmentCreated(model);

		// activate the newly created model
		activateEnvironment(model);
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

		/***
		 * @param environmentModel (may be null)
		 */
		void environmentActivated(ProofEnvironmentModel environmentModel);

		void suggestionsLoaded(Transformation[] suggestions);
	}

	@Override
	public void environmentAdded(ProofEnvironment environment) {
		addEnvironment(environment);
	}

	@Override
	public void environmentRemoved(ProofEnvironment environment) {
		ProofEnvironmentModel model = environmentModelMap.get(environment);

		// update list model before maps in case this triggers any events
		environmentNameModel.removeElement(model.getName());

		environmentModelMap.remove(environment);
		nameModelMap.remove(model.getName());

		for (Observer observer : observers)
			observer.environmentDeleted(model);
	}
}
