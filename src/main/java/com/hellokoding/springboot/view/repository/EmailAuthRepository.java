package com.hellokoding.springboot.view.repository;

import com.hellokoding.springboot.view.model.EmailAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuthUser, Long> {
}
