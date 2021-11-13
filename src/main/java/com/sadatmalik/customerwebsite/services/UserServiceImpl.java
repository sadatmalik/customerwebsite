package com.sadatmalik.customerwebsite.services;

import com.sadatmalik.customerwebsite.exceptions.NoSuchUserException;
import com.sadatmalik.customerwebsite.model.Authority;
import com.sadatmalik.customerwebsite.model.User;
import com.sadatmalik.customerwebsite.repositories.RoleRepository;
import com.sadatmalik.customerwebsite.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for: " + username));
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User saveUser(User user) throws IllegalStateException {

        try {
            loadUserByUsername(user.getUsername());
            // username already exists in DB
            throw new IllegalStateException("Cannot register - username already exists - " +
                    "please select a different Username");
        } catch (UsernameNotFoundException e) {
            // username does not exist, proceed
            LOGGER.info("Registering new user with unique username: " + user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getCustomer() != null && user.getAuthorities() == null) {
            Authority userRole = roleRepo.getByAuthority(Authority.RoleEnum.USER_ROLE);
            user.setAuthorities(Collections.singletonList(userRole));
        }

        return userRepo.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws NoSuchUserException {
        Optional<User> userOptional = userRepo.findById(id);

        if (userOptional.isEmpty()) {
            throw new NoSuchUserException("No user with ID " + id + " could be found.");
        }

        userRepo.deleteById(id);
    }

}
