//package com.knowledgeVista.User.SecurityConfiguration;
//

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//	 @Autowired
//	    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//	        // Configure authentication manager to use your custom user details service
//	        auth.userDetailsService(UserDetailsService()).passwordEncoder(passwordEncoder());
//	    }
//	 @Override
//	    protected void configure(HttpSecurity http) throws Exception {
//	        http
//	            .authorizeRequests()
//	                .antMatchers("/login").permitAll() // Allow access to login endpoint without authentication
//	                .anyRequest().authenticated()
//	                .and()
//	            .formLogin()
//	                .loginPage("/login") // Specify custom login page URL
//	                .defaultSuccessUrl("/home") // Redirect to home page after successful login
//	                .permitAll()
//	                .and()
//	            .logout()
//	                .permitAll();
//	    }

//public class SecurityConfig {
//

//}
