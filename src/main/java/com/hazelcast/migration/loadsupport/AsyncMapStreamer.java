package com.hazelcast.migration.loadsupport;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;

/**
 * Asynchronous implementation of {@link Streamer} for {@link IMap}.
 *
 * @param <K> key type
 * @param <V> value type
 */
final class AsyncMapStreamer<K, V> extends AbstractAsyncStreamer<K, V> {

    private final IMap<K, V> map;

    AsyncMapStreamer(IMap<K, V> map) {
        this.map = map;
    }

    @Override
    ICompletableFuture storeAsync(K key, V value) {
        return (ICompletableFuture) map.putAsync(key, value);
    }
}
