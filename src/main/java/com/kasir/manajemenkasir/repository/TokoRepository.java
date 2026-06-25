package com.kasir.manajemenkasir.repository;

import com.kasir.manajemenkasir.model.Toko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokoRepository extends JpaRepository<Toko, Integer> {
}
