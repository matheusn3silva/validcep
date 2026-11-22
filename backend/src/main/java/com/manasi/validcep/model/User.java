package com.manasi.validcep.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
    
    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setUser(null);
    }
}
