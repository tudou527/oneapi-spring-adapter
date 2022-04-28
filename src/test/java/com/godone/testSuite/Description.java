package com.godone.testSuite;

/**
 * class
 * 多行注释
 * @author authorName
 * @date 2022-04-08
 */
class Description<T, U> {
    String T;
    int U;
    /**
     * methodA
     * 多行注释
     * @deprecated 不久之后废弃
     */
    public void methodA() {
    }

    // methodB 单行注释
    public void methodB() {}
    
    /**
     * methodC 方法注释
     * @param a 参数 A
     * @param b 参数 B
     * @param c 参数
     * @return void
     */
    public void methodC(String a, int b, boolean c) {
    }
}