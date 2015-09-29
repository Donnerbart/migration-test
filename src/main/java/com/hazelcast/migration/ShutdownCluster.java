package com.hazelcast.migration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;

public class ShutdownCluster {

    public static void main(String[] args) throws InterruptedException {
        // init cluster
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();

        ICountDownLatch stopSignal = instance.getCountDownLatch("stopSignal");
        stopSignal.countDown();

        // leave cluster
        instance.shutdown();
    }
}
