package com.hazelcast.migration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.Partition;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.migration.domain.DomainObject;
import com.hazelcast.migration.domain.DomainObjectFactory;
import com.hazelcast.partition.InternalPartitionService;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.hazelcast.instance.TestUtil.getNode;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

public final class Utils {

    private Utils() {
    }

    public static void waitClusterSize(HazelcastInstance hz, int clusterSize) throws InterruptedException {
        while (true) {
            int actualClusterSize = hz.getCluster().getMembers().size();
            if (actualClusterSize >= clusterSize) {
                return;
            }
            System.out.println(format("Cluster size is %d/%d", actualClusterSize, clusterSize));
            SECONDS.sleep(1);
        }
    }

    public static Set<String> generateUniqueStrings(int uniqueStringsCount) {
        Set<String> stringsSet = new HashSet<String>(uniqueStringsCount);
        do {
            String randomString = RandomStringUtils.randomAlphabetic(30);
            stringsSet.add(randomString);
        } while (stringsSet.size() != uniqueStringsCount);
        return stringsSet;
    }

    public static DomainObject createNewDomainObject(DomainObjectFactory objectFactory, String indexedField) {
        DomainObject object = objectFactory.newInstance();
        object.setKey(randomAlphanumeric(7));
        object.setStringVal(indexedField);
        object.setIntVal(nextInt(0, Integer.MAX_VALUE));
        object.setLongVal(nextLong(0, Long.MAX_VALUE));
        object.setDoubleVal(nextDouble(0.0, Double.MAX_VALUE));
        return object;
    }

    public static InternalPartitionService getPartitionService(HazelcastInstance instance) {
        return getNode(instance).getNodeEngine().getPartitionService();
    }

    public static MapService getMapService(HazelcastInstance instance) {
        return (MapService) getNode(instance).getNodeEngine().getService(MapService.SERVICE_NAME);
    }

    public static MapServiceContext getMapServiceContext(HazelcastInstance instance) {
        return getMapService(instance).getMapServiceContext();
    }

    public static int getLocalPartitionsCount(HazelcastInstance instance) {
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

    public static void logPartitionData(HazelcastInstance instance) {
        int partitionStateVersion = getPartitionService(instance).getPartitionStateVersion();
        boolean hasOngoingMigrationLocal = getPartitionService(instance).hasOnGoingMigrationLocal();
        int localPartitionCount = getLocalPartitionsCount(instance);
        Collection<Integer> ownedPartitions = getMapServiceContext(instance).getOwnedPartitions();
        int ownedPartitionCount = ownedPartitions.size();

        System.out.println(format(
                "Partition state version: %d, hasOngoingMigrationLocal: %b, local partitions: %d vs. %d, ownedPartitions: %s",
                partitionStateVersion, hasOngoingMigrationLocal, localPartitionCount, ownedPartitionCount, ownedPartitions));
    }
}
