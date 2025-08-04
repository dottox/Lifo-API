package dev.dotto.lifoapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @NotBlank
    @Column(name = "job_name", length = 100)
    private String jobName;

    // In seconds
    @NotNull
    @Column(name = "working_time")
    private Integer workingTime;

    @NotNull
    private Long experience;

    @NotNull
    private Long gold;

    private Boolean premium;
}
