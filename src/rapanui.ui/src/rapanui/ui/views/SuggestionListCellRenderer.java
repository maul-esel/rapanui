package rapanui.ui.views;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import rapanui.core.Transformation;

import rapanui.ui.Cache;

public class SuggestionListCellRenderer implements ListCellRenderer<Transformation> {
	private static final int MAX_VIEW_CACHE_SIZE = 40;
	private final Cache<Transformation, SuggestionView> viewCache = new Cache<Transformation, SuggestionView>(MAX_VIEW_CACHE_SIZE);

	@Override
	public Component getListCellRendererComponent(JList<? extends Transformation> list, Transformation suggestion, int index,
			boolean cellIsSelected, boolean cellHasFocus) {
		SuggestionView view = viewCache.get(suggestion, sugg -> new SuggestionView(sugg));
		view.setSelected(cellIsSelected);
		return view;
	}
}
