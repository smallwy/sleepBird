package gameart.config.utils;

public class Pair<K, V> extends javafx.util.Pair<K, V> {
	/**
	 * Creates a new pair
	 *
	 * @param key   The key for this pair
	 * @param value The value to use for this pair
	 */
	public Pair(K key, V value) {
		super(key, value);
	}
}
