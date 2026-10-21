package com.project.controller;

import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.domain.Role;
import com.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/index")
    public String main(Model model,
                       @SessionAttribute(name = SessionKeys.LOGIN_USER, required = false) LoginUser user) {
        model.addAttribute("stations", mainService.getStations());
        model.addAttribute("notices", mainService.getLatestNotices());
        if (user != null && user.role() == Role.USER) {
            model.addAttribute("qnas", mainService.getLatestQnas());
        }
        return "main";
    }
}

