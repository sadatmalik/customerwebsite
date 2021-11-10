package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.services.CarService;
import com.sadatmalik.customerwebsite.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final CustomerService customerService;
    private final CarService carService;

    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

    @RequestMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(name = "id") Long id, Model model) {
        try {
            customerService.deleteCustomer(id);
            return "redirect:/customer-list";
        } catch (NoSuchCustomerException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }




    @GetMapping("/remove/{customer_id}")
    public String removeCarFromCustomer(@PathVariable(name = "customer_id") Long customerId,
                                        Model model) {
        try {
            customerService.removeCar(customerId);
            return "redirect:/customer-list";
        } catch (NoSuchCustomerException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

}
