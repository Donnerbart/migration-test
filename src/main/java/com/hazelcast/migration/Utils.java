package com.hazelcast.migration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.Partition;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.migration.domain.DomainObject;
import com.hazelcast.migration.domain.DomainObjectFactory;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.hazelcast.instance.TestUtil.getNode;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

public final class Utils {

    private Utils() {
    }

    static void waitClusterSize(HazelcastInstance hz, int clusterSize) throws InterruptedException {
        while (true) {
            int actualClusterSize = hz.getCluster().getMembers().size();
            if (actualClusterSize >= clusterSize) {
                return;
            }
            System.out.println(format("Cluster size is %d/%d", actualClusterSize, clusterSize));
            Thread.sleep(1000);
        }
    }

    static Set<String> generateUniqueStrings(int uniqueStringsCount) {
        Set<String> stringsSet = new HashSet<String>(uniqueStringsCount);
        do {
            String randomString = RandomStringUtils.randomAlphabetic(30);
            stringsSet.add(randomString);
        } while (stringsSet.size() != uniqueStringsCount);
        return stringsSet;
    }

    static DomainObject createNewDomainObject(DomainObjectFactory objectFactory, String indexedField) {
        DomainObject object = objectFactory.newInstance();
        object.setKey(randomAlphanumeric(7));
        object.setStringVal(indexedField);
        object.setIntVal(nextInt(0, Integer.MAX_VALUE));
        object.setLongVal(nextLong(0, Long.MAX_VALUE));
        object.setDoubleVal(nextDouble(0.0, Double.MAX_VALUE));
        return object;
    }

    static int getLocalPartitionsCount(HazelcastInstance instance) {
        Member localMember = instance.getCluster().getLocalMember();
        Set<Partition> partitions = instance.getPartitionService().getPartitions();
        int count = 0;
        for (Partition partition : partitions) {
            if (localMember.equals(partition.getOwner())) {
                count++;
            }
        }
        return count;
    }

    public static Collection<Integer> getOwnedPartitions(HazelcastInstance instance) {
        MapService mapService = getMapService(instance);
        return mapService.getMapServiceContext().getOwnedPartitions();
    }

    private static MapService getMapService(HazelcastInstance instance) {
        return (MapService) getNode(instance).getNodeEngine().getService(MapService.SERVICE_NAME);
    }
}
