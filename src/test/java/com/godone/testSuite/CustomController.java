package com.godone.testSuite;

public @interface CustomController {
    String value();
    
    boolean required();
    
    int index();
    
    String[] name();

    AuthOperationEnum[] version();
}
