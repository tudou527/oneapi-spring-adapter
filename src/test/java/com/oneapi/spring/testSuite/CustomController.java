package com.oneapi.spring.testSuite;

public @interface CustomController {
    String value();
    
    boolean required();
    
    int index();
    
    String[] name();

    AuthOperationEnum[] version();
}
