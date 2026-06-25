package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.ItemTransaksi;
import com.kasir.manajemenkasir.model.LaporanPenjualan;
import com.kasir.manajemenkasir.model.Toko;
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

    public LaporanPenjualan generateLaporanSemua(Toko toko) {
        LaporanPenjualan laporan = new LaporanPenjualan("Semua Periode");

        for (Transaksi transaksi : transaksiService.getAllTransaksi(toko)) {
            laporan.tambahTransaksi(transaksi);
        }

        laporan.generateLaporan();

        return laporan;
    }

    public LaporanPenjualan generateLaporanByTanggal(String tanggal, Toko toko) {
        LaporanPenjualan laporan = new LaporanPenjualan(tanggal);

        for (Transaksi transaksi : transaksiService.getAllTransaksi(toko)) {
            if (transaksi.getTanggal().equals(tanggal)) {
                laporan.tambahTransaksi(transaksi);
            }
        }

        laporan.generateLaporan();

        return laporan;
    }

    public double hitungTotalPenjualan(Toko toko) {
        double total = 0;

        for (Transaksi transaksi : transaksiService.getAllTransaksi(toko)) {
            total += transaksi.getTotalBayar();
        }

        return total;
    }

    public int hitungJumlahTransaksi(Toko toko) {
        return transaksiService.getAllTransaksi(toko).size();
    }

    public String cariBarangTerlaris(Toko toko) {
        Map<String, Integer> jumlahTerjual = new HashMap<>();

        for (Transaksi transaksi : transaksiService.getAllTransaksi(toko)) {
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

    public double hitungLaba(Toko toko) {
        double totalLaba = 0;

        for (Transaksi transaksi : transaksiService.getAllTransaksi(toko)) {
            for (ItemTransaksi item : transaksi.getDaftarItem()) {
                double hargaJual = item.getBarang().getHarga();
                double hargaModal = item.getBarang().getHargaModal();
                int qty = item.getQty();
                
                totalLaba += (hargaJual - hargaModal) * qty;
            }
            // Diskon mengurangi laba
            totalLaba -= transaksi.getDiskon();
        }

        return totalLaba;
    }

    public double hitungTotalDiskon(Toko toko) {
        double total = 0;
        for (Transaksi t : transaksiService.getAllTransaksi(toko)) {
            total += t.getDiskon();
        }
        return total;
    }

    public double hitungTotalPajak(Toko toko) {
        double total = 0;
        for (Transaksi t : transaksiService.getAllTransaksi(toko)) {
            total += t.getPajakPpn();
        }
        return total;
    }

    public double hitungRataRataTransaksi(Toko toko) {
        List<Transaksi> list = transaksiService.getAllTransaksi(toko);
        if (list.isEmpty()) return 0;
        double total = 0;
        for (Transaksi t : list) {
            total += t.getTotalBayar();
        }
        return total / list.size();
    }

    public Map<String, Integer> getPenjualanBarangList(Toko toko) {
        Map<String, Integer> map = new HashMap<>();
        for (Transaksi t : transaksiService.getAllTransaksi(toko)) {
            for (ItemTransaksi item : t.getDaftarItem()) {
                String name = item.getBarang().getNamaBarang();
                map.put(name, map.getOrDefault(name, 0) + item.getQty());
            }
        }
        return map;
    }

    public Map<String, Double> getPenjualanPerKategori(Toko toko) {
        Map<String, Double> map = new HashMap<>();
        for (Transaksi t : transaksiService.getAllTransaksi(toko)) {
            for (ItemTransaksi item : t.getDaftarItem()) {
                String cat = item.getBarang().getKategori();
                if (cat == null || cat.trim().isEmpty()) {
                    cat = "Umum";
                }
                double subtotal = item.getSubtotal();
                map.put(cat, map.getOrDefault(cat, 0.0) + subtotal);
            }
        }
        return map;
    }
}