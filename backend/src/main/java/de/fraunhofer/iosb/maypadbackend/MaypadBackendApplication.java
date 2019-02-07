package de.fraunhofer.iosb.maypadbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Maypad main class.
 */
@SpringBootApplication
public class MaypadBackendApplication {

    /**
     * Start Maypad. So let the heart of Maypad begin to beat.
     *
     * @param args Program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MaypadBackendApplication.class, args);
    }

}
