package com.hazelcast.instance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Partition;
import com.hazelcast.core.PartitionService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.SerializationService;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public final class TestUtil {

    private static final SerializationService serializationService = new DefaultSerializationServiceBuilder().build();

    private TestUtil() {
    }

    public static Data toData(Object obj) {
        return serializationService.toData(obj);
    }

    public static Object toObject(Data data) {
        return serializationService.toObject(data);
    }

    public static Node getNode(HazelcastInstance hz) {
        HazelcastInstanceImpl impl = getHazelcastInstanceImpl(hz);
        if (impl == null) {
            throw new NullPointerException("HazelcastInstanceImpl is null");
        }
        return impl.node;
    }

    public static HazelcastInstanceImpl getHazelcastInstanceImpl(HazelcastInstance hz) {
        HazelcastInstanceImpl impl = null;
        if (hz instanceof HazelcastInstanceProxy) {
            impl = ((HazelcastInstanceProxy) hz).original;
        } else if (hz instanceof HazelcastInstanceImpl) {
            impl = (HazelcastInstanceImpl) hz;
        }
        return impl;
    }

    public static void terminateInstance(HazelcastInstance hz) {
        final Node node = getNode(hz);
        node.getConnectionManager().shutdown();
        node.shutdown(true);
    }

    public static void warmUpPartitions(HazelcastInstance... instances) throws InterruptedException {
        for (HazelcastInstance instance : instances) {
            final PartitionService ps = instance.getPartitionService();
            for (Partition partition : ps.getPartitions()) {
                while (partition.getOwner() == null) {
                    Thread.sleep(10);
                }
            }
        }
    }

    public static void warmUpPartitions(Collection<HazelcastInstance> instances) throws InterruptedException {
        for (HazelcastInstance instance : instances) {
            final PartitionService ps = instance.getPartitionService();
            for (Partition partition : ps.getPartitions()) {
                while (partition.getOwner() == null) {
                    Thread.sleep(10);
                }
            }
        }
    }

    public static Integer getAvailablePort(int basePort) {
        return getAvailablePorts(basePort, 1).get(0);
    }

    public static List<Integer> getAvailablePorts(int basePort, int portCount) {
        List<Integer> availablePorts = new ArrayList<Integer>();
        int port = basePort;
        for (int i = 0; i < portCount; i++) {
            while (!isPortAvailable(port)) {
                port++;
            }
            availablePorts.add(port++);
        }
        return availablePorts;
    }

    // checks the DatagramSocket as well to check if the port is available in UDP and TCP
    public static boolean isPortAvailable(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            if (ds != null) {
                ds.close();
            }
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }
}
