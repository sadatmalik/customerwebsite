package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.model.Authority;
import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.model.Customer;
import com.sadatmalik.customerwebsite.model.User;
import com.sadatmalik.customerwebsite.services.CarService;
import com.sadatmalik.customerwebsite.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final CustomerService customerService;
    private final CarService carService;

    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

    @GetMapping("/login?success")
    public String showAdminOrUserDashboard(@CurrentSecurityContext Authentication authentication) {
        List<Authority> auth = ((User) authentication.getPrincipal()).getAuthorities();

        if (auth.get(0).getAuthority().equals(Authority.RoleEnum.ADMIN_ROLE)) {
            return "admin-dashboard";
        } else {
            // assumes USER_ROLE as ony have 2 roles in our system at the moment
            return "user-dashboard";
        }
    }







    @GetMapping("/customer-list")
    public String showCustomerList(Model model) {
        // Here you call the service to retrieve all the customers
        final List<Customer> customerList = customerService.getCustomers();

        // Once the customers are retrieved, you can store them in model and return it to the view
        model.addAttribute("customerList", customerList);
        return "customer-list";
    }

    @GetMapping("/edit/{id}")
    // The path variable "id" is used to pull a customer from the database
    public ModelAndView showEditCustomerPage(@PathVariable(name = "id") Long id) {
        // Since the previous methods use Model, this one uses ModelAndView
        // to get some experience using both. Model is more common these days,
        // but ModelAndView accomplishes the same thing and can be useful in
        // certain circumstances. The view name is passed to the constructor.
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

    @GetMapping("/assign/{id}")
    public String showAssignCarPage(@PathVariable(name = "id") Long id, Model model) {
        try {
            Customer customer = customerService.getCustomer(id);
            model.addAttribute("customer", customer);
            List<Car> cars = carService.getCars();
            model.addAttribute("cars", cars);
            return "select-customer-car";
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
            return "redirect:/customer-list";

        } catch (NoSuchCarException | NoSuchCustomerException e) {
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
