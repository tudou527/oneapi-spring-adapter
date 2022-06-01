package com.godone.testSuite;

import com.godone.testSuite.field.ComplexField;
import com.godone.testSuite.field.FieldWithDefaultValue;

public class ExtendGenericClazz<T> extends Description<FieldWithDefaultValue, ComplexField<T>> {
    T selfProperty;
}
