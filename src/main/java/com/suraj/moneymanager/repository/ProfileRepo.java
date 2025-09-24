package com.suraj.moneymanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suraj.moneymanager.entity.ProfileEntity;


public interface ProfileRepo extends JpaRepository<ProfileEntity, Long>{
  Optional<ProfileEntity> findByEmail(String email);
  Optional<ProfileEntity> findByActivationToken(String activationToken);
   Optional<ProfileEntity> findByResetPasswordToken(String token);
  
}
