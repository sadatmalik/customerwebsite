package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class AssignCarController {

    private final CustomerService customerService;

    @GetMapping("/assign-car/{customer_id}/{car_id}")
    public String assignCarToCustomer(@PathVariable(name = "customer_id") Long customerId,
                                      @PathVariable(name = "car_id") Long carId,
                                      Model model) {
        try {
            customerService.assignCar(customerId, carId);
            return "redirect:/admin-dashboard";

        } catch (NoSuchCarException | NoSuchCustomerException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

}
