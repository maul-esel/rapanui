package rapanui.core;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.BiConsumer;

class Patterns {
	public static <T> boolean addToSet(Collection<T> set, T item) {
		if (!set.contains(item)) {
			set.add(item);
			return true;
		}
		return false;
	}

	public static <T> boolean removeWithCheck(Collection<T> collection, T item) {
		if (collection.contains(item)) {
			collection.remove(item);
			return true;
		}
		return false;
	}

	public static <T> boolean removeWithCheck(List<T> collection, int index) {
		if (0 <= index && index < collection.size()) {
			collection.remove(index);
			return true;
		}
		return false;
	}

	public static <T> T[] listToArray(Collection<T> collection, IntFunction<T[]> constructor) {
		return collection.toArray(constructor.apply(collection.size()));
	}

	public static <TObserver, TArgument> void notifyObservers(
			Iterable<TObserver> observers,
			BiConsumer<TObserver, TArgument> listeningMethod,
			TArgument argument) {

		assert observers != null;
		assert listeningMethod != null;

		for (TObserver observer : observers) {
			if (observer != null)
				listeningMethod.accept(observer, argument);
		}
	}

	public static <TItem, TObserver> boolean removeWithCheckAndNotify(
			Collection<TItem> collection,
			TItem item,
			Iterable<TObserver> observers,
			BiConsumer<TObserver, TItem> listeningMethod) {
		boolean removed = removeWithCheck(collection, item);
		if (removed)
			notifyObservers(observers, listeningMethod, item);
		return removed;
	}

	public static <TItem, TObserver> boolean removeWithCheckAndNotify(
			List<TItem> collection,
			int index,
			Iterable<TObserver> observers,
			BiConsumer<TObserver, TItem> listeningMethod) {
		TItem item = (0 <= index && index <= collection.size()) ? collection.get(index) : null;
		boolean removed = removeWithCheck(collection, index);
		if (removed)
			notifyObservers(observers, listeningMethod, item);
		return removed;
	}
}
