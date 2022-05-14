package com.jay.shoppingmall.config;

import com.jay.shoppingmall.security.CustomUserDetailsService;
import com.jay.shoppingmall.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**","/js/**","/assets/**");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //TODO 로그인 후 /auth/null로 redirect 되는 버그 고치기.
                    .csrf().disable()
//                    .headers().frameOptions().disable()
//                .and()
                .rememberMe()
                    .key("123")
                    .rememberMeParameter("remember-me")
                    //1달
                    .tokenValiditySeconds(86400 * 30)
                    .userDetailsService(customUserDetailsService)
                    .authenticationSuccessHandler(loginSuccessHandler())
                .and()
                    .authorizeRequests()
                    .antMatchers("/h2-console/**", "/auth/login", "/auth/signup", "/", "/item/**", "/auth/forgot-password").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/auth/login")
                    .successHandler(loginSuccessHandler())
//                    .defaultSuccessUrl("/")
                .and()
                    .logout()
                    .logoutSuccessUrl("/auth/login")
                    .invalidateHttpSession(true);

    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService);
//    }
}
