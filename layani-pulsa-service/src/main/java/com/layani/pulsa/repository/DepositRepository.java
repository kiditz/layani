package com.layani.pulsa.repository;

import com.layani.pulsa.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Deposit findByOutletId(Long outletId);
}