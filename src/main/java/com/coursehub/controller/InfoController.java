package com.coursehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    @GetMapping("/about")
    public String about() {
        return "info/about";
    }

    @GetMapping("/faq")
    public String faq() {
        return "info/faq";
    }
}
