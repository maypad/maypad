package de.fraunhofer.iosb.maypadbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.io.File;
import java.util.Locale;

/**
 *  Configuration Class to access properties for the client, e.g. the locale.
 *
 * @author Max Willich
 */
@ComponentScan
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${MAYPAD_HOME:/usr/share/maypad/}")
    private String maypadHomePath;

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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File maypadHome = new File(maypadHomePath);
        File frontend = new File(maypadHome.getAbsolutePath() + "/frontend/");
        File index = new File(frontend.getAbsolutePath() + "/index.html");
        if (maypadHome.isDirectory() && frontend.isDirectory() && index.exists()) {
            logger.info("Serving frontend files from " + frontend.getAbsolutePath());
            registry.addResourceHandler("/**")
                    .addResourceLocations("file:" + frontend.getAbsolutePath() + "/");

        } else {
            logger.error(maypadHomePath + " does not contain valid frontend files.");
        }

    }
}