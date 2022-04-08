package com.etosun.godone.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring Mvc的根路径、健康检查等。
 *
 * @author weber.wb
 */
@Controller
public class MainController {
    @GetMapping("/**")
    public String index(HttpServletRequest request, Model model) {
        return "index";
    }
}
