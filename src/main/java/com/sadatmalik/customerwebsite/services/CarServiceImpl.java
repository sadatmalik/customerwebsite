package com.sadatmalik.customerwebsite.services;

import com.sadatmalik.customerwebsite.exceptions.NoSuchCarException;
import com.sadatmalik.customerwebsite.model.Car;
import com.sadatmalik.customerwebsite.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarServiceImpl implements CarService {

    private final CarRepository carRepo;

    // The findAll function gets all the cars by doing a SELECT query in the DB.
    @Override
    public List<Car> getCars() {
        return carRepo.findAll();
    }

    // The save function uses an INSERT query in the DB.
    @Override
    @Transactional
    public Car saveCar(Car car) {
        car.validate();
        return carRepo.save(car);
    }

    // The findById function uses a SELECT query with a WHERE clause in the DB.
    @Override
    public Car getCar(Long id) throws NoSuchCarException {
        Optional<Car> carOptional = carRepo.findById(id);

        if (carOptional.isEmpty()) {
            throw new NoSuchCarException("No car with ID \" + id + \" could be found.");
        }

        return carOptional.get();
    }

    // The deleteById function deletes the car by doing a DELETE in the DB.
    @Override
    public void deleteCar(Long id) throws NoSuchCarException {
        Optional<Car> carOptional = carRepo.findById(id);

        if (carOptional.isEmpty()) {
            throw new NoSuchCarException("No car with ID \" + id + \" could be found.");
        }

        carRepo.deleteById(id);
    }

    // The saveAll function would do multiple INSERTS into the DB.
    @Override
    public List<Car> saveAllCars(List<Car> carList) {
        carList.forEach(Car::validate);
        return carRepo.saveAll(carList);
    }
}
