
package shubhojeet.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_github_id",
                        columnNames = "github_id"
                )
        },
        indexes = {
                @Index(name = "idx_users_github_username", columnList = "github_username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "github_id", nullable = false, updatable = false)
    private Long githubId;

    @Column(name = "github_username", nullable = false, length = 100)
    private String githubUsername;

    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @Column(name = "avatar_url", length = 2048)
    private String avatarUrl;

    @Column(name = "token_scopes", length = 1000)
    private String tokenScopes;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "access_token", length = 2000)
    private String accessToken;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
