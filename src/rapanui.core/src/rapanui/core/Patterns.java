package rapanui.core;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.BiConsumer;

/**
 * A static helper class for certain common behaviours.
 */
class Patterns {
	/**
	 * Adds an item to a collection, if it isn't contained already.
	 *
	 * @param set The collection to add the item to. May not be null.
	 * @param item The item to add to the collection. Depending on the collection type, may or may not be null.
	 *
	 * @return True if the item was added, false if it already existed in the collection.
	 */
	public static <T> boolean addToSet(Collection<T> set, T item) {
		if (!set.contains(item)) {
			set.add(item);
			return true;
		}
		return false;
	}

	/**
	 * Checks if a collection contains an item, and removes it if so.
	 *
	 * @param collection The collection to remove the item from. May not be null.
	 * @param item The item to remove. May be null.
	 *
	 * @return True if the item was found and removed, false otherwise.
	 */
	public static <T> boolean removeWithCheck(Collection<T> collection, T item) {
		if (collection.contains(item)) {
			collection.remove(item);
			return true;
		}
		return false;
	}

	/**
	 * Checks if a collection has a certain index, and removes the corresponding item if so.
	 *
	 * @param collection The collection to remove the item from. May not be null.
	 * @param index The index of the item to remove.
	 *
	 * @return True if the index existed and was removed, false otherwise.
	 */
	public static <T> boolean removeWithCheck(List<T> collection, int index) {
		if (0 <= index && index < collection.size()) {
			collection.remove(index);
			return true;
		}
		return false;
	}

	/**
	 * Creates an array with the items of a collection.
	 *
	 * @param collection The collection to conbvert. Must not be null.
	 * @param constructor A function creating an array of the desired type and the supplied size. Must not be null.
	 *
	 * @return The newly created array.
	 */
	public static <T> T[] listToArray(Collection<T> collection, IntFunction<T[]> constructor) {
		return collection.toArray(constructor.apply(collection.size()));
	}

	/**
	 * Notifies a list of observers by calliung a specified method.
	 *
	 * @param observers The list of observers to be called. Must not be null.
	 * @param listeningMethod The method to call on each observer. Must not be null.
	 * @param argument An argument to pass to each observer. May be null.
	 */
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

	/**
	 * Removes an item from a collection, if present, and notifies observers about it.
	 * If the item is not found, observers are not notified.
	 *
	 * @param collection The collection to remove the item from. Must not be null.
	 * @param item The item to remove. May be null.
	 * @param observers The list of observers to notify. Must not be null.
	 * @param listeningMethod The method to call on each observer. Must not be null.
	 *
	 * @return True if the item existed and was removed, false otherwise.
	 */
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

	/**
	 * Removes an index from a collection, if present, and notified observers about it.
	 * If the index does not exist, observers are not notified.
	 *
	 * @param collection The collection to remove the index from. Must not be null.
	 * @param index The index to remove.
	 * @param observers The list of observers to notify. Must not be null.
	 * @param listeningMethod The method to call on each observer. Must not be null.
	 *
	 * @return True if the index existed and was removed, false otherwise.
	 */
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
