package com.kasir.manajemenkasir.repository;

import com.kasir.manajemenkasir.model.User;
import com.kasir.manajemenkasir.model.Toko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    List<User> findByToko(Toko toko);
}
