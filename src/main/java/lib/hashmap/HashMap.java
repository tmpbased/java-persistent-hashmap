package lib.hashmap;

import java.util.Optional;
import java.util.function.Function;

import lib.hashmap.hash.Hasher;

public interface HashMap<K, V> {
  static <K, V> HashMap<K, V> empty(Hasher<K> hasher) {
    return new Empty<>(hasher, new LevelOfInt(0, 4));
  }

  Optional<V> lookup(K key);

  HashMap<K, V> insert(K key, V value);

  HashMap<K, V> delete(K key);

  static <K, V> HashMap<K, V> update(final HashMap<K, V> map, final K key,
      final Function<V, Optional<V>> f) {
    return alter(map, key, value -> value.flatMap(it -> f.apply(it)));
  }

  static <K, V> HashMap<K, V> alter(final HashMap<K, V> map, final K key,
      final Function<Optional<V>, Optional<V>> f) {
    return f.apply(map.lookup(key))
            .map(value -> map.insert(key, value))
            .orElseGet(() -> map.delete(key));
  }
}
