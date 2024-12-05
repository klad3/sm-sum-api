package me.klad3.sumapispring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "institution_id", nullable = false, unique = true)
    private String institutionId;

    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    @Column(name = "api_secret", nullable = false)
    private String apiSecretHash;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void setApiSecret(String apiSecret) {
        this.apiSecretHash = new BCryptPasswordEncoder().encode(apiSecret);
    }

    public boolean verifyApiSecret(String apiSecret) {
        return new BCryptPasswordEncoder().matches(apiSecret, this.apiSecretHash);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
