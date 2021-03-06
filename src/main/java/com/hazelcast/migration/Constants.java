package com.hazelcast.migration;

import com.hazelcast.migration.domain.Strategy;

public class Constants {

    public static final Strategy STRATEGY = Strategy.PORTABLE;

    public static final int CLUSTER_SIZE = 3;
    public static final boolean USE_ASYNC_STREAMER = true;
    public static final boolean CHECK_QUERY_RESULT = false;

    public static final int ITEM_COUNT = 1000000;
    public static final int RECORDS_PER_UNIQUE = 10000;
    public static final int QUERY_COUNT = 100;

    public static final int FIX_OWNED_PARTITION_AT_QUERY_INDEX = 50;

    public static final int PASSIVE_MEMBER_AUTO_SHUTDOWN_SECONDS = 20;
}
