package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.Transaksi;
import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.BarangService;
import com.kasir.manajemenkasir.service.TransaksiService;
import com.kasir.manajemenkasir.service.AktivitasLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TransaksiController {
    private final TransaksiService transaksiService;
    private final BarangService barangService;
    private final AktivitasLogService aktivitasLogService;

    private Transaksi transaksiAktif;

    public TransaksiController(TransaksiService transaksiService, BarangService barangService, AktivitasLogService aktivitasLogService) {
        this.transaksiService = transaksiService;
        this.barangService = barangService;
        this.aktivitasLogService = aktivitasLogService;
    }

    private boolean belumLogin(HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        return user == null;
    }

    @GetMapping("/transaksi")
    public String index(Model model, HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        User user = (User) session.getAttribute("userLogin");

        if (transaksiAktif == null) {
            transaksiAktif = transaksiService.buatTransaksi();
            transaksiAktif.setToko(user.getToko());
        }

        model.addAttribute("user", user);
        model.addAttribute("transaksi", transaksiAktif);
        model.addAttribute("daftarBarang", barangService.getAllBarang(user.getToko()));

        return "transaksi";
    }

    @PostMapping("/transaksi/tambah-item")
    public String tambahItem(
            @RequestParam int idBarang,
            @RequestParam int qty,
            HttpSession session,
            Model model) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        User user = (User) session.getAttribute("userLogin");

        if (transaksiAktif == null) {
            transaksiAktif = transaksiService.buatTransaksi();
            transaksiAktif.setToko(user.getToko());
        }

        try {
            transaksiService.tambahItemKeTransaksi(transaksiAktif, idBarang, qty);
            return "redirect:/transaksi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("transaksi", transaksiAktif);
            model.addAttribute("daftarBarang", barangService.getAllBarang(user.getToko()));

            return "transaksi";
        }
    }

    @PostMapping("/transaksi/simpan")
    public String simpanTransaksi(
            @RequestParam double uangDibayar,
            Model model,
            HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        if (transaksiAktif == null) {
            return "redirect:/transaksi";
        }

        User user = (User) session.getAttribute("userLogin");

        try {
            double kembalian = transaksiService.hitungKembalian(transaksiAktif, uangDibayar);
            transaksiAktif.setUangDibayar(uangDibayar);
            Transaksi savedTransaksi = transaksiService.simpanTransaksi(transaksiAktif);

            aktivitasLogService.log(user, "Melakukan transaksi penjualan #" + savedTransaksi.getIdTransaksi() + " (Total: Rp " + savedTransaksi.getTotalBayar() + ")");

            model.addAttribute("user", user);
            model.addAttribute("transaksi", savedTransaksi);
            model.addAttribute("uangDibayar", uangDibayar);
            model.addAttribute("kembalian", kembalian);

            transaksiAktif = null;

            return "struk";
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("transaksi", transaksiAktif);
            model.addAttribute("daftarBarang", barangService.getAllBarang(user.getToko()));

            return "transaksi";
        }
    }

    @GetMapping("/riwayat-transaksi")
    public String riwayat(Model model, HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        User user = (User) session.getAttribute("userLogin");

        model.addAttribute("user", user);
        model.addAttribute("daftarTransaksi", transaksiService.getAllTransaksi(user.getToko()));

        return "riwayat-transaksi";
    }

    @GetMapping("/riwayat-transaksi/detail/{id}")
    public String detailRiwayat(@PathVariable int id, Model model, HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        Transaksi transaksi = transaksiService.getTransaksiById(id);

        if (transaksi == null) {
            return "redirect:/riwayat-transaksi";
        }

        User user = (User) session.getAttribute("userLogin");

        model.addAttribute("user", user);
        model.addAttribute("transaksi", transaksi);

        return "detail-transaksi";
    }

    @GetMapping("/transaksi/hapus-item/{idItem}")
    public String hapusItem(@PathVariable int idItem, HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        if (transaksiAktif == null) {
            return "redirect:/transaksi";
        }

        try {
            transaksiService.hapusItemDariTransaksi(transaksiAktif, idItem);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/transaksi";
    }

    @GetMapping("/transaksi/item/{idItem}/tambah")
    public String tambahQtyItem(@PathVariable int idItem, HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        if (transaksiAktif == null) {
            return "redirect:/transaksi";
        }

        try {
            transaksiService.tambahQtyItem(transaksiAktif, idItem);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/transaksi";
    }

    @GetMapping("/transaksi/item/{idItem}/kurang")
    public String kurangQtyItem(@PathVariable int idItem, HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        if (transaksiAktif == null) {
            return "redirect:/transaksi";
        }

        try {
            transaksiService.kurangQtyItem(transaksiAktif, idItem);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/transaksi";
    }

    @PostMapping("/transaksi/set-diskon")
    public String setDiskon(@RequestParam double diskon, HttpSession session) {
        if (belumLogin(session)) {
            return "redirect:/";
        }

        if (transaksiAktif != null) {
            transaksiAktif.setDiskon(diskon);
            transaksiAktif.hitungTotal();
        }

        return "redirect:/transaksi";
    }
}