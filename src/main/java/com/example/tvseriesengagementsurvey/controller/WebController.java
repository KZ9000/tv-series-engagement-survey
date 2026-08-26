package com.example.tvseriesengagementsurvey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/series")
    public String series() {
        return "series";
    }

    @GetMapping("/rate")
    public String rate() {
        return "rate";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
