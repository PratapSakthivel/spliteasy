package com.spliteasy.backend.entity;

import com.spliteasy.backend.repository.UserRepository;
import net.jqwik.api.*;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for User entity
 */
@SpringBootTest
class UserPropertyTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeProperty
    void cleanDatabase() {
        if (userRepository != null) {
            userRepository.deleteAll();
        }
    }

    /**
     * Property 1: Registration Creates User Account
     * 
     * **Validates: Requirements 1.1**
     * 
     * For any valid email and password combination, when a user registers,
     * the Authentication_System creates a new user account that can be
     * retrieved from the User_Repository.
     */
    @Property
    void registrationCreatesUserAccount(
            @ForAll("validEmails") String email,
            @ForAll @StringLength(min = 8, max = 100) String password) {
        
        // Given: A valid email and password
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        
        // When: The user is saved to the repository
        User savedUser = userRepository.save(user);
        
        // Then: The user can be retrieved from the repository
        Optional<User> retrievedUser = userRepository.findByEmail(email);
        
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo(email);
        assertThat(retrievedUser.get().getPassword()).isEqualTo(password);
        assertThat(retrievedUser.get().getId()).isNotNull();
        assertThat(retrievedUser.get().getId()).isEqualTo(savedUser.getId());
    }

    /**
     * Provides valid email addresses for property testing
     */
    @Provide
    Arbitrary<String> validEmails() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(20);
        
        Arbitrary<String> domain = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(3)
                .ofMaxLength(15);
        
        Arbitrary<String> tld = Arbitraries.of("com", "org", "net", "edu", "io");
        
        return Combinators.combine(localPart, domain, tld)
                .as((local, dom, t) -> local + "@" + dom + "." + t);
    }
}
