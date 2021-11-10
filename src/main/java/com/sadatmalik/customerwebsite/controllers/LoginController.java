package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.services.CarService;
import com.sadatmalik.customerwebsite.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final CustomerService customerService;
    private final CarService carService;

    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

}
