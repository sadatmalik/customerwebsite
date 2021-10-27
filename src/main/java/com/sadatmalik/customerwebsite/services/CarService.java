package com.sadatmalik.customerwebsite.services;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.model.Car;

import java.util.List;

public interface CarService {

    List<Car> getCars();

    Car saveCar(Car car);

    Car getCar(Long id) throws NoSuchCarException;

    void deleteCar(Long id) throws NoSuchCarException;

    List<Car> saveAllCars(List<Car> carList);
}