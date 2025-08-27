package nz.co.tsb.demofortsb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoForTSBApplication {

    public static void main(String[] args) {
        // Enable Virtual Threads (Java 21 feature)
        System.setProperty("spring.threads.virtual.enabled", "true");

        SpringApplication.run(DemoForTSBApplication.class, args);

        System.out.println("===========================================");
        System.out.println("   DemoForTSB Application Started!");
        System.out.println("   Running on: http://localhost:8080");
        System.out.println("   Java Version: " + System.getProperty("java.version"));
        System.out.println("===========================================");
    }
}
