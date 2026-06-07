package com.kasir.manajemenkasir.service;

import com.kasir.manajemenkasir.model.Admin;
import com.kasir.manajemenkasir.model.Kasir;
import com.kasir.manajemenkasir.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> daftarUser = new ArrayList<>();

    public UserService() {
        daftarUser.add(new Admin(1, "admin", "admin123", "Admin Toko", "08123456789"));
        daftarUser.add(new Kasir(2, "kasir", "kasir123", "Kasir Toko", "Pagi"));
    }

    public List<User> getAllUser() {
        return daftarUser;
    }

    public User login(String username, String password) {
        for (User user : daftarUser) {
            if (user.login(username, password)) {
                return user;
            }
        }

        return null;
    }

    public User getUserByUsername(String username) {
        for (User user : daftarUser) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRole().equalsIgnoreCase("Admin");
    }

    public boolean isKasir(User user) {
        return user != null && user.getRole().equalsIgnoreCase("Kasir");
    }
}