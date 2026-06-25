package com.kasir.manajemenkasir.repository;

import com.kasir.manajemenkasir.model.Barang;
import com.kasir.manajemenkasir.model.Toko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BarangRepository extends JpaRepository<Barang, Integer> {
    List<Barang> findByToko(Toko toko);
}
