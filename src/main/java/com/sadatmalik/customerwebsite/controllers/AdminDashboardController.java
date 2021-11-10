package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.model.Customer;
import com.sadatmalik.customerwebsite.services.CarService;
import com.sadatmalik.customerwebsite.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AdminDashboardController {

    @Autowired
    private final CustomerService customerService;

    @Autowired
    private final CarService carService;

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(Model model) {
        List<Customer> customers = customerService.getCustomers();
        model.addAttribute("customerList", customers);
        List<Car> cars = carService.getCars();
        model.addAttribute("cars", cars);
        return "admin-dashboard";
    }

    @GetMapping("/edit-customer/{id}")
    // The path variable "id" is used to pull a customer from the database
    public ModelAndView showEditCustomerPage(@PathVariable(name = "id") Long id) {
        try {
            Customer customer = customerService.getCustomer(id);
            ModelAndView mav = new ModelAndView("edit-customer");
            mav.addObject("customer", customer);
            return mav;

        } catch (NoSuchCustomerException e) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", e.getMessage());
            return mav;
        }
    }

    @PostMapping(value = {"/update-customer/{id}"})
    public String saveCustomer(@ModelAttribute("customer") Customer customer,
                                      Model model) {
        try {
            customerService.saveCustomer(customer);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        return "redirect:/admin-dashboard";
    }




}
