package com.payflow.accountservice.repository;

import com.payflow.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAccountsByUserId(Long id);

    Boolean existsByAccountNumber(String accountNumber);

}
