package com.example.UserManagementSystem.Security;

import com.example.UserManagementSystem.entities.Users;
import com.example.UserManagementSystem.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private  final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      Users user = userRepository.findByEmail(email)
              .orElseThrow(()->new UsernameNotFoundException("USER NOT FOUND"));
      return User.builder()
              .username(user.getEmail())
              .password(user.getPassword())
              .authorities(user.getRole().toUpperCase())//"ROLE_" +
              .build();
    }
}
