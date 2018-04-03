package lib.hashmap.hash;

import lib.hashmap.Hash;

final class JavaHashCode implements Hash {
  private final int hashCode;

  public JavaHashCode(final int hashCode) {
    this.hashCode = hashCode;
  }

  @Override
  public Bits toBits() {
    return new BitsOfLong(this.hashCode);
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof JavaHashCode) {
      final JavaHashCode o = (JavaHashCode) obj;
      return this.hashCode == o.hashCode;
    }
    return false;
  }
}
