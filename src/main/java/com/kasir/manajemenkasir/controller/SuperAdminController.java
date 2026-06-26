package com.kasir.manajemenkasir.controller;

import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.model.Admin;
import com.kasir.manajemenkasir.model.Kasir;
import com.kasir.manajemenkasir.model.Toko;
import com.kasir.manajemenkasir.model.AktivitasLog;
import com.kasir.manajemenkasir.repository.TokoRepository;
import com.kasir.manajemenkasir.repository.UserRepository;
import com.kasir.manajemenkasir.service.UserService;
import com.kasir.manajemenkasir.service.AktivitasLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SuperAdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TokoRepository tokoRepository;
    private final AktivitasLogService aktivitasLogService;

    public SuperAdminController(
            UserService userService,
            UserRepository userRepository,
            TokoRepository tokoRepository,
            AktivitasLogService aktivitasLogService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokoRepository = tokoRepository;
        this.aktivitasLogService = aktivitasLogService;
    }

    private boolean bukanSuperAdmin(HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        return user == null || !user.getRole().equalsIgnoreCase("SuperAdmin");
    }

    @GetMapping("/superadmin/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("userLogin");
        
        long totalToko = tokoRepository.count();
        long totalAkun = userRepository.count();
        List<AktivitasLog> logs = aktivitasLogService.getAllLogs();
        long totalLogs = logs.size();

        model.addAttribute("user", user);
        model.addAttribute("totalToko", totalToko);
        model.addAttribute("totalAkun", totalAkun);
        model.addAttribute("totalLogs", totalLogs);
        model.addAttribute("logs", logs);

        return "superadmin-dashboard";
    }

    @GetMapping("/superadmin/accounts")
    public String accounts(Model model, HttpSession session) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("userLogin");
        List<User> accounts = userService.getAllUser();
        List<Toko> daftarToko = tokoRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("daftarToko", daftarToko);

        return "superadmin-accounts";
    }

    @GetMapping("/superadmin/accounts/toggle-status/{id}")
    public String toggleAccountStatus(@PathVariable int id, HttpSession session) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }

        User superAdmin = (User) session.getAttribute("userLogin");
        User targetUser = userService.getUserById(id);

        if (targetUser != null && !targetUser.getRole().equalsIgnoreCase("SuperAdmin")) {
            targetUser.setAktif(!targetUser.isAktif());
            userRepository.save(targetUser);
            aktivitasLogService.log(superAdmin, "Superadmin " + (targetUser.isAktif() ? "mengaktifkan" : "menonaktifkan") + " akun: " + targetUser.getUsername());
        }

        return "redirect:/superadmin/accounts";
    }

    @GetMapping("/superadmin/toko/toggle-status/{id}")
    public String toggleStoreStatus(@PathVariable int id, HttpSession session) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }

        User superAdmin = (User) session.getAttribute("userLogin");
        Toko toko = tokoRepository.findById(id).orElse(null);

        if (toko != null) {
            toko.setAktif(!toko.isAktif());
            tokoRepository.save(toko);
            aktivitasLogService.log(superAdmin, "Superadmin " + (toko.isAktif() ? "mengaktifkan" : "menonaktifkan") + " toko: " + toko.getNamaToko());
        }

        return "redirect:/superadmin/accounts";
    }

    @GetMapping("/superadmin/accounts/tambah")
    public String formTambah(Model model, HttpSession session) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("userLogin");
        model.addAttribute("user", user);
        model.addAttribute("daftarToko", tokoRepository.findAll());
        return "superadmin-tambah-akun";
    }

    @PostMapping("/superadmin/accounts/tambah")
    public String tambahAkun(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam int idToko,
            @RequestParam String namaLengkap,
            HttpSession session,
            Model model
    ) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }
        User superAdmin = (User) session.getAttribute("userLogin");

        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username sudah digunakan.");
            model.addAttribute("user", superAdmin);
            model.addAttribute("daftarToko", tokoRepository.findAll());
            return "superadmin-tambah-akun";
        }

        Toko toko = tokoRepository.findById(idToko).orElse(null);
        if (toko == null) {
            model.addAttribute("error", "Toko tidak ditemukan.");
            model.addAttribute("user", superAdmin);
            model.addAttribute("daftarToko", tokoRepository.findAll());
            return "superadmin-tambah-akun";
        }

        User newUser;
        if ("Admin".equalsIgnoreCase(role)) {
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(password);
            admin.setNamaAdmin(namaLengkap);
            admin.setKontak("-");
            admin.setToko(toko);
            newUser = userRepository.save(admin);
        } else {
            Kasir kasir = new Kasir();
            kasir.setUsername(username);
            kasir.setPassword(password);
            kasir.setNamaKasir(namaLengkap);
            kasir.setShift("Pagi");
            kasir.setToko(toko);
            newUser = userRepository.save(kasir);
        }

        aktivitasLogService.log(superAdmin, "Superadmin membuat akun baru: " + username + " (Role: " + role + ", Toko: " + toko.getNamaToko() + ")");
        return "redirect:/superadmin/accounts";
    }

    @GetMapping("/superadmin/accounts/edit/{id}")
    public String formEdit(@PathVariable int id, Model model, HttpSession session) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("userLogin");
        User targetUser = userService.getUserById(id);
        if (targetUser == null || targetUser.getRole().equalsIgnoreCase("SuperAdmin")) {
            return "redirect:/superadmin/accounts";
        }

        String namaLengkap = "";
        if (targetUser instanceof Admin) {
            namaLengkap = ((Admin) targetUser).getNamaAdmin();
        } else if (targetUser instanceof Kasir) {
            namaLengkap = ((Kasir) targetUser).getNamaKasir();
        }

        model.addAttribute("user", user);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("namaLengkap", namaLengkap);
        model.addAttribute("daftarToko", tokoRepository.findAll());
        return "superadmin-edit-akun";
    }

    @PostMapping("/superadmin/accounts/edit/{id}")
    public String editAkun(
            @PathVariable int id,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam String role,
            @RequestParam int idToko,
            @RequestParam String namaLengkap,
            HttpSession session,
            Model model
    ) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }
        User superAdmin = (User) session.getAttribute("userLogin");
        User targetUser = userService.getUserById(id);

        if (targetUser == null || targetUser.getRole().equalsIgnoreCase("SuperAdmin")) {
            return "redirect:/superadmin/accounts";
        }

        // Check username availability if changed
        if (!targetUser.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username sudah digunakan oleh akun lain.");
            model.addAttribute("user", superAdmin);
            model.addAttribute("targetUser", targetUser);
            model.addAttribute("namaLengkap", namaLengkap);
            model.addAttribute("daftarToko", tokoRepository.findAll());
            return "superadmin-edit-akun";
        }

        Toko toko = tokoRepository.findById(idToko).orElse(null);
        if (toko == null) {
            model.addAttribute("error", "Toko tidak ditemukan.");
            model.addAttribute("user", superAdmin);
            model.addAttribute("targetUser", targetUser);
            model.addAttribute("namaLengkap", namaLengkap);
            model.addAttribute("daftarToko", tokoRepository.findAll());
            return "superadmin-edit-akun";
        }

        String oldUsername = targetUser.getUsername();
        String oldRole = targetUser.getRole();

        // If role changed, recreate user object
        if (!targetUser.getRole().equalsIgnoreCase(role)) {
            String finalPassword = (password != null && !password.trim().isEmpty()) ? password : targetUser.getPassword();
            userRepository.delete(targetUser);
            
            User newUser;
            if ("Admin".equalsIgnoreCase(role)) {
                Admin admin = new Admin();
                admin.setUsername(username);
                admin.setPassword(finalPassword);
                admin.setNamaAdmin(namaLengkap);
                admin.setKontak("-");
                admin.setToko(toko);
                newUser = userRepository.save(admin);
            } else {
                Kasir kasir = new Kasir();
                kasir.setUsername(username);
                kasir.setPassword(finalPassword);
                kasir.setNamaKasir(namaLengkap);
                kasir.setShift("Pagi");
                kasir.setToko(toko);
                newUser = userRepository.save(kasir);
            }
            
            aktivitasLogService.log(superAdmin, "Superadmin mengubah akun: " + oldUsername + " -> " + username + " (Role: " + oldRole + " -> " + role + ", Toko: " + toko.getNamaToko() + ")");
        } else {
            // Update same object
            targetUser.setUsername(username);
            if (password != null && !password.trim().isEmpty()) {
                targetUser.setPassword(password);
            }
            targetUser.setToko(toko);

            if (targetUser instanceof Admin) {
                ((Admin) targetUser).setNamaAdmin(namaLengkap);
            } else if (targetUser instanceof Kasir) {
                ((Kasir) targetUser).setNamaKasir(namaLengkap);
            }

            userRepository.save(targetUser);
            aktivitasLogService.log(superAdmin, "Superadmin memperbarui data akun: " + username + " (Role: " + role + ", Toko: " + toko.getNamaToko() + ")");
        }

        return "redirect:/superadmin/accounts";
    }

    @GetMapping("/superadmin/accounts/delete/{id}")
    public String deleteAccount(@PathVariable int id, HttpSession session) {
        if (bukanSuperAdmin(session)) {
            return "redirect:/login";
        }

        User superAdmin = (User) session.getAttribute("userLogin");
        User targetUser = userService.getUserById(id);

        if (targetUser != null) {
            // Jangan biarkan superadmin menghapus dirinya sendiri dari web interface
            if (targetUser.getUsername().equals(superAdmin.getUsername())) {
                return "redirect:/superadmin/accounts";
            }

            String targetUsername = targetUser.getUsername();
            String targetRole = targetUser.getRole();
            String namaToko = (targetUser.getToko() != null) ? targetUser.getToko().getNamaToko() : "Sistem";

            userService.hapusPegawai(id);
            
            aktivitasLogService.log(superAdmin, "Superadmin menghapus akun: " + targetUsername + " (Role: " + targetRole + ", Toko: " + namaToko + ")");
        }

        return "redirect:/superadmin/accounts";
    }
}
