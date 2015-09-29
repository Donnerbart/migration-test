package com.hazelcast.migration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import com.hazelcast.migration.domain.DomainObject;
import com.hazelcast.migration.domain.DomainObjectFactory;
import com.hazelcast.migration.loadsupport.Streamer;
import com.hazelcast.migration.loadsupport.StreamerFactory;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashSet;
import java.util.Set;

import static com.hazelcast.migration.Constants.CLUSTER_SIZE;
import static com.hazelcast.migration.Constants.RECORDS_PER_UNIQUE;
import static com.hazelcast.migration.Constants.STRATEGY;
import static com.hazelcast.migration.Constants.USE_ASYNC_STREAMER;
import static com.hazelcast.migration.Constants.ITEM_COUNT;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

public class WarmupClient {

    public static void main(String[] args) throws InterruptedException {
        // init cluster
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        waitClusterSize(instance, CLUSTER_SIZE);

        ICountDownLatch stopSignal = instance.getCountDownLatch("stopSignal");
        stopSignal.trySetCount(1);

        // prepare data
        int uniqueStringsCount = (int) Math.ceil(ITEM_COUNT / RECORDS_PER_UNIQUE);
        System.out.println(format("Prepare data (%d unique Strings)...", uniqueStringsCount));
        Set<String> stringsSet = generateUniqueStrings(uniqueStringsCount);

        ISet<String> set = instance.getSet("set");
        set.addAll(stringsSet);

        String[] strings = stringsSet.toArray(new String[uniqueStringsCount]);
        stringsSet.clear();
        System.out.println("Done!");

        // fill map
        System.out.println("Filling map...");
        IMap<String, DomainObject> map = instance.getMap("map");
        Streamer<String, DomainObject> streamer = StreamerFactory.getInstance(map, USE_ASYNC_STREAMER);
        DomainObjectFactory objectFactory = DomainObjectFactory.newFactory(STRATEGY);

        for (int i = 0; i < ITEM_COUNT; i++) {
            int index = i % uniqueStringsCount;
            String indexedField = strings[index];
            DomainObject object = createNewDomainObject(objectFactory, indexedField);
            streamer.pushEntry(object.getKey(), object);
        }
        streamer.await();
        System.out.println("Done!");

        // start other members
        System.out.println("Signaling other members...");
        ICountDownLatch startQueries = instance.getCountDownLatch("startQueries");
        startQueries.countDown();
        System.out.println("Done!");

        // leave cluster
        instance.shutdown();
    }

    private static void waitClusterSize(HazelcastInstance hz, int clusterSize) throws InterruptedException {
        while (true) {
            int actualClusterSize = hz.getCluster().getMembers().size();
            if (actualClusterSize >= clusterSize) {
                return;
            }
            System.out.println(format("Cluster size is %d/%d", actualClusterSize, clusterSize));
            Thread.sleep(1000);
        }
    }

    private static Set<String> generateUniqueStrings(int uniqueStringsCount) {
        Set<String> stringsSet = new HashSet<String>(uniqueStringsCount);
        do {
            String randomString = RandomStringUtils.randomAlphabetic(30);
            stringsSet.add(randomString);
        } while (stringsSet.size() != uniqueStringsCount);
        return stringsSet;
    }

    private static DomainObject createNewDomainObject(DomainObjectFactory objectFactory, String indexedField) {
        DomainObject object = objectFactory.newInstance();
        object.setKey(randomAlphanumeric(7));
        object.setStringVal(indexedField);
        object.setIntVal(nextInt(0, Integer.MAX_VALUE));
        object.setLongVal(nextLong(0, Long.MAX_VALUE));
        object.setDoubleVal(nextDouble(0.0, Double.MAX_VALUE));
        return object;
    }
}
