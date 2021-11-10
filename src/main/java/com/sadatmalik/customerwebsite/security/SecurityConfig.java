package com.sadatmalik.customerwebsite.security;

import com.sadatmalik.customerwebsite.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler(){
        return new LoginSuccessHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/webjars/", "/css/", "/login/", "/images/").permitAll()
                .antMatchers("/new-user", "/save-user").permitAll()
                .antMatchers("/user-dashboard").hasAuthority("USER_ROLE")
                .anyRequest().hasAuthority("ADMIN_ROLE")
                .and()
                .formLogin()
                .successHandler(loginSuccessHandler());
    }

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
}
