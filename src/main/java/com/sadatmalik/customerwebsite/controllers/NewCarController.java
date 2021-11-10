package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "redirect:/admin-dashboard";
    }

    @RequestMapping("/delete-car/{car_id}")
    public String deleteCustomer(@PathVariable(name = "car_id") Long id, Model model) {

        try {
            carService.deleteCar(id);
            return "redirect:/admin-dashboard";
        } catch (NoSuchCarException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

}
