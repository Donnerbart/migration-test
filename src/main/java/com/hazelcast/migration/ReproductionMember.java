package com.hazelcast.migration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;

import static com.hazelcast.migration.Utils.getMapServiceContext;
import static com.hazelcast.migration.Utils.logPartitionData;
import static com.hazelcast.migration.Utils.waitClusterSize;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ReproductionMember {

    public static void main(String[] args) throws InterruptedException {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        System.out.println("Instance started...");
        logPartitionData(instance);

        ICountDownLatch latch = instance.getCountDownLatch("latch");
        latch.trySetCount(1);

        if (instance.getCluster().getLocalMember().getAddress().getPort() == 5701) {
            waitClusterSize(instance, 3);
            SECONDS.sleep(5);
            latch.countDown();
        }
        latch.await(Integer.MAX_VALUE, DAYS);

        logPartitionData(instance);
        System.out.println("Updating owned partitions...");
        getMapServiceContext(instance).reloadOwnedPartitions();
        logPartitionData(instance);

        SECONDS.sleep(5);
        Hazelcast.shutdownAll();
    }
}
