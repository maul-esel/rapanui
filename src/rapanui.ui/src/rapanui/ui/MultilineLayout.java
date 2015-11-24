package rapanui.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SwingConstants;

public class MultilineLayout implements LayoutManager2 {
	private final int orientation;
	private int currentLine = 0;
	private Map<Component, Integer> componentPosition = new HashMap<Component, Integer>();

	public MultilineLayout(int orientation) {
		this.orientation = orientation;
	}

	public MultilineLayout() {
		this(SwingConstants.VERTICAL);
	}

	public void newLine() {
		currentLine++;
	}

	@Override
	public void addLayoutComponent(Component component, Object constraint) {
		if (constraint != null && constraint instanceof Integer)
			currentLine = (Integer)constraint;

		componentPosition.put(component, currentLine);
	}

	@Override
	public void removeLayoutComponent(Component component) {
		componentPosition.remove(component);
	}

	@Override
	public void layoutContainer(Container container) {
		// group components by line
		Map<Integer, ArrayList<Component>> lines = new HashMap<Integer, ArrayList<Component>>();
		int line = 0, maxLine = 0;
		for (Component child : container.getComponents()) {
			line = componentPosition.containsKey(child) ? componentPosition.get(child) : line;
			if (!lines.containsKey(line))
				lines.put(line, new ArrayList<Component>());
			lines.get(line).add(child);
			if (line > maxLine)
				maxLine = line;
		}

		ToIntFunction<Dimension> x1; // primary axis
		ToIntFunction<Dimension> x2; // secondary axis
		int containerSize;

		if (orientation == SwingConstants.VERTICAL) {
			x1 = (d) -> d.width;
			x2 = (d) -> d.height;
			containerSize = container.getWidth();
		} else {
			x1 = (d) -> d.height;
			x2 = (d) -> d.width;
			containerSize = container.getHeight();
		}

		int lineOffset = 0;
		for (line = 0; line <= maxLine; ++line) {
			if (!lines.containsKey(line))
				continue;

			ArrayList<Component> lineComponents = lines.get(line);

			int lineHeight = lineComponents.stream()
					.filter(c -> c.isVisible())
					.map(Component::getPreferredSize)
					.mapToInt(x2).max().orElse(0);
			int preferredWidth = lineComponents.stream().map(Component::getPreferredSize).mapToInt(x1).sum();

			Map<Component, Integer> assignedWidth = lineComponents.stream()
					.collect(Collectors.toMap(Function.identity(), c -> x1.applyAsInt(c.getPreferredSize())));

			int widthDifference = containerSize - preferredWidth;
			if (widthDifference > 0) {
				int maxWidthTolerance = lineComponents.stream()
						.mapToInt(c -> x1.applyAsInt(c.getMaximumSize()) - x1.applyAsInt(c.getPreferredSize()))
						.sum();
				Stream<Component> components = lineComponents.stream()
						.filter(c -> x1.applyAsInt(c.getMaximumSize()) > x1.applyAsInt(c.getPreferredSize()));
				for (Component component : components.toArray(Component[]::new)) {
					int delta = (int)((x1.applyAsInt(component.getMaximumSize()) - x1.applyAsInt(component.getPreferredSize()))
							* Double.min(1, widthDifference / (double)maxWidthTolerance));
					assignedWidth.put(component, x1.applyAsInt(component.getPreferredSize()) + delta);
				}

				if (lineComponents.size() == 1)
					assignedWidth.put(lineComponents.get(0), containerSize);
				// else if any more width remains, it will be filled with blanks
			} else {
				int minWidthTolerance = lineComponents.stream()
						.mapToInt(c -> x1.applyAsInt(c.getMinimumSize()) - x1.applyAsInt(c.getPreferredSize()))
						.sum();
				Stream<Component> components = lineComponents.stream()
						.filter(c -> x1.applyAsInt(c.getMinimumSize()) < x1.applyAsInt(c.getPreferredSize()));
				for (Component component : components.toArray(Component[]::new)) {
					int delta = (int)((x1.applyAsInt(component.getMinimumSize()) - x1.applyAsInt(component.getPreferredSize()))
							* Double.min(1, widthDifference / (double)minWidthTolerance));
					assignedWidth.put(component, component.getPreferredSize().width + delta);
				}

				int widthSum = assignedWidth.values().stream().mapToInt(Integer::intValue).sum();
				widthDifference = containerSize - widthSum;
				if (widthDifference != 0) {
					for (Component component : lineComponents) {
						int delta = (int)(widthDifference*assignedWidth.get(component)/(double)widthSum);
						assignedWidth.put(component, assignedWidth.get(component) + delta);
					}
				}
			}

			widthDifference = containerSize - assignedWidth.values().stream().mapToInt(Integer::intValue).sum();

			double columnOffset = 0;
			int gapCount = container.getComponentCount() - 1;
			for (Component child : lineComponents) {
				if (orientation == SwingConstants.VERTICAL) {
					int y = (int)(lineOffset + child.getAlignmentY() * (lineHeight - child.getPreferredSize().height));
					child.setBounds((int)columnOffset, y, assignedWidth.get(child), child.getPreferredSize().height);
				} else {
					int x = (int)(lineOffset + child.getAlignmentX() * (lineHeight - child.getPreferredSize().width));
					child.setBounds(x, (int)columnOffset, child.getPreferredSize().width, assignedWidth.get(child));
				}

				columnOffset += assignedWidth.get(child);
				if (widthDifference > 0)
					columnOffset += widthDifference / (double)gapCount;
			}

			lineOffset += lineHeight;
		}
	}

	@Override
	public Dimension maximumLayoutSize(Container container) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); // can be arbitrarily large
	}

	@Override
	public Dimension minimumLayoutSize(Container container) {
		return calculateLayoutSize(container, Component::getMinimumSize);
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		return calculateLayoutSize(container, Component::getPreferredSize);
	}

	protected Dimension calculateLayoutSize(Container container, Function<Component, Dimension> size) {
		Map<Integer, Integer> lineWidth = new HashMap<Integer, Integer>();
		Map<Integer, Integer> lineHeight = new HashMap<Integer, Integer>();

		int line = 0;
		for (Component child : container.getComponents()) {
			line = componentPosition.containsKey(child) ? componentPosition.get(child): line;

			Dimension childSize = size.apply(child);
			int currentLineWidth = childSize.width, currentLineHeight = childSize.height;

			if (lineWidth.containsKey(line)) {
				currentLineWidth += lineWidth.get(line);
				currentLineHeight = Integer.max(currentLineHeight, lineHeight.get(line));
			}

			lineWidth.put(line, currentLineWidth);
			lineHeight.put(line, currentLineHeight);
		}

		if (orientation == SwingConstants.VERTICAL)
			return new Dimension(
				lineWidth.values().stream().mapToInt(Integer::intValue).max().orElse(0),
				lineHeight.values().stream().mapToInt(Integer::intValue).sum());
		else
			return new Dimension(
					lineWidth.values().stream().mapToInt(Integer::intValue).sum(),
					lineHeight.values().stream().mapToInt(Integer::intValue).max().orElse(0));
	}

	@Override
	public float getLayoutAlignmentX(Container container) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container container) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(Container container) {
	}

	@Override
	public void addLayoutComponent(String constraint, Component component) {
	}
}
