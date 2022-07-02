package com.oneapi.spring.testSuite;

public @interface CustomAn {
    String value();
    
    boolean required();
    
    int index();
    
    String[] name();

    AuthOperationEnum[] version();
}
