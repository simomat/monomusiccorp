package de.infonautika.monomusiccorp.app;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import de.infonautika.monomusiccorp.app.business.ApplicationState;
import de.infonautika.monomusiccorp.app.controller.utils.links.Relation;
import de.infonautika.monomusiccorp.app.controller.utils.links.curi.MethodCuriProvider;
import de.infonautika.monomusiccorp.app.controller.utils.links.curi.RelationMethodRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

@SpringBootApplication
@EnableHypermediaSupport(type= {EnableHypermediaSupport.HypermediaType.HAL})
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

    @Bean
    public ServletContextInitializer getServletContextInitializer() {
        return servletContext -> {
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            initApplicationState(ctx);
            initRelationMethodRegistry(ctx);
        };
    }

    private void initApplicationState(WebApplicationContext ctx) {
        ApplicationState applicationState = ctx.getBean("applicationState", ApplicationState.class);
        applicationState.dropState();
        applicationState.createState();
    }

    private void initRelationMethodRegistry(WebApplicationContext ctx) {
        RelationMethodRegistry registry = ctx.getBean("relationMethodRegistry", RelationMethodRegistry.class);
        getRelationMethodsInControllers().forEach(registry::register);
    }

    private Stream<Method> getRelationMethodsInControllers() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        ClassLoader classLoader = getClass().getClassLoader();
        return scanner.findCandidateComponents("de.infonautika.monomusiccorp").stream()
                .map(beanDefinition -> {
                    try {
                        return stream(classLoader.loadClass(beanDefinition.getBeanClassName()).getDeclaredMethods());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(Function.identity())
                .filter(method -> method.getAnnotation(Relation.class) != null);
    }

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("ex", new UriTemplate("/api/curis/{rel}"));
    }

    @Bean
    public RelationMethodRegistry relationMethodRegistry() {
        return new RelationMethodRegistry();
    }

    @Bean
    public MethodCuriProvider methodCuriProvider() {
        return new MethodCuriProvider();
    }

    @Bean
    public JsonSchemaGenerator jsonSchemaGenerator() {
        return new JsonSchemaGenerator(new ObjectMapper());
    }

    @Configuration
    @EnableGlobalMethodSecurity(securedEnabled = true)
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            String realmName = "Realm";
            http
                    .headers().frameOptions().disable().and()
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
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

    }
}
