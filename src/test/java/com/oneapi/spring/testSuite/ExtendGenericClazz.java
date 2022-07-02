package com.oneapi.spring.testSuite;

import com.oneapi.spring.testSuite.field.ComplexField;
import com.oneapi.spring.testSuite.field.FieldWithDefaultValue;

public class ExtendGenericClazz<T> extends Description<FieldWithDefaultValue, ComplexField<T>> {
    T selfProperty;
}
