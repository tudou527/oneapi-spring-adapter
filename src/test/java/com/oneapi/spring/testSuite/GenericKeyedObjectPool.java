package com.oneapi.spring.testSuite;

public class GenericKeyedObjectPool<K, T> extends BaseGenericObjectPool<T> {
    public IdentityWrapper<T> wrapper;
    public K name;
}
