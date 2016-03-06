package rapanui.ui.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import rapanui.core.Transformation;
import rapanui.dsl.BINARY_RELATION;

public class SuggestionListModel extends AbstractListModel<Transformation> {
	private static final long serialVersionUID = 1L;

	private List<Transformation> elements = new ArrayList<Transformation>();
	private Map<String, Transformation> termMap = new HashMap<String, Transformation>();

	public void addSuggestion(Transformation suggestion) {
		String term = suggestion.getOutput().serialize();
		if (!termMap.containsKey(term)) {
			termMap.put(term, suggestion);
			insertElement(suggestion);
		} else if (isPreferable(suggestion, termMap.get(term))) {
			removeElement(termMap.get(term));
			termMap.put(term, suggestion);
			insertElement(suggestion);
		}
	}

	public void clear() {
		int size = elements.size();
		elements.clear();
		termMap.clear();
		fireIntervalRemoved(this, -1, size);
	}

	protected void insertElement(Transformation suggestion) {
		// TODO: maintain ordered list
		elements.add(suggestion);
		fireIntervalAdded(this, elements.size() - 2, elements.size());
	}

	protected void removeElement(Transformation suggestion) {
		int oldIndex = elements.indexOf(suggestion);
		elements.remove(oldIndex);
		fireIntervalRemoved(this, oldIndex - 1, oldIndex + 1);
	}

	protected boolean isPreferable(Transformation newSuggestion, Transformation oldSuggestion) {
		if (oldSuggestion.getFormulaType() == BINARY_RELATION.INCLUSION
				&& newSuggestion.getFormulaType() == BINARY_RELATION.EQUATION)
			return true; // prefer equation over inclusion
		return false; // prefer earlier suggestions over later ones
		// TODO: prefer simpler over more complex
	}

	@Override
	public Transformation getElementAt(int index) {
		return elements.get(index);
	}

	@Override
	public int getSize() {
		return elements.size();
	}

	@Override
	protected void fireIntervalAdded(Object source, int index0, int index1) {
		SwingUtilities.invokeLater(() -> super.fireIntervalAdded(source, index0, index1));
	}

	@Override
	protected void fireIntervalRemoved(Object source, int index0, int index1) {
		SwingUtilities.invokeLater(() -> super.fireIntervalRemoved(source, index0, index1));
	}

	@Override
	protected void fireContentsChanged(Object source, int index0, int index1) {
		SwingUtilities.invokeLater(() -> super.fireContentsChanged(source, index0, index1));
	}
}
