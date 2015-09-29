package com.hazelcast.migration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import static com.hazelcast.migration.Constants.PASSIVE_MEMBER_AUTO_SHUTDOWN_SECONDS;
import static com.hazelcast.migration.Utils.getLocalPartitionsCount;
import static java.util.concurrent.TimeUnit.SECONDS;

public class PassiveMemberWithAutoShutdown {

    public static void main(String[] args) throws InterruptedException {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        System.out.println("Partition count: " + getLocalPartitionsCount(instance));

        SECONDS.sleep(PASSIVE_MEMBER_AUTO_SHUTDOWN_SECONDS);

        System.out.println("Partition count: " + getLocalPartitionsCount(instance));

        Hazelcast.shutdownAll();
    }
}
