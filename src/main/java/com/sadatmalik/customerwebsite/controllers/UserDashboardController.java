package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.model.User;
import com.sadatmalik.customerwebsite.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class UserDashboardController {

    @Autowired
    private final CarService carService;

    @GetMapping("/user-dashboard")
    public String showUserDashboard(@CurrentSecurityContext(expression = "authentication")
                                                Authentication authentication,
                                    Model model) {

        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);

        List<Car> cars = carService.getCars();
        List<Car> unassignedCars = cars.stream()
                .filter((car) -> car.getCustomer() == null)
                .collect(Collectors.toList());
        model.addAttribute("cars", unassignedCars);

        return "user-dashboard";
    }
}
