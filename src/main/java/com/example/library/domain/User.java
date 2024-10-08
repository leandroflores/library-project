package com.example.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Name is required!")
    @NotEmpty(message = "Name can not be empty!")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Email is required!")
    @NotEmpty(message = "Email can not be empty!")
    @Email(message = "Email is invalid!")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Phone is required!")
    @NotEmpty(message = "Phone can not be empty!")
    @Column(nullable = false)
    private String phone;

    @NotNull(message = "Create at is required!")
    @PastOrPresent(message = "Date can not be a future date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

}
