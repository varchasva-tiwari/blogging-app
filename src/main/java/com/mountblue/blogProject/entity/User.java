package com.mountblue.blogProject.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name="users")
public class User {
    private static final String NAME_EMPTY = "Name cannot not be empty";
    private static final String EMAIL_EMPTY = "Email cannot not be empty";
    private static final String ROLE_EMPTY = "Role cannot not be empty";
    private static final String EMAIL_WRONG_FORMAT = "Enter email in proper format";
    private static final String PASSWORD_EMPTY = "Password cannot be empty";
    private static final String NAME_EXCEEDED = "Name  cannot exceed 30 characters";
    private static final String EMAIL_EXCEEDED = "Email cannot exceed 40 characters";
    private static final String PASSWORD_EXCEEDED = "Password cannot exceed 15 characters";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = NAME_EMPTY)
    @Size(max = 15, message = NAME_EXCEEDED)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = EMAIL_EMPTY)
    @Email(message = EMAIL_WRONG_FORMAT)
    @Size(max = 25, message = EMAIL_EXCEEDED)
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = PASSWORD_EMPTY)
    @Size(max = 15, message = PASSWORD_EXCEEDED)
    private String password;

    @Column(name = "role", nullable = false)
    @NotBlank(message = ROLE_EMPTY)
    private String role;

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}