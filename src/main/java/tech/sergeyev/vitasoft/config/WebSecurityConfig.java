package tech.sergeyev.vitasoft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.sergeyev.vitasoft.service.PersonService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .authorizeRequests()
                .antMatchers("/login").anonymous()
                .antMatchers("**/lk/user/**").hasRole("USER")
                .antMatchers("**/lk/admin/**").hasRole("ADMIN")
                .antMatchers("**/lk/operator/**").hasRole("OPERATOR")
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/lk")
                    .failureUrl("/login?error")
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .deleteCookies("JSESSIONID")
                    .permitAll();
    }

    @Autowired
    protected void configureAuthenticationManager(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            PersonService personService)
            throws Exception {
        authenticationManagerBuilder.userDetailsService(personService).passwordEncoder(passwordEncoder());
    }
}