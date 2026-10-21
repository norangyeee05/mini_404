package com.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Redirects the site root to the main index page. */
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/index";
    }
}
