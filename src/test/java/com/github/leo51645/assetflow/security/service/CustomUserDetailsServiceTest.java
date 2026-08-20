package com.github.leo51645.assetflow.security.service;

import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        // Arrange
        String email = "email123";
        Optional<UserEntity> expectedUser = Optional.of(new UserEntity());
        when(userRepository.findByEmail(email)).thenReturn(expectedUser);

        // Action
        UserDetails actualUser = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertEquals(expectedUser.get(), actualUser);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldNotLoadUserByUsernameDueToEmailNotFound() {
        // Arrange
        String email = "email123";
        when(userRepository.findByEmail(email)).thenThrow(new UsernameNotFoundException("User with email '" + email + "' not found"));

        // Action + Assert
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(email));
        verify(userRepository).findByEmail(email);
    }

}