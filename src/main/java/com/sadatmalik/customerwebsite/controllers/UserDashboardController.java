package com.sadatmalik.customerwebsite.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserDashboardController {

    @GetMapping("/user-dashboard")
    public String showUserDashboard() {
        return "user-dashboard";
    }
}
