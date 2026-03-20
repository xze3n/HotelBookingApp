package com.techtrek.booking.repository;

import com.techtrek.booking.entity.Role;
import com.techtrek.booking.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("repo@example.com")
                .password("pass123")
                .firstName("Test")
                .lastName("Repo")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(testUser);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        Optional<User> result = userRepository.findByEmail("repo@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("repo@example.com");
        assertThat(result.get().getFirstName()).isEqualTo("Test");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertThat(result).isNotPresent();
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        boolean exists = userRepository.existsByEmail("repo@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }
}
