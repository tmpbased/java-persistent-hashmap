package lib.array;

import java.util.stream.Stream;

public interface Array<V> {
  int length();

  V index(int index);

  Array<V> insert(int index, V value);

  Array<V> update(int index, V value);

  Array<V> delete(int index);

  Stream<V> stream();
  
  static <V> Array<V> append(final Array<V> array, final V value) {
    return array.insert(array.length(), value);
  }
}
