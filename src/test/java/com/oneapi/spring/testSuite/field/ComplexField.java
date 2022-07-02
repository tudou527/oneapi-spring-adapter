package com.oneapi.spring.testSuite.field;

import java.util.HashMap;
import java.util.List;

import com.oneapi.spring.testSuite.AuthOperationEnum;
import com.oneapi.spring.testSuite.Description;
import com.oneapi.spring.testSuite.Result;

public class ComplexField<T> {
    // 内置类型
    private boolean biBool;
    private byte biByte;
    private short biShort;
    private int biInt;
    private long biLong;
    private float biFloat;
    private double biDouble;
    private char biChar;
    
    // 简单类型
    private T genericField;
    private String strField;
    private Boolean boolField;
    private FieldWithDefaultValue result2;
    
    // 集合
    private T[] genericArr;
    private List<T[]> genericArrList;
    private Description<String, String>[] customGenericArr;
    private Description<String, String>[][] customGenericArrOfArr;
    
    // 各种组合情况
    private Description<T, T> genericProperty;
    private List<Description<String, Description<T, T>>> complexGenericProperty1;
    private HashMap<Description<AuthOperationEnum[], Long>, String> complexGenericProperty2;

    // 子类
    private ComplexSubClass subClass;
    
    // 扩展类
    private Result<? extends Description> extendField;
    
    private class ComplexSubClass {
        private String subClassName;
    }
}
