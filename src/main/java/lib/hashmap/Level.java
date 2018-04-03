package lib.hashmap;

import lib.hashmap.hash.Bits;

interface Level {
  int bitsPerSubkey();

  int subkeyMask();

  Level next();

  int index(Bits bits);

  LevelBits toBits();
}
