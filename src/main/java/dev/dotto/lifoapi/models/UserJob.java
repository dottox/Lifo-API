package dev.dotto.lifoapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_jobs")
public class UserJob {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long userJobId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Job job;

    @NotNull
    @Column(name = "worked_time")
    private Integer workedTime; // in seconds

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Item> itemsGained;

    @NotNull
    @Column(name = "experience_gained")
    private Long experienceGained;

    @NotNull
    @Column(name = "gold_gained")
    private Long goldGained;



}
