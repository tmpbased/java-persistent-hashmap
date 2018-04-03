package lib.hashmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import lib.hashmap.Collision;
import lib.hashmap.Hash;
import lib.hashmap.HashMap;
import lib.hashmap.Leaf;
import lib.hashmap.hash.Bits;
import lib.hashmap.hash.BitsOfLong;
import lib.hashmap.hash.Hasher;

public class HashMapTest {
  private HashMap<Key, Object> obj;

  private interface Key {
    Hash hash();
  }

  private final class HashOfLong implements Hash {
    private final long hash;

    public HashOfLong(final long hash) {
      this.hash = hash;
    }

    @Override
    public Bits toBits() {
      return new BitsOfLong(this.hash);
    }

    @Override
    public int hashCode() {
      return Long.hashCode(this.hash);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof HashOfLong) {
        final HashOfLong o = (HashOfLong) obj;
        return this.hash == o.hash;
      }
      return false;
    }
  }

  private final class KeyOfLong implements Key {
    private final long key;

    public KeyOfLong(final long key) {
      this.key = key;
    }

    @Override
    public Hash hash() {
      return new HashOfLong(this.key);
    }

    @Override
    public int hashCode() {
      return Long.hashCode(this.key);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof KeyOfLong) {
        final KeyOfLong o = (KeyOfLong) obj;
        return this.key == o.key;
      }
      return false;
    }
  }

  final class CollisionKey implements Key {
    private final int key;

    public CollisionKey(final int key) {
      this.key = key;
    }

    @Override
    public Hash hash() {
      return new HashOfLong(this.key % 10);
    }

    @Override
    public int hashCode() {
      return this.key;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof CollisionKey) {
        final CollisionKey o = (CollisionKey) obj;
        return this.key == o.key;
      }
      return false;
    }
  }

  private final class KeyHasher implements Hasher<Key> {
    @Override
    public Hash hash(final Key key) {
      return key.hash();
    }
  }

  @Before
  public void setUp() {
    this.obj = HashMap.empty(new KeyHasher());
  }

  @Test
  public void emptyRemove() {
    assertSame(this.obj, this.obj.delete(new KeyOfLong(1)));
  }

  @Test
  public void insert() {
    HashMap<Key, Object> newObj = this.obj.insert(new KeyOfLong(1), "1");
    assertEquals(Optional.of("1"), newObj.lookup(new KeyOfLong(1)));
  }

  @Test
  public void delete() {
    HashMap<Key, Object> newObj = this.obj.insert(new KeyOfLong(1), "1");
    newObj = newObj.delete(new KeyOfLong(1));
    assertEquals(Optional.empty(), newObj.lookup(new KeyOfLong(1)));
  }

  @Test
  public void insertDeep() {
    final Key key1 = new KeyOfLong(0x1122334455667788L);
    HashMap<Key, Object> newObj = this.obj.insert(key1, "1");
    newObj = newObj.insert(new KeyOfLong(0x0122334455667788L), "2");
    assertEquals(Optional.of("1"), newObj.lookup(key1));
  }

  @Test
  public void insertCollision() {
    HashMap<Key, Object> newObj = this.obj.insert(new CollisionKey(1), "1");
    newObj = newObj.insert(new CollisionKey(11), "2");

    assertEquals(Collision.class, newObj.getClass());
    assertEquals(Optional.of("1"), newObj.lookup(new CollisionKey(1)));

    newObj = newObj.delete(new CollisionKey(11));

    assertEquals(Leaf.class, newObj.getClass());
    assertEquals(Optional.of("1"), newObj.lookup(new CollisionKey(1)));
  }
}
