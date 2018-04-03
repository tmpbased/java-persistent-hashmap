package lib.hashmap;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import lib.array.ImmutableArray;
import lib.hashmap.hash.Bits;
import lib.hashmap.hash.Hasher;

final class Leaf<K, V> implements HashMap<K, V> {
  private final Hasher<K> hasher;
  private final Level level;
  private final Hash hash;
  final K key;
  final V value;

  Leaf(final Hasher<K> hasher, final Level level, final K key, final V value) {
    this.hasher = hasher;
    this.level = level;
    this.hash = hasher.hash(key);
    this.key = key;
    this.value = value;
  }

  @Override
  public Optional<V> lookup(final K key) {
    if (this.hash.equals(this.hasher.hash(key)) && this.key.equals(key)) {
      return Optional.of(this.value);
    }
    return Optional.empty();
  }

  @Override
  public HashMap<K, V> insert(final K key, final V value) {
    final Hash hash = this.hasher.hash(key);
    if (this.hash.equals(hash)) {
      if (this.key.equals(key)) {
        if (this.value.equals(value)) {
          return this;
        }
        return new Leaf<>(this.hasher, this.level, key, value);
      }
      return new Collision<>(this.hasher, this.level, hash,
          ImmutableArray.of(this, new Leaf<>(this.hasher, this.level, key, value)));
    }
    return pair(key, value);
  }

  private HashMap<K, V> pair(final Level level, final int i1,
      final Leaf<K, V> leaf1, final int i2, final Leaf<K, V> leaf2) {
    LevelBits bitmap = level.toBits();
    bitmap = bitmap.plus(leaf1.hash.toBits());
    bitmap = bitmap.plus(leaf2.hash.toBits());
    if (i1 < i2) {
      return new BitmapIndexed<K, V>(this.hasher, bitmap, leaf1, leaf2);
    }
    return new BitmapIndexed<K, V>(this.hasher, bitmap, leaf2, leaf1);
  }

  private HashMap<K, V> pair(final K key, final V value) {
    final Deque<Level> levelStack = new LinkedList<>();
    Level level = this.level;
    int i1, i2;
    final Bits bits = this.hash.toBits();
    do {
      i1 = level.index(bits);
      i2 = level.index(this.hasher.hash(key).toBits());
      if (i1 == i2) {
        levelStack.addLast(level);
        level = level.next();
      }
    } while (i1 == i2);
    HashMap<K, V> map = pair(level,
        //
        i1, new Leaf<>(this.hasher, level, this.key, this.value),
        //
        i2, new Leaf<>(this.hasher, level, key, value));
    while ((level = levelStack.pollLast()) != null) {
      map = new BitmapIndexed<K, V>(this.hasher, level.toBits().plus(bits), map);
    }
    return map;
  }

  @Override
  public HashMap<K, V> delete(final K key) {
    if (this.hash.equals(this.hasher.hash(key)) && this.key.equals(key)) {
      return new Empty<>(this.hasher, this.level);
    }
    return this;
  }
}
