package com.godone.testSuite;

/**
 * class
 * 多行注释
 * @author authorName
 * @date 2022-04-08
 */
class Description {
    // fieldName1 单行注释
    public String fieldName1;
    /**
     * fieldName2
     * 多行注释
     * @author author1
     */
    public String fieldName2;

    public SubClassDes fieldName3;

    /**
     * methodA
     * 多行注释
     * @deprecated 不久之后废弃
     */
    public void methodA() {
    }

    // methodB 单行注释
    public void methodB() {}

    // class 单行注释
    private class SubClassDes {
        private String name;
    }
}