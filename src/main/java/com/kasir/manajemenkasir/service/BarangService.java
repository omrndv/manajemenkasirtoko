package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.Barang;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BarangService {
    private final List<Barang> daftarBarang = new ArrayList<>();

    public BarangService() {
        daftarBarang.add(new Barang(1, "Beras 5kg", 75000, 20));
        daftarBarang.add(new Barang(2, "Minyak Goreng 1L", 18000, 30));
        daftarBarang.add(new Barang(3, "Gula Pasir 1kg", 16000, 25));
    }

    public List<Barang> getAllBarang() {
        return daftarBarang;
    }

    public int hitungTotalBarang() {
        return daftarBarang.size();
    }

    public int hitungTotalStok() {
        int totalStok = 0;

        for (Barang barang : daftarBarang) {
            totalStok += barang.getStok();
        }

        return totalStok;
    }

    public Barang getBarangById(int idBarang) {
        for (Barang barang : daftarBarang) {
            if (barang.getIdBarang() == idBarang) {
                return barang;
            }
        }

        return null;
    }

    public void tambahBarang(Barang barang) {
        int idBaru = 1;

        for (Barang dataBarang : daftarBarang) {
            if (dataBarang.getIdBarang() >= idBaru) {
                idBaru = dataBarang.getIdBarang() + 1;
            }
        }

        barang.setIdBarang(idBaru);
        daftarBarang.add(barang);
    }

    public void updateBarang(int idBarang, Barang barangBaru) {
        Barang barang = getBarangById(idBarang);

        if (barang != null) {
            barang.setNamaBarang(barangBaru.getNamaBarang());
            barang.setHarga(barangBaru.getHarga());
            barang.setStok(barangBaru.getStok());
        }
    }

    public void hapusBarang(int idBarang) {
        daftarBarang.removeIf(barang -> barang.getIdBarang() == idBarang);
    }
}