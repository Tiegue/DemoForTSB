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

import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
import nz.co.tsb.demofortsb.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Configuration
//@ConditionalOnProperty(value="app.seed.enabled", havingValue="true")//data initialized from liquibase
@DependsOn("liquibase")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordService passwordService;

    @Value("${WT_ADMIN_NATIONAL_ID:123456789}")
    private String adminNationalId;

    @Value("${ADMIN_DEFAULT_PASSWORD:Admin123!}")
    private String defaultAdminPassword;

    @Value("${AUTO_INIT_ADMIN_PASSWORD:true}")
    private boolean autoInitAdminPassword;

    @Override
    public void run(String... args) throws Exception {

        // Auto-initialization of admin password is disabled
        if (!autoInitAdminPassword) {
            logger.info("Auto-initialization of admin password is disabled");
            return;
        }

        try {
            // Check if admin exists
            Customer admin = customerRepository.findByNationalId(adminNationalId).orElse(null);

            if (admin == null) {
                logger.warn("Admin user with nationalId {} not found. Skipping password initialization.",
                        adminNationalId);
                return;
            }

            // Check if password needs to be set
            boolean needsPassword = admin.getPasswordHash() == null ||
                    admin.getPasswordHash().isEmpty() ||
                    admin.getPasswordHash().equals("CHANGE_ME");

            if (needsPassword) {
                logger.info("Initializing admin password for user: {}", admin.getEmail());

                // Set default password using PasswordService (BCrypt strength 12)
                String hashedPassword = passwordService.hashPassword(defaultAdminPassword);
                admin.setPasswordHash(hashedPassword);

                // Ensure admin is active
                if (!admin.isActive()) {
                    admin.setStatus(Customer.CustomerStatus.ACTIVE);
                    logger.info("Activating admin account");
                }

                customerRepository.save(admin);

                logger.info("=====================================");
                logger.info("Admin password initialized successfully");
                logger.info("AdminUserAsEmail: {}", admin.getEmail());
                logger.info("Default Password: {}", defaultAdminPassword);
                logger.info("Please change this password after first login!");
                logger.info("=====================================");

            } else {
                // Check if password needs rehashing (upgrade from BCrypt 10 to 12)
                if (passwordService.needsRehash(admin.getPasswordHash())) {
                    logger.info("Admin password needs rehashing for security upgrade");
                    // Note: Can't rehash without knowing the plain password
                    // User will get new hash on next password change
                }

                logger.info("Admin user already has password set for: {}", admin.getEmail());
            }

        } catch (Exception e) {
            logger.error("Failed to initialize admin password", e);
            // Don't throw - allow application to start even if admin initialization fails
        }
    }

    // =====  Commented as Liquibase is used for data initialization
//    @Bean
//    CommandLineRunner initDatabase(CustomerRepository customerRepository) {
//        return args -> {
//            logger.info("Initializing database with sample data...");
//
//            // Create sample customers
//            Customer customer1 = CustomerBuilder.create()
//                    .firstName("Tiegue")
//                    .lastName("Zhang")
//                    .email("tiegue303@example.com")
//                    .phoneNumber("+1234567890")
//                    .dateOfBirth(LocalDate.of(1978, 3, 3))
//                    .nationalId("123456789")
//                    .status(Customer.CustomerStatus.ACTIVE)
//                    .build();
//
//            Customer customer2 = CustomerBuilder.create()
//                    .firstName("Tina")
//                    .lastName("Mu")
//                    .email("tinamooh@example.com")
//                    .phoneNumber("+0987654321")
//                    .dateOfBirth(LocalDate.of(1980, 5, 15))
//                    .nationalId("987654321")
//                    .status(Customer.CustomerStatus.ACTIVE)
//                    .build();
//
//            customerRepository.save(customer1);
//            customerRepository.save(customer2);
//
//            logger.info("Sample data initialized successfully!");
//            logger.info("Total customers in database: {}", customerRepository.count());
//        };
//    }
}
