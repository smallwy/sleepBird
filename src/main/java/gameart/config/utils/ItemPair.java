package gameart.config.utils;

import javafx.util.Pair;

public class ItemPair<K, V> extends Pair<K, V> {
	/**
	 * Creates a new pair
	 * @param key   The key for this pair
	 * @param value The value to use for this pair
	 */
	public ItemPair(K key, V value) {
		super(key, value);
	}
}
