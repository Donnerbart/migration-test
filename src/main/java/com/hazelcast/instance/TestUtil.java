package com.hazelcast.instance;

import com.hazelcast.core.HazelcastInstance;

public final class TestUtil {

    private TestUtil() {
    }

    public static HazelcastInstanceImpl getHazelcastInstanceImpl(HazelcastInstance hz) {
        HazelcastInstanceImpl impl = null;
        if (hz instanceof HazelcastInstanceProxy) {
            impl = ((HazelcastInstanceProxy) hz).original;
        } else if (hz instanceof HazelcastInstanceImpl) {
            impl = (HazelcastInstanceImpl) hz;
        }
        return impl;
    }

    public static Node getNode(HazelcastInstance hz) {
        HazelcastInstanceImpl impl = getHazelcastInstanceImpl(hz);
        if (impl == null) {
            throw new NullPointerException("HazelcastInstanceImpl is null");
        }
        return impl.node;
    }
}
