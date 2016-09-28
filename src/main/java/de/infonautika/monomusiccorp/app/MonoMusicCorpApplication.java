package de.infonautika.monomusiccorp.app;


import de.infonautika.monomusiccorp.app.security.DefaultUsers;
import de.infonautika.monomusiccorp.app.security.ModifiableUserDetailsManager;
import de.infonautika.monomusiccorp.app.security.ModifiableUserDetailsManagerImpl;
import de.infonautika.monomusiccorp.app.security.UserRole;
import org.springframework.beans.factory.ObjectProvider;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

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
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("eclipselink.weaving", "false");
        properties.put("eclipselink.logging.level", "FINE");
        return properties;
    }

    @Configuration
    @EnableWebSecurity
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        private ModifiableUserDetailsManager userDetailsManager;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            String realmName = "Realm";
            http
                    .csrf().disable()
                    .authorizeRequests()
                        .antMatchers("/").authenticated()
                        .antMatchers("/info/**").authenticated()
                        .antMatchers("/shopping/**").hasRole(UserRole.CUSTOMER)
                        .antMatchers("/app/**").hasRole(UserRole.ADMIN)
                        .antMatchers("/stock/**").hasRole(UserRole.STOCK_MANAGER).and()
                        .httpBasic().realmName(realmName).and()
                    .logout()
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // workaround for http basic auth that lets you change user
                            // (in combination with specific ajax request)
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setHeader("WWW-Authenticate", "Basic realm=\"" + realmName + "\"");
                            response.setHeader("Cache-Control", "no-cache");
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"redirect\": \"/\"}");
                        });
        }


        @Bean
        public ModifiableUserDetailsManager getUserDetailsManager() {
            if (userDetailsManager == null) {
                userDetailsManager = new ModifiableUserDetailsManagerImpl();
                userDetailsManager.createUser(DefaultUsers.ADMIN);
            }
            return userDetailsManager;
        }

    }
}
