package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.exceptions.NoSuchUserException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping("/delete-user/{cust_id}")
    public String deleteCustomer(@PathVariable(name = "cust_id") Long id, Model model) {

        try {
            Customer customer = customerService.getCustomer(id);
            userService.deleteUser(customer.getUser().getId());
            customerService.deleteCustomer(id);
            return "redirect:/admin-dashboard";
        } catch (NoSuchCustomerException | NoSuchUserException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


}
