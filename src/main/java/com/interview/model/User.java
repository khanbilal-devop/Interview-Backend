package com.interview.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = false)
    private String name;

    @Column(name = "email", columnDefinition = "VARCHAR(50)", nullable = false)
    private String email;


    @Column(name = "password", columnDefinition = "VARCHAR(100)", nullable = false)
    private String password;

    @Column(name = "confirm_password", columnDefinition = "VARCHAR(100)", nullable = false)
    private String confirmPassword;

    @Column(name = "active", columnDefinition = "TINYINT", nullable = false)
    private boolean active = true;

    @Transient
    @JsonIgnore
    private String orderBy;

    @Transient
    @JsonIgnore
    private boolean ascending;

    @Transient
    @JsonIgnore
    private Long rolesId;
}
