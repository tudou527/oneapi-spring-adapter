package com.oneapi.spring.testSuite.field;

import java.math.BigDecimal;
import java.util.*;

public class FieldWithDefaultValue {
    /**
     * 字段注释
     */
    private String stringField = "hello";
    // 单行字段注释
    private long longField;
    private long[] longArrayField;
    private String[][] stringArrayField;
    private List<Long> longListField;
    private Long longObjectField;
    private int intField = 99;
    private Integer intObjectField = 100;
    private HashMap<String, String> stringMap;

    private Date dateField = new Date();
    private Date dateField2;
    private Date dateField3;
    private float floatField = 1.2f;
    private Float floatObjectField = 2.2f;
    private short shortField = 1;
    private Short shortObjectField = 2;
    private double doubleField = 1.67;
    private Double doubleObjectField = 0.67;
    private BigDecimal bigDecimalObjectField = new BigDecimal(0.1);
    private boolean booleanField = false;
    private Boolean booleanObjectField = true;
    private ArrayList arrayListField = new ArrayList();
    private Collection collectionField = new ArrayList();
    
    private static final String staticFinalField = "staticFinalField";
    private static String staticField = "staticField";
    private final String finalField = "finalField";
    
    private static Integer staticIntegerField = 123;
    private final float finalFloatField = 3.3f;
    
    private transient int age = 0;
    
    private ArrayList<Double> doubleArrayListField;
    private Collection<Boolean> booleanCollectionField;
}
