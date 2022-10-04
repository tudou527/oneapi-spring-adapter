package com.oneapi.spring.testSuite;

public abstract class BaseGenericObjectPool<T> {
    static class IdentityWrapper<T> {
        public T instance;
        public String wrapperName;

        public IdentityWrapper(T instance) {
        }
    }
}
