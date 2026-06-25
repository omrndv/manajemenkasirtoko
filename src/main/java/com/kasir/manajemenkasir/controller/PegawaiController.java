package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.service.UserService;
import com.kasir.manajemenkasir.service.AktivitasLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PegawaiController {
    private final UserService userService;
    private final AktivitasLogService aktivitasLogService;

    public PegawaiController(UserService userService, AktivitasLogService aktivitasLogService) {
        this.userService = userService;
        this.aktivitasLogService = aktivitasLogService;
    }

    private boolean bukanAdmin(HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        return user == null || !user.getRole().equalsIgnoreCase("Admin");
    }

    @GetMapping("/pegawai")
    public String index(Model model, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");

        model.addAttribute("user", user);
        model.addAttribute("daftarPegawai", userService.getPegawaiByToko(user.getToko()));

        return "pegawai";
    }

    @PostMapping("/pegawai/tambah")
    public String tambahPegawai(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String namaKasir,
            HttpSession session,
            Model model) {
        
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User user = (User) session.getAttribute("userLogin");

        User newKasir = userService.registerKasir(username, password, namaKasir, user.getToko());
        
        if (newKasir == null) {
            model.addAttribute("error", "Username sudah digunakan.");
            model.addAttribute("user", user);
            model.addAttribute("daftarPegawai", userService.getPegawaiByToko(user.getToko()));
            return "pegawai";
        }

        aktivitasLogService.log(user, "Menambahkan pegawai kasir baru: " + namaKasir + " (Username: " + username + ")");
        return "redirect:/pegawai";
    }

    @GetMapping("/pegawai/edit/{id}")
    public String formEditPegawai(@PathVariable int id, Model model, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User userLogin = (User) session.getAttribute("userLogin");
        User pegawai = userService.getUserById(id);

        if (pegawai == null || pegawai.getToko().getIdToko() != userLogin.getToko().getIdToko()) {
            return "redirect:/pegawai";
        }

        java.util.List<User> list = userService.getPegawaiByToko(userLogin.getToko());
        int displayId = 1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdUser() == id) {
                displayId = i + 1;
                break;
            }
        }

        model.addAttribute("user", userLogin);
        model.addAttribute("pegawai", pegawai);
        model.addAttribute("displayId", displayId);

        return "edit-pegawai";
    }

    @PostMapping("/pegawai/edit/{id}")
    public String updatePegawai(
            @PathVariable int id,
            @RequestParam String username,
            @RequestParam String namaKasir,
            @RequestParam(required = false) String password,
            HttpSession session) {
        
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User userLogin = (User) session.getAttribute("userLogin");
        User pegawai = userService.getUserById(id);

        if (pegawai != null && pegawai.getToko().getIdToko() == userLogin.getToko().getIdToko()) {
            userService.updateKasir(id, username, namaKasir, password);
            aktivitasLogService.log(userLogin, "Mengubah data pegawai kasir: " + namaKasir + " (Username: " + username + ")");
        }

        return "redirect:/pegawai";
    }

    @GetMapping("/pegawai/hapus/{id}")
    public String hapusPegawai(@PathVariable int id, HttpSession session) {
        if (bukanAdmin(session)) {
            return "redirect:/dashboard";
        }

        User pegawai = userService.getUserById(id);
        String namaPegawai = (pegawai != null) ? pegawai.getUsername() : "ID #" + id;

        userService.hapusPegawai(id);
        
        User user = (User) session.getAttribute("userLogin");
        aktivitasLogService.log(user, "Menghapus pegawai: " + namaPegawai);

        return "redirect:/pegawai";
    }
}
