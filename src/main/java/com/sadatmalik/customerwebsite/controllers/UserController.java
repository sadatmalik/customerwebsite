package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.model.Authority;
import com.sadatmalik.customerwebsite.model.Customer;
import com.sadatmalik.customerwebsite.model.User;
import com.sadatmalik.customerwebsite.services.CustomerService;
import com.sadatmalik.customerwebsite.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {

    @Autowired
    private final CustomerService customerService;

    @Autowired
    private final UserService userService;

    @GetMapping("/new-user")
    public String showNewUserForm(Model model) {
        User user = new User();
        user.setCustomer(new Customer());
        model.addAttribute("user", user);
        return "new-user";
    }

    @PostMapping("/save-user")
    public String registerNewUser(
            @ModelAttribute("user") User user,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication,
            Model model) {

        try {
            Customer savedCustomer = customerService.saveCustomer(user.getCustomer());
            user.setCustomer(savedCustomer);
            User savedUser = userService.saveUser(user);
            model.addAttribute("savedUser", savedUser);
        }  catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }

        // redirect based on authenticated role
        User principal = (User) authentication.getPrincipal();
        if (principal != null) {
            List<Authority> auth = ((User) principal).getAuthorities();
            if (auth.get(0).getAuthority().equals(Authority.RoleEnum.ADMIN_ROLE.toString())) {
                return "redirect:/admin-dashboard";
            } else if (auth.get(0).getAuthority().equals(Authority.RoleEnum.USER_ROLE.toString())) {
                // assumes USER_ROLE as ony have 2 roles in our system at the moment
                return "redirect:/user-dashboard";
            }
        }

        return "index";
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
