package com.hazelcast.migration;

import com.hazelcast.core.Hazelcast;

import static com.hazelcast.migration.Constants.PASSIVE_MEMBER_AUTO_SHUTDOWN_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class PassiveMemberWithAutoShutdown {

    public static void main(String[] args) throws InterruptedException {
        Hazelcast.newHazelcastInstance();

        SECONDS.sleep(PASSIVE_MEMBER_AUTO_SHUTDOWN_SECONDS);

        Hazelcast.shutdownAll();
    }
}
