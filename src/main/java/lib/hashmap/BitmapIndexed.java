package lib.hashmap;

import java.util.Optional;

import lib.array.Array;
import lib.array.ImmutableArray;
import lib.hashmap.hash.Bits;
import lib.hashmap.hash.Hasher;

final class BitmapIndexed<K, V> implements HashMap<K, V> {
  private final Hasher<K> hasher;
  private final LevelBits bits;
  private final Array<HashMap<K, V>> leafs;

  @SafeVarargs
  BitmapIndexed(final Hasher<K> hasher, final LevelBits bitmap,
      final HashMap<K, V>... leafs) {
    this(hasher, bitmap, ImmutableArray.of(leafs));
  }

  private BitmapIndexed(final Hasher<K> hasher, final LevelBits bitmap,
      final Array<HashMap<K, V>> leafs) {
    this.hasher = hasher;
    this.bits = bitmap;
    this.leafs = leafs;
  }

  @Override
  public Optional<V> lookup(K key) {
    final Bits bits = this.hasher.hash(key).toBits();
    if (this.bits.full()) {
      return this.leafs.index(this.bits.index(bits)).lookup(key);
    }
    if (this.bits.has(bits)) {
      final int i = this.bits.sparseIndex(bits);
      return this.leafs.index(i).lookup(key);
    }
    return Optional.empty();
  }

  @Override
  public HashMap<K, V> insert(K key, V value) {
    final Bits bits = this.hasher.hash(key).toBits();
    if (this.bits.full()) {
      final int i = this.bits.index(bits);
      final HashMap<K, V> newLeaf = this.leafs.index(i).insert(key, value);
      if (newLeaf != this.leafs.index(i)) {
        return new BitmapIndexed<>(this.hasher, this.bits,
            this.leafs.update(i, newLeaf));
      }
    }
    final int i = this.bits.sparseIndex(bits);
    if (this.bits.has(bits)) {
      final HashMap<K, V> newLeaf = this.leafs.index(i).insert(key, value);
      if (newLeaf != this.leafs.index(i)) {
        return new BitmapIndexed<>(this.hasher, this.bits,
            this.leafs.update(i, newLeaf));
      }
    } else {
      final LevelBits newBitmap = this.bits.plus(bits);
      final Array<HashMap<K, V>> newLeafs = this.leafs.insert(i, new Leaf<>(this.hasher, this.bits.toLevel(), key, value));
      return new BitmapIndexed<>(this.hasher, newBitmap, newLeafs);
    }
    return this;
  }

  private boolean isLeafOrCollision(HashMap<K, V> map) {
    return map instanceof Leaf || map instanceof Collision;
  }

  @Override
  public HashMap<K, V> delete(K key) {
    final Bits bits = this.hasher.hash(key).toBits();
    if (this.bits.full()) {
      final int i = this.bits.index(bits);
      final HashMap<K, V> newLeaf = this.leafs.index(i).delete(key);
      if (newLeaf != this.leafs.index(i)) {
        if (newLeaf instanceof Empty) {
          return new BitmapIndexed<>(this.hasher, this.bits.minus(bits), this.leafs.delete(i));
        }
        return new BitmapIndexed<>(this.hasher, this.bits, this.leafs.update(i, newLeaf));
      }
    }
    if (this.bits.has(bits)) {
      final int i = this.bits.sparseIndex(bits);
      final HashMap<K, V> newLeaf = this.leafs.index(i).delete(key);
      if (newLeaf != this.leafs.index(i)) {
        if (newLeaf instanceof Empty) {
          switch (this.leafs.length()) {
            case 1:
              return new Empty<>(this.hasher, this.bits.toLevel());
            case 2:
              if (isLeafOrCollision(this.leafs.index(1 - i))) {
                return this.leafs.index(1 - i);
              }
              return new BitmapIndexed<>(this.hasher, this.bits.minus(bits), this.leafs.delete(i));
            default:
              if (isLeafOrCollision(this.leafs.index(i)) && this.leafs.length() == 1) {
                return this.leafs.index(i);
              }
              return new BitmapIndexed<>(this.hasher, this.bits, this.leafs.update(i, newLeaf));
          }
        }
      }
    }
    return this;
  }
}
