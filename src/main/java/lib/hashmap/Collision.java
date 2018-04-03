package lib.hashmap;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import lib.array.Array;
import lib.hashmap.hash.Hasher;

final class Collision<K, V> implements HashMap<K, V> {
  private final Hasher<K> hasher;
  private final Level level;
  private final Hash hash;
  private final Array<Leaf<K, V>> leafs;

  public Collision(final Hasher<K> hasher, final Level level, final Hash hash, final Array<Leaf<K, V>> leafs) {
    this.hasher = hasher;
    this.level = level;
    this.hash = hash;
    this.leafs = leafs;
  }

  @Override
  public Optional<V> lookup(final K key) {
    if (this.hash.equals(this.hasher.hash(key))) {
      return this.leafs.stream()
                       .filter(it -> it.key.equals(key))
                       .map(it -> it.value).findAny();
    }
    return Optional.empty();
  }

  @Override
  public HashMap<K, V> insert(final K key, final V value) {
    if (this.hash.equals(this.hasher.hash(key))) {
      final Leaf<K, V> leaf = new Leaf<>(this.hasher, this.level, key, value);
      final int len = this.leafs.length();
      for (int i = 0; i < len; i++) {
        if (this.leafs.index(i).equals(value)) {
          return new Collision<>(this.hasher, this.level, this.hash, this.leafs.update(i, leaf));
        }
      }
      return new Collision<>(this.hasher, this.level, this.hash, this.leafs.insert(len, leaf));
    }
    final LevelBits bitmap = this.level.toBits().plus(this.hash.toBits());
    return new BitmapIndexed<K, V>(this.hasher, bitmap, this).insert(key, value);
  }

  private OptionalInt indexOf(final K key) {
    return IntStream.range(0, this.leafs.length())
                    .filter(i -> this.leafs.index(i).key.equals(key))
                    .findAny();
  }

  @Override
  public HashMap<K, V> delete(final K key) {
    if (this.hash.equals(this.hasher.hash(key))) {
      final int i = indexOf(key).orElse(-1);
      if (i >= 0) {
        if (this.leafs.length() == 2) {
          return this.leafs.index(1 - i);
        }
        return new Collision<>(this.hasher, this.level, this.hash,
            this.leafs.delete(i));
      }
    }
    return this;
  }
}
