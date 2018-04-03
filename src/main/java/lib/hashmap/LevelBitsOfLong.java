package lib.hashmap;

import lib.hashmap.hash.Bits;

final class LevelBitsOfLong implements LevelBits {
  private final long bits;
  private final Level level;

  public LevelBitsOfLong(final Level level) {
    this(0L, level);
  }

  private LevelBitsOfLong(final long bits, final Level level) {
    this.bits = bits;
    this.level = level;
  }

  @Override
  public int bitsPerSubkey() {
    return this.level.bitsPerSubkey();
  }

  @Override
  public int subkeyMask() {
    return this.level.subkeyMask();
  }

  @Override
  public Level next() {
    return new LevelBitsOfLong(this.level.next());
  }

  @Override
  public int index(final Bits bits) {
    return this.level.index(bits);
  }

  private long mask(final Bits bits) {
    return 1 << index(bits);
  }

  @Override
  public boolean has(final Bits bits) {
    return (this.bits & mask(bits)) != 0;
  }

  @Override
  public int sparseIndex(final Bits bits) {
    return Long.bitCount(this.bits & (mask(bits) - 1));
  }

  @Override
  public LevelBits plus(final Bits bits) {
    return new LevelBitsOfLong(this.bits | mask(bits), this.level);
  }

  @Override
  public LevelBits minus(Bits bits) {
    return new LevelBitsOfLong(this.bits & ~mask(bits), this.level);
  }

  @Override
  public boolean full() {
    final int subkeyMask = subkeyMask();
    return (this.bits & subkeyMask) == subkeyMask;
  }

  @Override
  public LevelBits toBits() {
    return this;
  }

  @Override
  public Level toLevel() {
    return this.level;
  }
}
