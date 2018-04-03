package lib.hashmap.hash;

import lib.hashmap.Hash;

public interface Hasher<T> {
	Hash hash(T value);
}
