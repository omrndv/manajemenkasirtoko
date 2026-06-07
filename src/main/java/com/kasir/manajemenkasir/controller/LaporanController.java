package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.LaporanPenjualan;
import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.LaporanService;
import com.kasir.manajemenkasir.service.TransaksiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LaporanController {
    private final LaporanService laporanService;
    private final TransaksiService transaksiService;

    public LaporanController(LaporanService laporanService, TransaksiService transaksiService) {
        this.laporanService = laporanService;
        this.transaksiService = transaksiService;
    }

    @GetMapping("/laporan")
    public String index(Model model, HttpSession session) {
        User user = (User) session.getAttribute("userLogin");

        if (user == null || !user.getRole().equalsIgnoreCase("Admin")) {
            return "redirect:/dashboard";
        }

        LaporanPenjualan laporan = laporanService.generateLaporanSemua();

        model.addAttribute("user", user);
        model.addAttribute("laporan", laporan);
        model.addAttribute("totalPenjualan", laporanService.hitungTotalPenjualan());
        model.addAttribute("jumlahTransaksi", laporanService.hitungJumlahTransaksi());
        model.addAttribute("barangTerlaris", laporanService.cariBarangTerlaris());
        model.addAttribute("daftarTransaksi", transaksiService.getAllTransaksi());

        return "laporan";
    }
}