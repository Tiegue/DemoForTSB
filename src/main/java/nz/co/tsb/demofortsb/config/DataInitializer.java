package nz.co.tsb.demofortsb.config;

import nz.co.tsb.demofortsb.entity.CustomerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.time.LocalDate;

@Configuration
@ConditionalOnProperty(value="app.seed.enabled", havingValue="true")//data initialized from liquibase
@DependsOn("liquibase")
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(CustomerRepository customerRepository) {
        return args -> {
            log.info("Initializing database with sample data...");

            // Create sample customers
            Customer customer1 = CustomerBuilder.create()
                    .firstName("Tiegue")
                    .lastName("Zhang")
                    .email("tiegue303@example.com")
                    .phoneNumber("+1234567890")
                    .dateOfBirth(LocalDate.of(1978, 3, 3))
                    .nationalId("123456789")
                    .status(Customer.CustomerStatus.ACTIVE)
                    .build();

            Customer customer2 = CustomerBuilder.create()
                    .firstName("Tina")
                    .lastName("Mu")
                    .email("tinamooh@example.com")
                    .phoneNumber("+0987654321")
                    .dateOfBirth(LocalDate.of(1980, 5, 15))
                    .nationalId("987654321")
                    .status(Customer.CustomerStatus.ACTIVE)
                    .build();

            customerRepository.save(customer1);
            customerRepository.save(customer2);

            log.info("Sample data initialized successfully!");
            log.info("Total customers in database: {}", customerRepository.count());
        };
    }
}
