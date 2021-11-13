package com.sadatmalik.customerwebsite.services;

import com.sadatmalik.customerwebsite.exceptions.NoSuchUserException;
import com.sadatmalik.customerwebsite.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    public List<User> findAll();

    public User saveUser(User user) throws IllegalStateException;

    public void deleteUser(Long id) throws NoSuchUserException;
}
