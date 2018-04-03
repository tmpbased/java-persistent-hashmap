package lib.array;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;

public final class ImmutableArray<V> implements Array<V> {
  private final V[] array;

  public ImmutableArray(final IntFunction<V[]> generator) {
    this(generator.apply(0));
  }

  private ImmutableArray(final V[] array) {
    this.array = array;
  }

  public static <V> Array<V> singleton(final IntFunction<V[]> generator, final V value) {
    final V[] array = generator.apply(1);
    array[0] = value;
    return new ImmutableArray<>(array);
  }

  @SafeVarargs
  public static <V> Array<V> of(final V... values) {
    final V[] array = Arrays.copyOf(values, values.length);
    for (int i = 0; i < values.length; i++) {
      array[i] = values[i];
    }
    return new ImmutableArray<>(array);
  }

  @Override
  public int length() {
    return this.array.length;
  }

  @Override
  public V index(final int index) {
    return this.array[index];
  }

  @Override
  public Array<V> insert(final int index, final V value) {
    final V[] newArray = Arrays.copyOf(this.array, this.array.length + 1);
    System.arraycopy(newArray, index, newArray, index + 1, newArray.length - index - 1);
    newArray[index] = value;
    return new ImmutableArray<>(newArray);
  }

  @Override
  public Array<V> update(int index, V value) {
    final V[] newArray = this.array.clone();
    newArray[index] = value;
    return new ImmutableArray<>(newArray);
  }

  @Override
  public Array<V> delete(int index) {
    final V[] newArray = Arrays.copyOf(this.array, this.array.length - 1);
    System.arraycopy(this.array, index + 1, newArray, index, this.array.length - index - 1);
    return new ImmutableArray<>(newArray);
  }

  @Override
  public Stream<V> stream() {
    return Arrays.stream(this.array);
  }

  public static <E> Collector<E, ?, Array<E>> collect(final IntFunction<E[]> generator) {
    return Collector.of(() -> Stream.<E>builder(),
        //
        (out, in) -> out.add(in),
        //
        (a, b) -> {
          b.build().forEach(a::add);
          return a;
        },
        //
        it -> new ImmutableArray<>(it.build().toArray(generator)));
  }
}
