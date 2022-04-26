package com.godone.testSuite;

import java.util.ArrayList;

public @interface CustomAn {
    String value();
    
    boolean required();
    
    int index();
    
    String[] name();

    AuthOperationEnum[] version();
}
