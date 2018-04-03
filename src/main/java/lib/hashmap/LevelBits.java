package lib.hashmap;

import lib.hashmap.hash.Bits;

interface LevelBits extends Level {
  int sparseIndex(Bits bits);

  boolean has(Bits bits);

  LevelBits plus(Bits bits);

  LevelBits minus(Bits bits);

  boolean full();

  Level toLevel();
}
