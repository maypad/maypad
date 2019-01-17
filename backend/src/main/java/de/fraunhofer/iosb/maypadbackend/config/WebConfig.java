package de.fraunhofer.iosb.maypadbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 *  Configuration Class to access properties for the client, e.g. the locale.
 *
 * @author Max Willich
 */
@ComponentScan
@Configuration
public abstract class WebConfig implements WebMvcConfigurer {

    /**
     * Returns LocaleResolver that resolves current locale from session.
     * If locale can't be resolved, US-Locale is set as default.
     *
     * @return LocaleResolver with properties mentioned above.
     */
    @Bean
    public LocaleResolver getLocaleResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    /**
     * Returns a LocaleChangeInterceptor which is set up to trigger when "?lang=..." is present
     * in a request.
     *
     * @return The Interceptor Spring will use to change the locale
     */
    @Bean
    public LocaleChangeInterceptor getLocaleChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Implements addInterceptors from WebMvcConfigurer, adds a LocaleChangeInterceptor
     * with correct setup to the spring-application (see getLocaleChangeInterceptor()).
     *
     * @param registry Registry in which the interceptor is supposed to be added.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLocaleChangeInterceptor());
    }
}
