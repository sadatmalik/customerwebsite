package com.sadatmalik.customerwebsite.services;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.exceptions.NoSuchCustomerException;
import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.model.Customer;
import com.sadatmalik.customerwebsite.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CarService carService;

    // The findAll function gets all the customers by doing a SELECT query in the DB.
    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    // The save function uses an INSERT query in the DB.
    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        customer.validate();
        return customerRepository.save(customer);
    }

    // The findById function uses a SELECT query with a WHERE clause in the DB.
    @Override
    public Customer getCustomer(Long id) throws NoSuchCustomerException {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isEmpty()) {
            throw new NoSuchCustomerException("No customer with ID " + id + " could be found.");
        }

        return customerOptional.get();
    }

    // The deleteById function deletes the customer by doing a DELETE in the DB.
    @Override
    @Transactional
    public void deleteCustomer(Long id) throws NoSuchCustomerException {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isEmpty()) {
            throw new NoSuchCustomerException("No customer with ID " + id + " could be found.");
        }

        customerRepository.deleteById(id);
    }

    // The saveAll function would do multiple INSERTS into the DB.
    @Override
    @Transactional
    public List<Customer> saveAllCustomer(List<Customer> customerList) {
        customerList.forEach(Customer::validate);
        return customerRepository.saveAll(customerList);
    }

    @Override
    @Transactional
    public Customer assignCar(Long customerId, Long carId) throws NoSuchCustomerException,
            NoSuchCarException {

        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new NoSuchCustomerException("No customer with ID " + customerId + " could be found.");
        }
        Customer customer = customerOptional.get();

        Car car = carService.getCar(carId);
        customer.setCar(car);

        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer removeCar(Long customerId) throws NoSuchCustomerException {
        // TODO: 27/10/2021 refactor this code into a getCustomer method :
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new NoSuchCustomerException("No customer with ID " + customerId + " could be found.");
        }
        Customer customer = customerOptional.get();

        customer.setCar(null);

        return customerRepository.save(customer);
    }
}