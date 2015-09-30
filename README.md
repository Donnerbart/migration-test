# Usage

# To reproduce the bug

- Start `Constants.CLUSTER_SIZE` instances of `ReproductionMember`
- Find the member with a wrong `ownedPartitions` state, which looks like this:

```
Instance started...

Sep 30, 2015 11:49:47 AM com.hazelcast.core.LifecycleService
INFO: [172.16.18.1]:5702 [dev] [3.6-SNAPSHOT] Address[172.16.18.1]:5702 is STARTED

Sep 30, 2015 11:49:47 AM com.hazelcast.internal.monitors.HealthMonitor
INFO: [172.16.18.1]:5702 [dev] [3.6-SNAPSHOT] processors=8, physical.memory.total=11.8G, physical.memory.free=1.9G, swap.space.total=8.0G, swap.space.free=7.4G, heap.memory.used=25.5M, heap.memory.free=205.0M, heap.memory.total=230.5M, heap.memory.max=2.6G, heap.memory.used/total=0.00%, heap.memory.used/max=0.00%, minor.gc.count=3, minor.gc.time=16ms, major.gc.count=0, major.gc.time=0ms, load.process=0.50%, load.system=0.67%, load.systemAverage=32.00%, thread.count=46, thread.peakCount=46, cluster.timeDiff=-1, event.q.size=0, executor.q.async.size=0, executor.q.client.size=0, executor.q.query.size=0, executor.q.scheduled.size=0, executor.q.io.size=0, executor.q.system.size=0, executor.q.operations.size=0, executor.q.priorityOperation.size=0, operations.completed.count=580, executor.q.mapLoad.size=0, executor.q.mapLoadAllKeys.size=0, executor.q.cluster.size=0, executor.q.response.size=0, operations.running.count=0, operations.pending.invocations.percentage=0.00%, operations.pending.invocations.count=0, proxy.count=0, clientEndpoint.count=0, connection.active.count=3, client.connection.count=0, connection.count=2

Partition state version: 994, hasOngoingMigrationLocal: false, local partitions: 90, ownedPartitions (27): [91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117]
```

# To see how the bug affects indexed queries 

- Start `Constants.CLUSTER_SIZE` instances of `ActiveMember`
- Start `WarmupClient`
- Wait until data is filled in and queries have started
- The bad state of `ownedPartitions` will be fixed after `Constants.FIX_OWNED_PARTITION_AT_QUERY_INDEX` query executions
