package com.hellokoding.springboot.view.repository;

import com.hellokoding.springboot.view.model.NewUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<NewUser, Long> {

    @Modifying
    @Transactional
    @Query("SELECT u FROM NewUser u WHERE  u.email = :email")
    List<NewUser> getUserByEmail(String email);

//    @Modifying
//    @Transactional
//    @Query("SELECT u FROM NewUser u WHERE  u.password = :password")
//    List<NewUser> getUserByPassword(String password);
}
