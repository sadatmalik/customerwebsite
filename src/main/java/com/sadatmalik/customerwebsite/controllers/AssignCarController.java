package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.model.Customer;
import com.sadatmalik.customerwebsite.services.CarService;
import com.sadatmalik.customerwebsite.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AssignCarController {

    private final CustomerService customerService;
    private final CarService carService;

    @GetMapping("/assign-car/{customer_id}")
    public String showAssignCarPage(@PathVariable(name = "customer_id") Long id, Model model) {
        try {
            Customer customer = customerService.getCustomer(id);
            model.addAttribute("customer", customer);
            List<Car> cars = carService.getCars();
            model.addAttribute("cars", cars);
            return "assign-car";
        } catch (NoSuchCustomerException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

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

    @GetMapping("/unassign-car/{customer_id}")
    public String removeCarFromCustomer(@PathVariable(name = "customer_id") Long customerId,
                                        Model model) {
        try {
            customerService.removeCar(customerId);
            return "redirect:/admin-dashboard";
        } catch (NoSuchCustomerException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

}
