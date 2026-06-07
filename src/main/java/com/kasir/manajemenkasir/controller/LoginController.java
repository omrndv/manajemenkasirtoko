package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.BarangService;
import com.kasir.manajemenkasir.service.LaporanService;
import com.kasir.manajemenkasir.service.TransaksiService;
import com.kasir.manajemenkasir.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    private final UserService userService;
    private final BarangService barangService;
    private final TransaksiService transaksiService;
    private final LaporanService laporanService;

    public LoginController(
            UserService userService,
            BarangService barangService,
            TransaksiService transaksiService,
            LaporanService laporanService
    ) {
        this.userService = userService;
        this.barangService = barangService;
        this.transaksiService = transaksiService;
        this.laporanService = laporanService;
    }

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String prosesLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        User user = userService.login(username, password);

        if (user == null) {
            model.addAttribute("error", "Username atau password salah.");
            return "login";
        }

        session.setAttribute("userLogin", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("userLogin");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        model.addAttribute("totalBarang", barangService.hitungTotalBarang());
        model.addAttribute("totalStok", barangService.hitungTotalStok());
        model.addAttribute("jumlahTransaksi", transaksiService.getAllTransaksi().size());
        model.addAttribute("totalPenjualan", laporanService.hitungTotalPenjualan());
        model.addAttribute("barangTerlaris", laporanService.cariBarangTerlaris());

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}