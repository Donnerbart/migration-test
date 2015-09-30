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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.hazelcast.migration.Constants.CHECK_QUERY_RESULT;
import static com.hazelcast.migration.Constants.FIX_OWNED_PARTITION_AT_QUERY_INDEX;
import static com.hazelcast.migration.Constants.QUERY_COUNT;
import static com.hazelcast.migration.Constants.RECORDS_PER_UNIQUE;
import static com.hazelcast.migration.Utils.getMapServiceContext;
import static com.hazelcast.migration.Utils.logPartitionState;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class ActiveMember {

    public static void main(String[] args) throws InterruptedException {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();
        logPartitionState(instance);

        ICountDownLatch startQueries = instance.getCountDownLatch("startQueries");
        startQueries.trySetCount(1);

        // wait for signal
        System.out.println("Wait for query start...");
        startQueries.await(Integer.MAX_VALUE, DAYS);
        System.out.println("Done!");

        // prepare data
        System.out.println("Prepare data...");
        Random random = new Random();
        ISet<String> set = instance.getSet("set");
        String[] uniqueStrings = set.toArray(new String[set.size()]);
        System.out.println("Done!");

        logPartitionState(instance);

        // query map
        System.out.println("Starting queries...");
        IMap<String, DomainObject> map = instance.getMap("map");
        Set<String> objectKeys = new HashSet<String>();
        for (int i = 1; i <= QUERY_COUNT; i++) {
            String string = uniqueStrings[random.nextInt(uniqueStrings.length)];
            Predicate predicate = Predicates.equal("stringVal", string);

            long started = System.nanoTime();
            Collection<DomainObject> objects = map.values(predicate);
            long diff = System.nanoTime() - started;
            System.out.println(format("#%5d Query took %5d ms (%d results)", i, NANOSECONDS.toMillis(diff), objects.size()));

            // check received objects
            if (CHECK_QUERY_RESULT) {
                for (DomainObject object : objects) {
                    if (object == null) {
                        throw new RuntimeException("returned object is null");
                    }
                    objectKeys.add(object.getKey());
                }
                if (objectKeys.size() != RECORDS_PER_UNIQUE) {
                    throw new RuntimeException("got duplicate objects!");
                }
                objectKeys.clear();
            }
            if (i == FIX_OWNED_PARTITION_AT_QUERY_INDEX) {
                logPartitionState(instance);
                getMapServiceContext(instance).reloadOwnedPartitions();
                logPartitionState(instance);
            }
        }
        System.out.println("Done!");

        // wait for shutdown
        System.out.println("Wait for shutdown...");
        ICountDownLatch stopSignal = instance.getCountDownLatch("stopSignal");
        stopSignal.await(Integer.MAX_VALUE, DAYS);
        Hazelcast.shutdownAll();
    }
}
