package dev.dotto.lifoapi.services;

import dev.dotto.lifoapi.exceptions.ApiException;
import dev.dotto.lifoapi.exceptions.ResourceNotFoundException;
import dev.dotto.lifoapi.models.Job;
import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.payloads.job.JobDTO;
import dev.dotto.lifoapi.payloads.job.JobResponse;
import dev.dotto.lifoapi.payloads.job.StartWorkResponse;
import dev.dotto.lifoapi.payloads.job.StopWorkResponse;
import dev.dotto.lifoapi.repositories.JobRepository;
import dev.dotto.lifoapi.repositories.UserRepository;
import dev.dotto.lifoapi.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public JobResponse getAllJobs(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        if (jobRepository.count() == 0) {
            throw new ApiException("No jobs found", HttpStatus.NOT_FOUND);
        }

        // Create pageable object
        Pageable pageDetails = PageRequest.of(
                pageNumber,
                pageSize,
                sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );

        // Use the pageable object to fetch jobs
        Page<Job> jobPage = jobRepository.findAll(pageDetails);
        if (jobPage.isEmpty()) {
            throw new ApiException("No jobs found with that pagination params", HttpStatus.NOT_FOUND);
        }

        // Map the Job entities to JobDTOs
        List<JobDTO> jobDTOs = jobPage.getContent().stream()
                .map(j -> modelMapper.map(j, JobDTO.class))
                .toList();

        // Create JobResponse object and return it
        return new JobResponse(
                jobDTOs,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isLast()
        );
    }

    @Override
    public JobDTO getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        return modelMapper.map(job, JobDTO.class);
    }

    @Override
    public StartWorkResponse startWorking(Long jobId) {
        User user = authUtil.loggedInUser();
        // If user has a job and the workingUntil date is in the past (they finished the job):
        // - Finish the current job, gaining experience and gold
        // - Let the user start a new job
        if (user.getJob() != null) {
            if (user.getWorkingUntil().before(new Date())) {
                stopWorking();
            }
            else { // User is working but the job is not finished yet
                throw new ApiException("Ya estás trabajando actualmente", HttpStatus.BAD_REQUEST);
            }

        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        // Check if job is premium and user too
        if (job.getPremium() && !user.getPremium()) {
            throw new ApiException("Este trabajo es premium, necesitas una cuenta premium para trabajar en él", HttpStatus.BAD_REQUEST);
        }

        user.setJob(job);
        user.setWorkingSince(new Date());
        user.setWorkingUntil(Date.from(Instant.now().plus(Duration.ofSeconds(job.getWorkingTime()))));
        userRepository.save(user);
        return new StartWorkResponse(
                "Has comenzado a trabajar",
                job.getJobName(),
                job.getWorkingTime(),
                Date.from(Instant.now().plus(Duration.ofSeconds(job.getWorkingTime()))).getTime()
        );
    }

    @Override
    public StopWorkResponse stopWorking() {
        User user = authUtil.loggedInUser();

        // If user is not working on any job, throw an exception
        if (user.getJob() == null) {
            throw new ApiException("No estás trabajando", HttpStatus.BAD_REQUEST);
        }

        // Get the user's job
        Job job = user.getJob();

        // Calculate experience and gold gained
        // If user completed the job:
        // - Total experience and gold is added to user's account
        // Else, user completed only a part of the job:
        // - Experience and gold is calculated based on the time worked
        Date workingSince = user.getWorkingSince();
        Date workingUntil = user.getWorkingUntil();
        Date now = new Date();

        if (now.after(workingUntil)) { // User completed the job
            user.setExperience(user.getExperience() + job.getExperience());
            user.setGold(user.getGold() + job.getGold());
            user.setJob(null);
            userRepository.save(user);
            return new StopWorkResponse(
                    "Has finalizado tu trabajo!",
                    job.getJobName(),
                    job.getWorkingTime(),
                    job.getExperience(),
                    job.getGold(),
                    true
            );
        }
        else { // User did not complete the job, calculate partial experience and gold
            long workedTime = now.getTime() - workingSince.getTime();
            long totalTime = workingUntil.getTime() - workingSince.getTime();

            if (totalTime <= 0) {
                throw new ApiException("Tiempo de trabajo inválido", HttpStatus.BAD_REQUEST);
            }

            Float percentageCompleted = (float) workedTime / totalTime;
            long experienceGained = (long) (job.getExperience() * percentageCompleted);
            long goldGained = (long) (job.getGold() * percentageCompleted);

            user.setExperience(user.getExperience() + experienceGained);
            user.setGold(user.getGold() + goldGained);
            user.setJob(null);
            userRepository.save(user);
            return new StopWorkResponse(
                    "Has parado de trabajar",
                    job.getJobName(),
                    (int) workedTime / 1000, // Convert milliseconds to seconds
                    experienceGained,
                    goldGained,
                    false
            );
        }
    }


}
