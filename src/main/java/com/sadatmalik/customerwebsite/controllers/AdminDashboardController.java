package com.sadatmalik.customerwebsite.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AdminDashboardController {

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard() {
        return "admin-dashboard";
    }
}
