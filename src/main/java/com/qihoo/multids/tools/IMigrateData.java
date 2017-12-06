package com.qihoo.multids.tools;

import java.util.List;

public interface IMigrateData<T, K> {
    List<K> getShardingKeys();

    List<T> query(K key);

    void delete(K key);

    void insert(List<T> data, K key);
}
