package rapanui.core;

import java.util.Collection;
import java.util.List;

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
}
