package dev.dotto.lifoapi.models;

import dev.dotto.lifoapi.config.AppConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // --- User's info ---

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Email
    private String email;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserItem> items;

    private Boolean premium;


    // --- Account's info ---

    @NotNull
    @Column(name = "created_at")
    private Date createdAt;

//    @NotNull
//    @Column(name = "ip_created_from")
//    private String ipCreatedFrom;
//
//    @NotNull
//    @Column(name = "premium_until")
//    private Date premiumUntil;
//
//    @NotNull
//    @Column(name = "banned_until")
//    private Date bannedUntil;
//
//    @NotNull
//    @Column(name = "last_login")
//    private Date lastLogin;


    // --- Player's info ---

    @NotNull
    private Integer level;

    // User experience on the current level
    @NotNull
    @Column(name = "current_level_experience")
    private Long currentLevelExperience;

    // Total experience of the user
    @NotNull
    @Column(name = "total_experience")
    private Long totalExperience;

    // Experience needed to reach the next level
    @NotNull
    @Column(name = "next_level_experience")
    private Long nextLevelExperience;

    @NotNull
    private Long gold;


    // --- Work's info ---

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    private Job job;

    @NotNull
    @Column(name = "working_since")
    private Date workingSince;

    @NotNull
    @Column(name = "working_until")
    private Date workingUntil;


    // --- Constructors ---

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = new HashSet<>();
        this.items = new HashSet<>();
        this.level = 1;
        this.currentLevelExperience = 0L;
        this.totalExperience = 0L;
        this.nextLevelExperience = Long.valueOf(AppConstants.EXPERIENCE_BASE);
        this.gold = 0L;
        this.createdAt = new Date();
        this.job = null;
        this.workingSince = new Date(0);
        this.workingUntil = new Date(0);
        this.premium = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}
