package com.suraj.moneymanager.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.suraj.moneymanager.dto.AuthDTO;
import com.suraj.moneymanager.dto.ProfileDto;
import com.suraj.moneymanager.entity.ProfileEntity;
import com.suraj.moneymanager.repository.ProfileRepo;
import com.suraj.moneymanager.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepo profileRepo;
  private final EmailService emailService;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  private final JwtUtil jwtUtil;

  @Value("${app.activation.url}")
  private String activationUrl;

  public ProfileDto registerProfile(ProfileDto profileDto) {
    ProfileEntity newProfile = toEntity(profileDto);
    newProfile.setActivationToken(UUID.randomUUID().toString());

    newProfile = profileRepo.save(newProfile);
    // send activation link
    String activationLink = activationUrl + "/api/v1.0/activate?token=" + newProfile.getActivationToken();
    String subject = "Please activate your account";
    String body = "Click the link to activate your account: " + activationLink;
    try {
      emailService.sendEmail(newProfile.getEmail(), subject, body);
    } catch (Exception e) {
      System.err.println("Failed to send activation email: " + e.getMessage());
      // Optionally, you can add a field to the response to indicate email failure
    }

    System.out.println(newProfile);
    return toDto(newProfile);
  }

  public ProfileEntity toEntity(ProfileDto profileDto) {
    return ProfileEntity.builder()
        .id(profileDto.getId())
        .fullName(profileDto.getFullName())
        .email(profileDto.getEmail())
        .password(passwordEncoder.encode(profileDto.getPassword()))
        .profileImageUrl(profileDto.getProfileImageUrl())
        .createdAt(profileDto.getCreatedAt())
        .updatedAt(profileDto.getUpdatedAt())
        .build();
  }

  public ProfileDto toDto(ProfileEntity profileEntity) {
    return ProfileDto.builder()
        .id(profileEntity.getId())
        .fullName(profileEntity.getFullName())
        .email(profileEntity.getEmail())

        .profileImageUrl(profileEntity.getProfileImageUrl())
        .createdAt(profileEntity.getCreatedAt())
        .updatedAt(profileEntity.getUpdatedAt())
        .build();
  }

  public boolean activateUser(String token) {
    Optional<ProfileEntity> profileEntityOptional = profileRepo.findByActivationToken(token);
    if (!profileEntityOptional.isPresent()) {
      return false;
    }
    ProfileEntity profileEntity = profileEntityOptional.get();
    profileEntity.setActivationToken(null);
    profileEntity.setIsActive(true);
    profileRepo.save(profileEntity);
    return true;
  }

  public boolean isAccountActive(String email) {
    return profileRepo.findByEmail(email).map(ProfileEntity::getIsActive).orElse(false);
  }

  public ProfileEntity getCurrentProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    return profileRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
  }

  public ProfileDto getPublicProfile(String email) {
    ProfileEntity currentUser = null;
    if (email == null) {
      currentUser = getCurrentProfile();
    } else {
      currentUser = profileRepo.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
    }
    return ProfileDto.builder()
        .id(currentUser.getId())
        .fullName(currentUser.getFullName())
        .email(currentUser.getEmail())
        .profileImageUrl(currentUser.getProfileImageUrl())
        .createdAt(currentUser.getCreatedAt())
        .updatedAt(currentUser.getUpdatedAt())
        .build();

  }

  public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
    try {
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
      System.out.println("Trying login with: " + authDTO.getEmail() + " / " + authDTO.getPassword());

      String token = jwtUtil.generateToken(authDTO.getEmail());
      System.out.println("Generated token: " + token);
      return Map.of(
          "token", token,
          "user", getPublicProfile(authDTO.getEmail()));
    } catch (Exception e) {
      throw new RuntimeException("Invalid email or password");
    }

  }

}
