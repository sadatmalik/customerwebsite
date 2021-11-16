package com.sadatmalik.customerwebsite.repositories;

import com.sadatmalik.customerwebsite.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

}