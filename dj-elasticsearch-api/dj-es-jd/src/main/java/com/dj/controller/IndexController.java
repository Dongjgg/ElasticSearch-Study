package com.dj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping({"/","/index"})
    public String index() {
        return "index";
    }

//    @Autowired
//    private ContentService contentService;
//
//    @GetMapping("/parse/{keywords}")
//    public Boolean parse(@PathVariable("keywords") String keywords) throws IOException {
//        return contentService.parseContent(keywords);
//    }
}