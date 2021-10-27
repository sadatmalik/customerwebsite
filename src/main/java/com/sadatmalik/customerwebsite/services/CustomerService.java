package com.sadatmalik.customerwebsite.services;

import com.sadatmalik.customerwebsite.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getCustomers();

    Customer saveCustomer(Customer customer);

    Customer getCustomer(Long id);

    void deleteCustomer(Long id);

    List<Customer> saveAllCustomer(List<Customer> customerList);
}