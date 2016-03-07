package rapanui.ui.views;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import rapanui.core.Transformation;

public class SuggestionListCellRenderer implements ListCellRenderer<Transformation> {
	private static final int MAX_VIEW_CACHE_SIZE = 60;
	private static final int INITIAL_VIEW_CACHE_SIZE = 10;

	@SuppressWarnings("serial")
	private final LinkedHashMap<Transformation, SuggestionView> viewCache
		= new LinkedHashMap<Transformation, SuggestionView>(INITIAL_VIEW_CACHE_SIZE, 0.75f, true) {
		@Override protected boolean removeEldestEntry(Map.Entry<Transformation, SuggestionView> eldest) {
			return size() > MAX_VIEW_CACHE_SIZE;
		}
	};

	@Override
	public Component getListCellRendererComponent(JList<? extends Transformation> list, Transformation suggestion, int index,
			boolean cellIsSelected, boolean cellHasFocus) {
		SuggestionView view = viewCache.computeIfAbsent(suggestion, sugg -> new SuggestionView(sugg));
		view.setSelected(cellIsSelected);
		return view;
	}
}
