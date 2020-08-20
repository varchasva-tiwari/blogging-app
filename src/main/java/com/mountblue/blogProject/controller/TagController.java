package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TagController {
    @Autowired
    private TagService tagService;

    @RequestMapping("/readTags")
    private ModelAndView readTags() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("tag", tagService.read());
        return mv;
    }
}
