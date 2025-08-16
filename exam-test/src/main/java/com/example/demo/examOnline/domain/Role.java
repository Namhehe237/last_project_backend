package com.example.demo.examOnline.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;
    
    @Size(min = 2, max = 50)
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
    
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users;
}