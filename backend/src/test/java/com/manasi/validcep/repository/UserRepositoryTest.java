package com.manasi.validcep.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.manasi.validcep.model.Address;
import com.manasi.validcep.model.User;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev")
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    void shouldPersistUserWithAddresses() {
        // Given
        User user = buildUser("ana@email.com");
        user.addAddress(buildAddress("01001000")); 
        user.addAddress(buildAddress("20040020")); 

        // When
        User saved = userRepository.save(user);
        em.flush();
        em.clear();

        // Then
        User found = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals(2, found.getAddresses().size());

        assertTrue(userRepository.existsByEmail(saved.getEmail()));
        assertFalse(userRepository.existsByEmail("novo@email.com"));
    }

    @Test
    void shouldRemoveCascadeUserWithAddresses() {
        // Given
        User user = buildUser("ana@email.com");
        user.addAddress(buildAddress("01001000"));

        User saved = userRepository.save(user);
        em.flush();
        em.clear();

        Long addressId = saved.getAddresses().get(0).getId();

        // When
        userRepository.deleteById(saved.getId());
        em.flush();
        em.clear();

        // Then
        Address foundAddress = em.find(Address.class, addressId);
        assertNull(foundAddress);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        // Given
        User user = buildUser("ana@email.com");
        User user2 = buildUser("ana@email.com");

        userRepository.save(user);
        em.flush();
        em.clear();

        // When and Then

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2); 
        });
    }

    private User buildUser(String email) {
        User user = new User();
        user.setName("Ana Silva");
        user.setEmail(email);
        user.setPhoneNumber("11999999999");
        return user;
    }

    private Address buildAddress(String zipCode) {
        Address address = new Address();
        address.setCity("São Paulo");
        address.setNeighborhood("Jardim dos Ipes");
        address.setState("SP");
        address.setStreet("Palmeiras das Bermudas");
        address.setStreetNumber("284");
        address.setZipCode(zipCode);
        return address;
    }
}
