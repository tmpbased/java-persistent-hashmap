package lib.hashmap.hash;

import lib.hashmap.Hash;

public final class JavaHasher<T> implements Hasher<T> {
  @Override
  public Hash hash(final T value) {
    return new JavaHashCode(value.hashCode());
  }
}
