package com.hazelcast.migration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import com.hazelcast.migration.domain.DomainObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class ActiveMember {

    public static void main(String[] args) throws InterruptedException {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        ICountDownLatch startQueries = instance.getCountDownLatch("startQueries");
        startQueries.trySetCount(1);

        // wait for signal
        System.out.println("Wait for query start...");
        startQueries.await(Integer.MAX_VALUE, TimeUnit.DAYS);
        System.out.println("Done!");

        // prepare data
        System.out.println("Prepare data...");
        Random random = new Random();
        ISet<String> set = instance.getSet("set");
        String[] uniqueStrings = set.toArray(new String[set.size()]);
        System.out.println("Done!");

        // query map
        System.out.println("Starting queries...");
        IMap<String, DomainObject> map = instance.getMap("map");
        for (int i = 1; i <= Constants.QUERY_COUNT; i++) {
            String string = uniqueStrings[random.nextInt(uniqueStrings.length)];
            Predicate predicate = Predicates.equal("stringVal", string);

            long started = System.nanoTime();
            Collection<DomainObject> results = map.values(predicate);
            long diff = System.nanoTime() - started;
            System.out.println(format("#%5d Query took %5d ms (%d results)", i, TimeUnit.NANOSECONDS.toMillis(diff), results.size()));
        }
        System.out.println("Done!");

        // wait for shutdown
        System.out.println("Wait for shutdown...");
        ICountDownLatch stopSignal = instance.getCountDownLatch("stopSignal");
        stopSignal.await(Integer.MAX_VALUE, TimeUnit.DAYS);
        Hazelcast.shutdownAll();
    }
}
