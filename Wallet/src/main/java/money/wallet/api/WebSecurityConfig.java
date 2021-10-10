package money.wallet.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Disables CSRF protection and provides basic HTTP auth.
 *
 * It simplifies the API consumption if it's not used in any web application (no CSRF risk);
 * if it is used in that way however, use e.g. double submit cookie pattern or a similar solution.
 */
@Configuration
@EnableWebSecurity
class WalletWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                    .authorizeRequests().anyRequest().authenticated()
                    .and().headers().frameOptions().sameOrigin()
                    .and().httpBasic();

    }
}
