package com.suraj.moneymanager.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suraj.moneymanager.dto.AuthDTO;
import com.suraj.moneymanager.dto.ProfileDto;
import com.suraj.moneymanager.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @PostMapping("/register")
  public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto) {
    ProfileDto registeredProfile = profileService.registerProfile(profileDto);
    if(registeredProfile==null)
    {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    return ResponseEntity.ok(registeredProfile);
    // System.out.println(registeredProfile);
    
  }

  
  @GetMapping("/activate")
  public ResponseEntity<String> activateProfile(@RequestParam String token) {
    boolean isActivated = profileService.activateUser(token);
    if (isActivated) {
      return ResponseEntity.ok("Profile activated successfully");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation token");
    }
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO) {

    try {
      if(!profileService.isAccountActive(authDTO.getEmail()))
      {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Account is not active"));
      }
     Map<String,Object> response=profileService.authenticateAndGenerateToken(authDTO);

        return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message",e.getMessage()));
    }
    
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
      String email = request.get("email");
      profileService.sendForgotPasswordEmail(email);
      return ResponseEntity.ok("Password reset link sent to your email if it exists.");
  }

  // Reset password
  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
      String token = request.get("token");
      String newPassword = request.get("password");
      profileService.resetPassword(token, newPassword);
      return ResponseEntity.ok("Password has been reset successfully.");
  }

   @GetMapping("/profile")
  public ResponseEntity<ProfileDto> getProfile() {
    ProfileDto profile = profileService.getPublicProfile(null);
    return ResponseEntity.ok(profile);
  }

   @PutMapping("/update-profile-image")
  public ResponseEntity<ProfileDto> updateProfileImage(@RequestBody Map<String, String> request) {
      String imageUrl = request.get("profileImageUrl");
      ProfileDto updatedProfile = profileService.updateProfileImage(imageUrl);
      return ResponseEntity.ok(updatedProfile);
  }


 
  
}
