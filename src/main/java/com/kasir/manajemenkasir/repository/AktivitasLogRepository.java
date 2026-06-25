package com.kasir.manajemenkasir.repository;

import com.kasir.manajemenkasir.model.AktivitasLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AktivitasLogRepository extends JpaRepository<AktivitasLog, Integer> {
    List<AktivitasLog> findAllByOrderByIdDesc();
}
