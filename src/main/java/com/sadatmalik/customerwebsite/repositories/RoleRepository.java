package com.sadatmalik.customerwebsite.repositories;

import com.sadatmalik.customerwebsite.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Authority, Long> {

    Authority getByAuthority(Authority.RoleEnum role);
}
