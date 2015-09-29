package com.hazelcast.migration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;

import static com.hazelcast.migration.Utils.getLocalPartitionsCount;
import static java.util.concurrent.TimeUnit.DAYS;

public class PassiveMember {

    public static void main(String[] args) throws InterruptedException {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        System.out.println("Partition count: " + getLocalPartitionsCount(instance));

        ICountDownLatch latch = instance.getCountDownLatch("stopSignal");
        latch.await(Integer.MAX_VALUE, DAYS);

        Hazelcast.shutdownAll();
    }
}
