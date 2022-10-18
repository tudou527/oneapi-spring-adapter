package com.oneapi.spring.testSuite;

public class NestedClass {
    @CustomController(version={AuthOperationEnum.ARTICLE_SEARCH}, value="anValue", required=false, index=2, name={"a", "b", "c"})
    class Controller {
        @GetMapping(value = "/test/a", consumes="application/xml", produces = "application/json; encoding=utf-8")
        public String index(){
            return "Hello";
        }
    }
}
