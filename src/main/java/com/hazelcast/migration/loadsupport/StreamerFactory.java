package com.hazelcast.migration.loadsupport;

import com.hazelcast.core.IMap;

/**
 * Creates {@link Streamer} instances for {@link IMap}.
 *
 * If possible an asynchronous variant is created, otherwise it will be synchronous.
 */
public final class StreamerFactory {

    private StreamerFactory() {
    }

    public static <K, V> Streamer<K, V> getInstance(IMap<K, V> map, boolean async) {
        if (async) {
            return new AsyncMapStreamer<K, V>(map);
        }
        return new SyncMapStreamer<K, V>(map);
    }
}
