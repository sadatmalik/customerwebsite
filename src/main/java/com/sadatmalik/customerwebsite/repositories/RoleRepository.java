package com.sadatmalik.customerwebsite.repositories;

import com.sadatmalik.customerwebsite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<User, Long> {
}
