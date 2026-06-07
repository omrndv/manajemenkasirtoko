package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.ItemTransaksi;
import com.kasir.manajemenkasir.model.LaporanPenjualan;
import com.kasir.manajemenkasir.model.Transaksi;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LaporanService {
    private final TransaksiService transaksiService;

    public LaporanService(TransaksiService transaksiService) {
        this.transaksiService = transaksiService;
    }

    public LaporanPenjualan generateLaporanSemua() {
        LaporanPenjualan laporan = new LaporanPenjualan("Semua Periode");

        for (Transaksi transaksi : transaksiService.getAllTransaksi()) {
            laporan.tambahTransaksi(transaksi);
        }

        laporan.generateLaporan();

        return laporan;
    }

    public LaporanPenjualan generateLaporanByTanggal(String tanggal) {
        LaporanPenjualan laporan = new LaporanPenjualan(tanggal);

        for (Transaksi transaksi : transaksiService.getAllTransaksi()) {
            if (transaksi.getTanggal().equals(tanggal)) {
                laporan.tambahTransaksi(transaksi);
            }
        }

        laporan.generateLaporan();

        return laporan;
    }

    public double hitungTotalPenjualan() {
        double total = 0;

        for (Transaksi transaksi : transaksiService.getAllTransaksi()) {
            total += transaksi.getTotalBayar();
        }

        return total;
    }

    public int hitungJumlahTransaksi() {
        return transaksiService.getAllTransaksi().size();
    }

    public String cariBarangTerlaris() {
        Map<String, Integer> jumlahTerjual = new HashMap<>();

        for (Transaksi transaksi : transaksiService.getAllTransaksi()) {
            List<ItemTransaksi> daftarItem = transaksi.getDaftarItem();

            for (ItemTransaksi item : daftarItem) {
                String namaBarang = item.getBarang().getNamaBarang();
                int qty = item.getQty();

                jumlahTerjual.put(namaBarang, jumlahTerjual.getOrDefault(namaBarang, 0) + qty);
            }
        }

        String barangTerlaris = "-";
        int jumlahTerbanyak = 0;

        for (Map.Entry<String, Integer> entry : jumlahTerjual.entrySet()) {
            if (entry.getValue() > jumlahTerbanyak) {
                barangTerlaris = entry.getKey();
                jumlahTerbanyak = entry.getValue();
            }
        }

        return barangTerlaris;
    }
}