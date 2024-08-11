package com.artur.task_management_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Токен для активации аккаунта по почте.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "confirmation_token_table")
public class ConfirmationToken {
    @Id
    @SequenceGenerator(
            name = "confirmation_token_id",
            sequenceName = "confirmation_token_id",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "confirmation_token_id",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfirmationToken)) return false;
        ConfirmationToken confirmationToken = (ConfirmationToken) o;
        return Objects.equals(id, confirmationToken.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
