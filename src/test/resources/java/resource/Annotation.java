package com.etosun.godone.test;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.brain.job.common.log.BizMonitorDefinition;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring Mvc的根路径、健康检查等。
 *
 * @author weber.wb
 */
@RestController
@RequestMapping(value = "/staffJob")
public class Annotation {
    @Autowired(value="anValue", required=false, index=2, name={"a", "b", "c"})
    protected HttpServletRequest request;

    private JdbcTemplate jdbcTemplate;

    @PostMapping("upload")
    @ResponseBody
    @BizMonitorDefinition(operationCode = "upload", operationName = "人员导入")
    public String index(HttpServletRequest request, Model model) {
        return "index";
    }
}
