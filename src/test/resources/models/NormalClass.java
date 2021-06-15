package com.etosun.test;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This is class Description
 * @author tudou527
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NormalClass {

    Logger logger = LoggerFactory.getLogger(Util.class);

    public String getToken() {
        return "";
    }
}
