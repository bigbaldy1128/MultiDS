package com.qihoo.multids.sharding.algorithm;
/**
 * Created by wangjinzhao on 2017/10/25.
 */

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

class ConsistentHash<T>  {
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Long, T> circle = new TreeMap<>();

    ConsistentHash(Collection<T> nodes) {
        this.hashFunction = Hashing.md5();
        this.numberOfReplicas = 100;

        for (T node : nodes) {
            add(node);
        }
    }

    private void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hashUnencodedChars(node.toString() + i).asLong(), node);
        }
    }

    private void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hashUnencodedChars(node.toString() + i).asLong());
        }
    }

    T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        long hash = hashFunction.hashUnencodedChars(key.toString()).asLong();
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }
}
