package com.godone.testSuite.field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.godone.testSuite.AuthOperationEnum;
import com.godone.testSuite.Description;

public class ComplexField<T> {
    private boolean success;
    private T result;
    private String errorMsg;
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
