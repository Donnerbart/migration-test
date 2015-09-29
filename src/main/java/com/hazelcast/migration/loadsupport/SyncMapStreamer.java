package com.hazelcast.migration.loadsupport;

import com.hazelcast.core.IMap;

/**
 * Synchronous implementation of {@link Streamer} for {@link IMap}.
 *
 * @param <K> key type
 * @param <V> value type
 */
final class SyncMapStreamer<K, V> implements Streamer<K, V> {

    private final IMap<K, V> map;

    SyncMapStreamer(IMap<K, V> map) {
        this.map = map;
    }

    @Override
    public void pushEntry(K key, V value) {
        map.set(key, value);
    }

    @Override
    public void await() {
    }
}
