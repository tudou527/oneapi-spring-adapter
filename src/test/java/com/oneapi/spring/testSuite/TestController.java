package com.oneapi.spring.testSuite;

import com.oneapi.spring.testSuite.field.FieldWithDefaultValue;
import com.google.inject.Singleton;

/**
 * Spring Mvc的根路径、健康检查等。
 *
 * @author weber.wb
 */
@Singleton
@CustomAn(version={AuthOperationEnum.ARTICLE_SEARCH}, value="anValue", required=false, index=2, name={"a", "b", "c"})
@CustomController(version={AuthOperationEnum.ARTICLE_SEARCH}, value="anValue", required=false, index=2, name={"a", "b", "c"})
public class TestController {
    private String jdbcTemplate;
    
    @GetMapping(value = "/test/a", consumes="application/xml", produces = "application/json; encoding=utf-8")
    private void testEmptyRouter(){
    }
    
    /**
     * 方法 contentTypeXml
     * params argsA 参数 A
     * @return Result<String>
     */
    @GetMapping(value = "/test/b", consumes="application/xml", produces = "application/json; encoding=utf-8")
    public Result<String> contentTypeXml(
        @CustomAn(version={AuthOperationEnum.ARTICLE_SEARCH}, value="anValue", required=false, index=2, name={"a", "b", "c"})
        FieldWithDefaultValue argsA
    ){
        return new Result<String>();
    }

    public Result<String> noArguments() {
        return new Result<String>();
    }

    @CustomClassAn(level=TestController.class)
    public Result<String> attrIsClassInAnnotation() {
        return new Result<String>();
    }
}
