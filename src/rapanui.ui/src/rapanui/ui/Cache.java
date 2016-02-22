package rapanui.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<TKey, TValue> {
	private int maxCapacity;

	private final Map<TKey, TValue> map = new HashMap<TKey, TValue>();
	private final Map<TKey, Long> usageTime = new HashMap<TKey, Long>();

	public Cache(int maxCapacity) {
		assert maxCapacity > 1;
		this.maxCapacity = maxCapacity;
	}

	public synchronized void put(TKey key, TValue value) {
		if (map.size() >= maxCapacity) {
			TKey leastRecentlyUsed = usageTime.entrySet().stream().min(
					(e1, e2) -> e1.getValue() == e2.getValue() ? 0 : (e1.getValue() < e2.getValue() ? -1 : 1)
				).get().getKey();
			map.remove(leastRecentlyUsed);
			usageTime.remove(leastRecentlyUsed);
		}

		map.put(key, value);
		usageTime.put(key,  System.currentTimeMillis());
	}

	public synchronized TValue get(TKey key) {
		if (!map.containsKey(key))
			throw new IllegalArgumentException("Unknown key");

		usageTime.put(key, System.currentTimeMillis());
		return map.get(key);
	}

	public synchronized TValue get(TKey key, Function<TKey, TValue> supplier) {
		if (map.containsKey(key))
			return get(key);

		TValue value = supplier.apply(key);
		put(key, value);
		return value;
	}

	public synchronized void delete(TKey key) {
		map.remove(key);
		usageTime.remove(key);
	}

	public synchronized boolean hasKey(TKey key) {
		return map.containsKey(key);
	}
}
