package dev.dotto.lifoapi.security;

import dev.dotto.lifoapi.models.GameRole;
import dev.dotto.lifoapi.models.Role;
import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.repositories.UserRepository;
import dev.dotto.lifoapi.repositories.RoleRepository;
import dev.dotto.lifoapi.security.jwt.AuthEntryPointJwt;
import dev.dotto.lifoapi.security.jwt.AuthTokenFilter;
import dev.dotto.lifoapi.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity()
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/user").authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()
        ));


        return http.build();
    }

    // This bean is used to ignore security for Swagger UI and other static resources
    // so that they can be accessed without authentication.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    // Allows to create the initial admin user
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Initialize roles
            Role userRole = roleRepository.findByRoleName(GameRole.USER)
                    .orElseGet(() -> roleRepository.save(new Role(GameRole.USER)));
            Role adminRole = roleRepository.findByRoleName(GameRole.ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(GameRole.ADMIN)));

            // Create the set of roles for the admin user
            Set<Role> adminRoles = Set.of(userRole, adminRole);

            // Get admin password from environment variable
            String adminPassword = System.getenv("ADMIN_PASSWORD");
            if (adminPassword == null || adminPassword.isEmpty()) {
                throw new IllegalStateException("ADMIN_PASSWORD environment variable is not set");
            }

            // Create admin user
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", passwordEncoder.encode(adminPassword), "admin@lifo.com");
                userRepository.save(admin);
            }

            // Set roles for admin user
            userRepository.findByUsername("admin").ifPresent(adm -> {
                adm.setRoles(adminRoles);
                userRepository.save(adm);
            });
        };
    }
}
