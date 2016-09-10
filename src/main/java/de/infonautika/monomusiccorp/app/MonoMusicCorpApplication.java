package de.infonautika.monomusiccorp.app;


import de.infonautika.monomusiccorp.app.security.UserRole;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Map;

import static java.util.Collections.singletonMap;

@SpringBootApplication
public class MonoMusicCorpApplication extends JpaBaseConfiguration {

    protected MonoMusicCorpApplication(DataSource dataSource, JpaProperties properties, ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider) {
        super(dataSource, properties, jtaTransactionManagerProvider);
    }

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MonoMusicCorpApplication.class, args);
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter();
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        return singletonMap("eclipselink.weaving", "false");
    }

    @Configuration
    @EnableWebSecurity
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        private UserDetailsManager userDetailsManager;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            String realmName = "Realm";
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/**").authenticated().and()
                    .httpBasic().realmName(realmName).and()
                    .logout()
                        .addLogoutHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setHeader("WWW-Authenticate", "Basic realm=\"" + realmName + "\"");
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setHeader("Cache-Control", "no-cache");
                            response.getWriter().write("{\"redirect\": \"/\"}");
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true);
        }

        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
            userDetailsManager = auth.inMemoryAuthentication()
                    .withUser("admin").password("admin").roles("ADMIN", "USER").and()
                    .withUser("hans").password("hans").roles(UserRole.CUSTOMER.toString()).and()
                    .getUserDetailsService();
        }

        @Bean
        public UserDetailsManager getUserDetailsManager() {
            return userDetailsManager;
        }

    }
}
