package com.hazelcast.migration.domain;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class IdentifiedDataSerializableObjectFactory implements DataSerializableFactory {

    public static final int FACTORY_ID = 2000;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        switch (typeId) {
            case IdentifiedDataSerializableDomainObject.CLASS_ID:
                return new IdentifiedDataSerializableDomainObject();
            default:
                throw new IllegalArgumentException("Unknown type id " + typeId);
        }
    }
}
