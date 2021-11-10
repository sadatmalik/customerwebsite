package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class NewCarController {

    private final CarService carService;

    @GetMapping("/new-car")
    public String showNewCarPage(Model model) {
        Car car = new Car();
        model.addAttribute("car", car);
        return "new-car";
    }

    @PostMapping("/save-car")
    public String save(@ModelAttribute("car") Car car, Model model) {
        try {
            carService.saveCar(car);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        return "redirect:/cars";
    }
}
