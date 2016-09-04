package de.infonautika.monomusiccorp.app;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

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


//    @Configuration
//    @EnableWebSecurity
//    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
////            http.authorizeRequests().antMatchers("/**").authenticated().and().httpBasic();
//            http.authorizeRequests().antMatchers("/**").permitAll().and().httpBasic();
//
////            http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin()
////                    .loginPage("/login").failureUrl("/login?error").permitAll().and()
////                    .logout().permitAll();
//        }
//
//        @Override
//        public void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.inMemoryAuthentication().withUser("admin").password("admin")
//                    .roles("ADMIN", "USER").and().withUser("user").password("user")
//                    .roles("USER");
//        }
//
//    }
}
