package com.suraj.moneymanager.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    System.out.println(registeredProfile);
    return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
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

 
  
}
