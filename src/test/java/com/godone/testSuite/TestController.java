package com.godone.testSuite;

import com.google.inject.Singleton;

/**
 * Spring Mvc的根路径、健康检查等。
 *
 * @author weber.wb
 */
@Singleton
@CustomAn(version={AuthOperationEnum.ARTICLE_SEARCH}, value="anValue", required=false, index=2, name={"a", "b", "c"})
public class TestController {
    private String jdbcTemplate;
}
