package e63c.Lai.GA;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public MemberDetailsService memberDetailsService() {
		return new MemberDetailsService();
	}
	
	@Bean 
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(memberDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/categories/add", "/categories/edit/*", "/categories/save", "/categories/delete/*",
						"/acessories/add", "/acessories/edit/*", "/acessories/save", "acessories/delete/*",
						"/members", "/members/add", "/members/edit/*", "/members/save", "/members/delete/*").hasRole("ADMIN")
			.antMatchers("/").permitAll()
			.antMatchers("/bootstrap/*/*").permitAll()
			.antMatchers("/accessories/* ").permitAll()
			.antMatchers("/images/*").permitAll()
			.antMatchers("/uploads/*/*/*").permitAll()
			.antMatchers("/accessories/*").permitAll()
			.antMatchers("/Sign-Up").permitAll()
			.antMatchers("/Sign-Up/save").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin().loginPage("/login").permitAll()
			.and()
			.logout().logoutUrl("/logout").permitAll()
			.and()
			.exceptionHandling().accessDeniedPage("/403");
	}
}