package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profil")
    public String profil(Model model, HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        return "ganti-password";
    }

    @PostMapping("/profil/ganti-password")
    public String gantiPassword(
            @RequestParam String passwordLama,
            @RequestParam String passwordBaru,
            @RequestParam String konfirmasiPassword,
            HttpSession session,
            Model model) {
        
        User user = (User) session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);

        // Verifikasi password lama
        User dbUser = userService.login(user.getUsername(), passwordLama);
        if (dbUser == null) {
            model.addAttribute("error", "Password lama tidak sesuai.");
            return "ganti-password";
        }

        // Verifikasi password baru
        if (!passwordBaru.equals(konfirmasiPassword)) {
            model.addAttribute("error", "Konfirmasi password tidak cocok.");
            return "ganti-password";
        }

        // Simpan password baru
        userService.resetPassword(user.getUsername(), passwordBaru);
        
        model.addAttribute("success", "Password berhasil diubah!");
        return "ganti-password";
    }
}
