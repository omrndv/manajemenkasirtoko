package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.model.Admin;
import com.kasir.manajemenkasir.model.Kasir;
import com.kasir.manajemenkasir.model.Toko;
import com.kasir.manajemenkasir.repository.TokoRepository;
import com.kasir.manajemenkasir.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TokoRepository tokoRepository;

    public UserService(UserRepository userRepository, TokoRepository tokoRepository) {
        this.userRepository = userRepository;
        this.tokoRepository = tokoRepository;
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public List<User> getPegawaiByToko(Toko toko) {
        return userRepository.findByToko(toko);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.login(username, password))
                .orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRole().equalsIgnoreCase("Admin");
    }

    public boolean isKasir(User user) {
        return user != null && user.getRole().equalsIgnoreCase("Kasir");
    }

    public User registerAdmin(String username, String password, String namaAdmin, String kontak, String namaToko) {
        if (userRepository.findByUsername(username).isPresent()) {
            return null; // Username already exists
        }
        
        // Buat toko baru
        Toko toko = new Toko(namaToko, "-", kontak);
        toko = tokoRepository.save(toko);

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setNamaAdmin(namaAdmin);
        admin.setKontak(kontak);
        admin.setToko(toko); // Assign toko ke admin

        return userRepository.save(admin);
    }

    public User registerKasir(String username, String password, String namaKasir, Toko toko) {
        if (userRepository.findByUsername(username).isPresent()) {
            return null; // Username already exists
        }
        
        Kasir kasir = new Kasir();
        kasir.setUsername(username);
        kasir.setPassword(password);
        kasir.setNamaKasir(namaKasir);
        kasir.setShift("Pagi"); // Default shift
        kasir.setToko(toko);

        return userRepository.save(kasir);
    }

    public void hapusPegawai(int idUser) {
        userRepository.deleteById(idUser);
    }

    public User getUserById(int idUser) {
        return userRepository.findById(idUser).orElse(null);
    }

    public void updateKasir(int idUser, String username, String namaKasir, String password) {
        User user = getUserById(idUser);
        if (user instanceof Kasir) {
            Kasir kasir = (Kasir) user;
            kasir.setUsername(username);
            kasir.setNamaKasir(namaKasir);
            if (password != null && !password.trim().isEmpty()) {
                kasir.setPassword(password);
            }
            userRepository.save(kasir);
        }
    }

    public boolean resetPassword(String username, String kontakToko, String newPassword) {
        User user = getUserByUsername(username);
        if (user != null && user.getToko() != null && user.getToko().getKontak() != null && user.getToko().getKontak().trim().equals(kontakToko.trim())) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String username, String newPassword) {
        User user = getUserByUsername(username);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User getOrCreateSuperAdmin(String username, String password) {
        Admin sa = new Admin();
        sa.setUsername(username);
        sa.setPassword(password);
        sa.setRole("SuperAdmin");
        sa.setNamaAdmin("Owner / Superadmin");
        sa.setIdUser(-1);
        return sa;
    }
}