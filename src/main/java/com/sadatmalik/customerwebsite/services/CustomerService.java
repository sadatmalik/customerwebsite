package com.sadatmalik.customerwebsite.services;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getCustomers();

    Customer saveCustomer(Customer customer);

    Customer getCustomer(Long id) throws NoSuchCustomerException;

    void deleteCustomer(Long id) throws NoSuchCustomerException;

    List<Customer> saveAllCustomer(List<Customer> customerList);

    Customer assignCar(Long customerId, Long carId) throws NoSuchCustomerException, NoSuchCarException;

    Customer removeCar(Long customerId) throws NoSuchCustomerException;
}