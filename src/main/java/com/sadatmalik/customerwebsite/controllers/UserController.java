package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.model.Customer;
import com.sadatmalik.customerwebsite.model.User;
import com.sadatmalik.customerwebsite.services.CustomerService;
import com.sadatmalik.customerwebsite.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    @Autowired
    private final CustomerService customerService;

    @Autowired
    private final UserService userService;

    @GetMapping("/new")
    public String showNewCustomerPage(Model model) {
        // Here a new (empty) Customer is created and then sent to the view
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "new-customer";
    }

    @GetMapping("/new-user")
    public String showRegisterNewUserPage(Model model) {
        User user = new User();
        user.setCustomer(new Customer());
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute("user") User user, Model model) {
        // Here a new (empty) Customer is created and then sent to the view
        Customer savedCustomer = customerService.saveCustomer(user.getCustomer());
        user.setCustomer(savedCustomer);
        User savedUser = userService.saveUser(user);
        model.addAttribute("customer", savedCustomer);
        return "new-customer";
    }

    @PostMapping(value = {"/save", "/update/{id}"})
    // As the Model is received back from the view, @ModelAttribute
    // creates a Customer based on the object you collected from
    // the HTML page above
    public String saveCustomerAndUser(@ModelAttribute("customer") Customer customer,
                                      Model model) {
        try {
            Customer savedCustomer = customerService.saveCustomer(customer);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        return "redirect:/customer-list";
    }

}
