package dev.dotto.lifoapi.services;

import dev.dotto.lifoapi.exceptions.ApiException;
import dev.dotto.lifoapi.exceptions.ResourceNotFoundException;
import dev.dotto.lifoapi.models.Item;
import dev.dotto.lifoapi.models.Job;
import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.payloads.ItemDTO;
import dev.dotto.lifoapi.payloads.job.JobDTO;
import dev.dotto.lifoapi.payloads.job.JobResponse;
import dev.dotto.lifoapi.payloads.job.StartWorkResponse;
import dev.dotto.lifoapi.payloads.job.StopWorkResponse;
import dev.dotto.lifoapi.repositories.JobRepository;
import dev.dotto.lifoapi.repositories.UserRepository;
import dev.dotto.lifoapi.util.AuthUtil;
import dev.dotto.lifoapi.util.GameUtil;
import jakarta.transaction.Transactional;
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

    @Autowired
    private GameUtil gameUtil;

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
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isLast(),
                jobDTOs
        );
    }

    @Override
    public JobDTO getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        return modelMapper.map(job, JobDTO.class);
    }

    @Override
    @Transactional
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
    @Transactional
    public StopWorkResponse stopWorking() {
        User user = authUtil.loggedInUser();

        // If user is not working on any job, throw an exception
        if (user.getJob() == null) {
            throw new ApiException("No estás trabajando", HttpStatus.BAD_REQUEST);
        }

        // Get the user's job
        Job job = user.getJob();

        // Reset user's job.
        user.setJob(null);

        // Calculate the time worked and the total time for the job
        Date workingSince = user.getWorkingSince();
        Date workingUntil = user.getWorkingUntil();
        Date now = new Date();
        long workedTime = now.getTime() - workingSince.getTime(); // in milliseconds
        long totalTime = workingUntil.getTime() - workingSince.getTime(); // in milliseconds

        if (totalTime <= 0) { // Check for invalid working time, should not happen
            throw new ApiException("Tiempo de trabajo inválido", HttpStatus.BAD_REQUEST);
        }

        // Calculate the percentage of the job completed
        Float percentageCompleted = (float) workedTime / totalTime;
        Boolean completedJob = percentageCompleted >= 1.0F;

        // If the job is completed, cap the percentage at 100% and use the total time worked
        if (completedJob) {
            percentageCompleted = 1.0F;
            workedTime = totalTime;
        }

        // Calculate experience and gold gained based on the percentage completed and add them to the player
        long experienceGained = (long) (job.getExperience() * percentageCompleted);
        long goldGained = (long) (job.getGold() * percentageCompleted);
        gameUtil.addGoldToPlayer(user, goldGained);
        gameUtil.addExperienceToPlayer(user, experienceGained);

        // Calculate and add the items gained from working
        List<Item> gainedItems = gameUtil.addItemsFromWorking(user, workedTime / 1000);

        // Convert gainedItems to their DTO representation
        List<ItemDTO> gainedItemsDTO = gainedItems.stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .toList();

        Integer levelUps = gameUtil.calculateLevelUps(user);

        return new StopWorkResponse(
                percentageCompleted == 1.0F ? "Finalizaste de trabajar!" : "Has parado de trabajar",
                job.getJobName(),
                (int) workedTime / 1000, // Convert milliseconds to seconds
                experienceGained,
                goldGained,
                levelUps,
                completedJob,
                gainedItemsDTO
                );
    }
}
