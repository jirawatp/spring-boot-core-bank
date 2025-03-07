package com.pattanayutanachot.jirawat.core.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Citizen ID is required")
    private String citizenId;

    @Column(nullable = false)
    @NotBlank(message = "Thai name is required")
    private String thaiName;

    @Column(nullable = false)
    @NotBlank(message = "English name is required")
    private String englishName;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String password;

    @Column(nullable = false, length = 6)
    @NotBlank(message = "PIN is required")
    private String pin;

    @Column(nullable = false, precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}