package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.Barang;
import com.kasir.manajemenkasir.model.ItemTransaksi;
import com.kasir.manajemenkasir.model.Toko;
import com.kasir.manajemenkasir.model.Transaksi;
import com.kasir.manajemenkasir.repository.TransaksiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransaksiService {
    private final TransaksiRepository transaksiRepository;
    private final BarangService barangService;

    public TransaksiService(TransaksiRepository transaksiRepository, BarangService barangService) {
        this.transaksiRepository = transaksiRepository;
        this.barangService = barangService;
    }

    public List<Transaksi> getAllTransaksi(Toko toko) {
        return transaksiRepository.findByToko(toko);
    }

    public Transaksi getTransaksiById(int idTransaksi) {
        return transaksiRepository.findById(idTransaksi).orElse(null);
    }

    public Transaksi buatTransaksi() {
        int idBaru = (int) (transaksiRepository.count() + 1);
        String tanggalHariIni = LocalDate.now().toString();

        return new Transaksi(idBaru, tanggalHariIni);
    }

    public ItemTransaksi buatItemTransaksi(int idItem, int idBarang, int qty) {
        Barang barang = barangService.getBarangById(idBarang);

        if (barang == null) {
            throw new IllegalArgumentException("Barang tidak ditemukan.");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Jumlah barang harus lebih dari 0.");
        }

        if (barang.getStok() < qty) {
            throw new IllegalArgumentException("Stok barang tidak mencukupi.");
        }

        return new ItemTransaksi(idItem, barang, qty);
    }

    public void tambahItemKeTransaksi(Transaksi transaksi, int idBarang, int qty) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        Barang barang = barangService.getBarangById(idBarang);

        if (barang == null) {
            throw new IllegalArgumentException("Barang tidak ditemukan.");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Jumlah barang harus lebih dari 0.");
        }

        int qtySudahAda = 0;
        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getBarang().getIdBarang() == idBarang) {
                qtySudahAda = item.getQty();
                break;
            }
        }

        if (barang.getStok() < (qty + qtySudahAda)) {
            throw new IllegalArgumentException("Stok barang tidak mencukupi.");
        }

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getBarang().getIdBarang() == idBarang) {
                item.setQty(item.getQty() + qty);
                item.getBarang().setStok(barang.getStok() - item.getQty());
                transaksi.hitungTotal();
                return;
            }
        }

        int idItemBaru = transaksi.getDaftarItem().size() + 1;
        ItemTransaksi itemBaru = new ItemTransaksi(idItemBaru, barang, qty);
        barang.setStok(barang.getStok() - qty);

        transaksi.tambahItem(itemBaru);
    }

    public void tambahQtyItem(Transaksi transaksi, int idItem) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getIdItem() == idItem) {
                Barang dbBarang = barangService.getBarangById(item.getBarang().getIdBarang());
                if (dbBarang == null) {
                    throw new IllegalArgumentException("Barang tidak ditemukan.");
                }

                if (dbBarang.getStok() < (item.getQty() + 1)) {
                    throw new IllegalArgumentException("Stok barang tidak mencukupi.");
                }

                item.setQty(item.getQty() + 1);
                item.getBarang().setStok(dbBarang.getStok() - item.getQty());
                transaksi.hitungTotal();
                return;
            }
        }

        throw new IllegalArgumentException("Item transaksi tidak ditemukan.");
    }

    public void kurangQtyItem(Transaksi transaksi, int idItem) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        ItemTransaksi itemDikurangi = null;

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getIdItem() == idItem) {
                itemDikurangi = item;
                break;
            }
        }

        if (itemDikurangi == null) {
            throw new IllegalArgumentException("Item transaksi tidak ditemukan.");
        }

        Barang dbBarang = barangService.getBarangById(itemDikurangi.getBarang().getIdBarang());
        if (dbBarang != null) {
            if (itemDikurangi.getQty() <= 1) {
                transaksi.getDaftarItem().remove(itemDikurangi);
            } else {
                itemDikurangi.setQty(itemDikurangi.getQty() - 1);
                itemDikurangi.getBarang().setStok(dbBarang.getStok() - itemDikurangi.getQty());
            }
        }

        transaksi.hitungTotal();
    }

    @Transactional
    public Transaksi simpanTransaksi(Transaksi transaksi) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh kosong.");
        }

        if (transaksi.getDaftarItem().isEmpty()) {
            throw new IllegalArgumentException("Transaksi harus memiliki minimal satu item.");
        }

        transaksi.hitungTotal();

        // Update barang stock in database
        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            Barang dbBarang = barangService.getBarangById(item.getBarang().getIdBarang());
            if (dbBarang != null) {
                dbBarang.setStok(dbBarang.getStok() - item.getQty());
                barangService.updateBarang(dbBarang.getIdBarang(), dbBarang);
                item.setBarang(dbBarang);
            }
        }

        // Reset IDs for DB generation
        transaksi.setIdTransaksi(0);
        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            item.setIdItem(0);
            item.setTransaksi(transaksi);
        }

        Transaksi savedTransaksi = transaksiRepository.save(transaksi);
        savedTransaksi.simpanTransaksi();
        return savedTransaksi;
    }

    public double hitungKembalian(Transaksi transaksi, double uangDibayar) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        if (uangDibayar < transaksi.getTotalBayar()) {
            throw new IllegalArgumentException("Uang pembayaran kurang.");
        }

        return transaksi.hitungKembalian(uangDibayar);
    }

    public void hapusTransaksi(int idTransaksi) {
        transaksiRepository.deleteById(idTransaksi);
    }

    public void hapusItemDariTransaksi(Transaksi transaksi, int idItem) {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        ItemTransaksi itemDihapus = null;

        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getIdItem() == idItem) {
                itemDihapus = item;
                break;
            }
        }

        if (itemDihapus == null) {
            throw new IllegalArgumentException("Item transaksi tidak ditemukan.");
        }

        transaksi.getDaftarItem().remove(itemDihapus);
        transaksi.hitungTotal();
    }
}