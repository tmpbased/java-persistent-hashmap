package lib.hashmap;

import java.util.Optional;

import lib.hashmap.hash.Hasher;

final class Empty<K, V> implements HashMap<K, V> {
  private final Hasher<K> hasher;
  private final Level level;

  public Empty(final Hasher<K> hasher, final Level level) {
    this.hasher = hasher;
    this.level = level;
  }

  @Override
  public Optional<V> lookup(K key) {
    return Optional.empty();
  }

  @Override
  public HashMap<K, V> insert(K key, V value) {
    return new Leaf<>(this.hasher, this.level, key, value);
  }

  @Override
  public HashMap<K, V> delete(K key) {
    return this;
  }
}
