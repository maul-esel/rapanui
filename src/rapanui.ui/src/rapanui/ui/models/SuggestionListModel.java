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
		SwingUtilities.invokeLater(() -> addSuggestionInternal(suggestion));
	}

	protected synchronized void addSuggestionInternal(Transformation suggestion) {
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
		SwingUtilities.invokeLater(this::clearInternal);
	}

	protected synchronized void clearInternal() {
		int size = elements.size();
		if (size > 0) {
			elements.clear();
			termMap.clear();
			fireIntervalRemoved(this, 0, size - 1);
		}
	}

	// sort terms first by length, then lexicographically
	protected void insertElement(Transformation suggestion) {
		String suggestedTerm = suggestion.getOutput().serialize();

		// binary search for position
		int low = 0, high = elements.size();
		while (low < high) {
			int mid = (low + high) / 2;
			String midTerm = elements.get(mid).getOutput().serialize();
			if (suggestedTerm.length() > midTerm.length()
					|| (suggestedTerm.length() == midTerm.length() && suggestedTerm.compareTo(midTerm) > 0))
				low = mid + 1;
			else
				high = mid;
		}

		elements.add(low, suggestion);
		fireIntervalAdded(this, low, low);
	}

	protected void removeElement(Transformation suggestion) {
		int oldIndex = elements.indexOf(suggestion);
		if (oldIndex >= 0) {
			elements.remove(oldIndex);
			fireIntervalRemoved(this, oldIndex, oldIndex);
		}
	}

	protected boolean isPreferable(Transformation newSuggestion, Transformation oldSuggestion) {
		if (oldSuggestion.getFormulaType() == BINARY_RELATION.INCLUSION
				&& newSuggestion.getFormulaType() == BINARY_RELATION.EQUATION)
			return true; // prefer equation over inclusion
		return false; // prefer earlier suggestions over later ones
		// TODO: prefer simpler over more complex
	}

	@Override
	public synchronized Transformation getElementAt(int index) {
		return elements.get(index);
	}

	@Override
	public synchronized int getSize() {
		return elements.size();
	}
}
