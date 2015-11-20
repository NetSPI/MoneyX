package com.nvisium.androidnv.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.savedrequest.NullRequestCache;

import com.nvisium.androidnv.api.service.UserService;

@Configuration
@EnableWebMvcSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	UserService userService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(userService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/register/**", "/forgot-password/**", "/dist/**",
						"/console/**", "/test**").permitAll().anyRequest().authenticated()
				.and().formLogin().loginPage("/login")
				.defaultSuccessUrl("/dashboard").permitAll().and().logout()
				.permitAll().and().requestCache()
				.requestCache(new NullRequestCache())
				.and().sessionManagement().sessionFixation().none()
				.and().csrf().disable();

		http.headers().frameOptions().disable();
	}

}