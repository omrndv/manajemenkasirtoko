package com.kasir.manajemenkasir.repository;

import com.kasir.manajemenkasir.model.Barang;
import com.kasir.manajemenkasir.model.Toko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface BarangRepository extends JpaRepository<Barang, Integer> {
    List<Barang> findByToko(Toko toko);

    @Transactional
    @Modifying
    @Query(value = "UPDATE item_transaksi SET id_barang = NULL WHERE id_barang = ?1", nativeQuery = true)
    void nullifyItemTransaksiReferences(int idBarang);
}
