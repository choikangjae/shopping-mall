package com.jay.shoppingmall.config;

import com.jay.shoppingmall.security.CustomUserDetailsService;
import com.jay.shoppingmall.security.LoginFailureHandler;
import com.jay.shoppingmall.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring().antMatchers("/css/**","/js/**","/assets/**");
//    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
//                    .requestCache().requestCache(requestCache())
//                .and()
//                .rememberMe()
//                .rememberMeParameter("remember-me")
                //토큰 유효기간 1달
//                .tokenValiditySeconds(86400 * 30)
//                    .alwaysRemember(true)
//                .userDetailsService(customUserDetailsService)
//                .and()
                .authorizeRequests()
                .antMatchers("/auth/login**", "/seller/null", "/admin/null", "/auth/null", "/null", "/css/**", "/js/**", "/assets/**", "/auth/signup-done", "/auth/seller-signup", "/auth/signup", "/", "/item/**", "/auth/forgot-password").permitAll()
                .antMatchers("/me/**", "/seller/start", "/seller/agree").hasRole("USER")
                .antMatchers("/seller/**").hasRole("SELLER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler())
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/auth/login")
                .permitAll()
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
//                .failureUrl("/auth/login?error")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/auth/login")
                //로그아웃시 세션을 통해 발급한 모든 쿠키 삭제.
                .deleteCookies("JSESSIONID", "remember-me")
                .invalidateHttpSession(true);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler("username", "/");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //권한별 계층 구조 설정
    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_SELLER > ROLE_USER");
        return roleHierarchy;
    }

    //권한 계층 구조 등록
    @Bean
    public SecurityExpressionHandler<FilterInvocation> expressionHandler() {
        DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        webSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return webSecurityExpressionHandler;
    }
//    @Bean
//    public AccessDecisionVoter<? extends Object> roleVoter() {
//        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
//        return roleHierarchyVoter;
//    }

//    AccessDecisionManager accessDecisionManager() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_SELLER > ROLE_USER");
//
//        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
//        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy);
//
//        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
//        webExpressionVoter.setExpressionHandler(defaultWebSecurityExpressionHandler);
//
//        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList();
//
//        AccessDecisionManager accessDecisionManager = new AffirmativeBased(voters);
//        return accessDecisionManager;
//    }


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService);
//    }
}
