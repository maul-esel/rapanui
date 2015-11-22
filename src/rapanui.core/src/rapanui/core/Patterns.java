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

	public static <TListener, TArgument> void notifyObservers(
			Iterable<TListener> observers,
			BiConsumer<TListener, TArgument> listeningMethod,
			TArgument argument) {

		assert observers != null;
		assert listeningMethod != null;

		for (TListener observer : observers) {
			if (observer != null)
				listeningMethod.accept(observer, argument);
		}
	}
}
