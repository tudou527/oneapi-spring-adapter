package com.godone.testSuite;

public enum AuthOperationEnum {
    ACCESS_DATA_BOARD("athena_data_board_op", "数据看板操作"),
    ARTICLE_SEARCH("athena_article_search_op", "文章搜索和查看操作"),
    ARTICLE_ORDER("athena_article_order_op", "文章排序操作"),
    ARTICLE_PUBLISH("athena_publish_article_op", "文章发布操作");
    
    private String code;
    
    private String name;
    
    AuthOperationEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
}