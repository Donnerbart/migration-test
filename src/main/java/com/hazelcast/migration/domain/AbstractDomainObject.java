package com.hazelcast.migration.domain;

abstract class AbstractDomainObject implements DomainObject {

    protected String key;
    protected String stringVal;
    protected double doubleVal;
    protected long longVal;
    protected int intVal;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getStringVal() {
        return stringVal;
    }

    @Override
    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    @Override
    public double getDoubleVal() {
        return doubleVal;
    }

    @Override
    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
    }

    @Override
    public long getLongVal() {
        return longVal;
    }

    @Override
    public void setLongVal(long longVal) {
        this.longVal = longVal;
    }

    @Override
    public int getIntVal() {
        return intVal;
    }

    @Override
    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        DomainObject that = (DomainObject) other;
        return !(key != null ? !key.equals(that.getKey()) : that.getKey() != null);
    }

    @Override
    public int hashCode() {
        return (key != null ? key.hashCode() : 0);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{"
                + "key='" + key + '\''
                + ", stringVal='" + stringVal + '\''
                + ", doubleVal=" + doubleVal
                + ", longVal=" + longVal
                + ", intVal=" + intVal
                + '}';
    }
}
