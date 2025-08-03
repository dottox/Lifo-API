package dev.dotto.lifoapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


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

    @NotNull
    private Long experience;

    @NotNull
    private Long gold;


    // --- Work's info ---

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
        this.level = 1;
        this.experience = 0L;
        this.gold = 0L;
        this.createdAt = new Date();
        this.workingSince = new Date(0);
        this.workingUntil = new Date(0);
    }

}
