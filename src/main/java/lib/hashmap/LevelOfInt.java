package lib.hashmap;

import lib.hashmap.hash.Bits;

final class LevelOfInt implements Level {
  private final int shift, bitsPerSubkey;

  public LevelOfInt(final int shift, final int bitsPerSubkey) {
    if (shift < 0 || shift + bitsPerSubkey > Long.SIZE) {
      throw new IllegalArgumentException("shift = " + shift);
    }
    if (bitsPerSubkey <= 0 || bitsPerSubkey > 6 /* 1 << 6 = Long.SIZE */) {
      throw new IllegalArgumentException("bitsPerSubkey = " + bitsPerSubkey);
    }
    this.shift = shift;
    this.bitsPerSubkey = bitsPerSubkey;
  }

  @Override
  public int bitsPerSubkey() {
    return this.bitsPerSubkey;
  }

  @Override
  public int subkeyMask() {
    return (1 << this.bitsPerSubkey) - 1;
  }

  @Override
  public Level next() {
    return new LevelOfInt(this.shift + this.bitsPerSubkey, this.bitsPerSubkey);
  }

  @Override
  public int index(Bits bits) {
    return bits.intAtIndex(this.shift) & subkeyMask();
  }

  @Override
  public LevelBits toBits() {
    return new LevelBitsOfLong(this);
  }
}
