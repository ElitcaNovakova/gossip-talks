package bg.codeacademy.spring.gossiptalks.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        // to enable access to h2-console
        .csrf()
        /**/.disable()
        .cors()
        /**/.disable()
        .headers()
        /**/.frameOptions().sameOrigin().and()
        // basic authentication
        .httpBasic()
        /**/.and()
        .logout()
        /**/.and()
        // security permissions
        .authorizeRequests()
        /**/.antMatchers("/h2-console/**").permitAll()
        /**/.antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
        /**/.antMatchers("/api/v1/**").authenticated();
  }

  @Bean
  PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
