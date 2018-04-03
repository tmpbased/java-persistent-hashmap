package lib.hashmap.hash;

public final class BitsOfLong implements Bits {
  private final long bits;

  public BitsOfLong(final long bits) {
    this.bits = bits;
  }

  @Override
  public int intAtIndex(final int shift) {
    if (shift < 0 || shift >= Long.SIZE) {
      throw new IllegalArgumentException();
    }
    return (int) (this.bits >>> shift);
  }
}
