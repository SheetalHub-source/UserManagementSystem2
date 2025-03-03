
package com.example.UserManagementSystem.Security;

import com.example.UserManagementSystem.JWT.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())  // CSRF disabled since JWT is stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/failed", "/loginpage").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("SUPERADMIN")
                        .requestMatchers("/product/**", "/api/category/**").hasAnyRole("SUPERADMIN", "ADMIN")
                        .requestMatchers("/api/product/**").hasAnyRole("SUPERADMIN", "ADMIN")
                        .requestMatchers("/", "/home", "/index").permitAll()
                        .requestMatchers("/verify").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/loginpage") // Custom login page
                        .defaultSuccessUrl("/home", true) // Redirect after successful login
                        .permitAll())
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/loginpage?logout")
                .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Ensure stateless session
                .authenticationProvider(authenticationProvider()) // Set authentication provider
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT Filter before UsernamePasswordAuthenticationFilter

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

/*
package com.example.UserManagementSystem.Security;

import com.example.UserManagementSystem.JWT.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/signup","/login","/loginpage"))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/failed","/loginpage").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("SUPERADMIN")
                        .requestMatchers("/product/**", "/api/category/**").hasAnyRole("SUPERADMIN", "ADMIN")
                        .requestMatchers("/api/product/**").hasAnyRole("SUPERADMIN", "ADMIN")
                        .requestMatchers("/verify").authenticated()

                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
                */
/*.formLogin(form-> form
                        .loginPage("/loginpage")
                                .loginProcessingUrl("/login")
                                .failureUrl("/loginpage")
                                .permitAll()
                        )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())*//*

               // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               // .authenticationProvider(authenticationProvider());
                //.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
*/
