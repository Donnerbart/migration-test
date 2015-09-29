package com.hazelcast.migration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;

import java.util.concurrent.TimeUnit;

public class PassiveMemberWithAutoShutdown {

    public static void main(String[] args) throws InterruptedException {
        Hazelcast.newHazelcastInstance();

        TimeUnit.SECONDS.sleep(Constants.PASSIVE_MEMBER_AUTO_SHUTDOWN_SECONDS);

        Hazelcast.shutdownAll();
    }
}
