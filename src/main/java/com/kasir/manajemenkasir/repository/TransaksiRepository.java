package com.kasir.manajemenkasir.repository;

import com.kasir.manajemenkasir.model.Transaksi;
import com.kasir.manajemenkasir.model.Toko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, Integer> {
    List<Transaksi> findByToko(Toko toko);
}
