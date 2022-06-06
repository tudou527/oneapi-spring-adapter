package com.godone.testSuite.field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.godone.testSuite.AuthOperationEnum;
import com.godone.testSuite.Description;

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
    
    
    private T genericField;
    private String strField;
    private Boolean boolField;
    private FieldWithDefaultValue result2;
    private Description<T, T> genericProperty;
    
    private List<Description<String, Description<T, T>>> complexGenericProperty1;
    private HashMap<Description<AuthOperationEnum, Long>, String> complexGenericProperty2;
    private Map<Description<AuthOperationEnum, Long>, String> complexGenericProperty3;
    private Map<Description<AuthOperationEnum, Long>, T> complexGenericProperty4;
    private Map<Description<AuthOperationEnum[], Long>, T> complexGenericProperty5;
    private Map<Description<String[], Long>, T> complexGenericProperty6;
    private Map<Description<String, Long>, T[]> complexGenericProperty7;
    
    private List<T> genericList;
    private HashMap<T, Long> genericHashMap;
 
    private T[] genericArr;
    private List<T[]> genericArrList;
    private HashMap<T[], Long> genericArrHashMap;
}
