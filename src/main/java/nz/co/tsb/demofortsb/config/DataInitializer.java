package nz.co.tsb.demofortsb.config;

import jakarta.annotation.PostConstruct;
import nz.co.tsb.demofortsb.dto.response.CustomerReponse;
import nz.co.tsb.demofortsb.entity.CustomerBuilder;
import nz.co.tsb.demofortsb.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nz.co.tsb.demofortsb.entity.Customer;
import nz.co.tsb.demofortsb.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import nz.co.tsb.demofortsb.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//@ConditionalOnProperty(value="app.seed.enabled", havingValue="true")//data initialized from liquibase
//@DependsOn("liquibase")

public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer() {
        logger.info("DataInitializer initialized");
    }

    @PostConstruct
    public void init() {
        logger.info("DataInitializer @PostConstruct called!");
    }



    @Autowired
    private PasswordService passwordService;

    @Autowired
    private CustomerService customerService;

    @Value("${WT_ADMIN_NATIONAL_ID:123456789}")
    private String adminNationalId;

    @Value("${ADMIN_DEFAULT_PASSWORD:password123!}")
    private String defaultAdminPassword;

    @Value("${AUTO_INIT_ADMIN_PASSWORD:true}")
    private boolean autoInitAdminPassword;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DATAINITIALIZER IS RUNNING!!!");
        logger.info("Initializing default admin user password");

        initAdminPassword();
        generateDefaultPasswordHash();
        setTinaPassword();

        logger.info("Default admin and user password initialized successfully");
        System.out.println("DATAINITIALIZER IS FINISHED!!!");

    }


    private void initAdminPassword() {
        // Auto-initialization of admin password is disabled
        if (!autoInitAdminPassword) {
            logger.info("Auto-initialization of admin password is disabled");
            return;
        }

        try {
            // Check if admin exists
            Customer admin = null;
            admin = customerService.getCustomerByNationalId(adminNationalId);

            if (admin == null) {
                logger.warn("Admin user with nationalId {} not found. Skipping password initialization.",
                        adminNationalId);
                return;
            }

            // Set default password using PasswordService (BCrypt strength 12)
            String hashedPassword = passwordService.hashPassword(defaultAdminPassword);
            customerService.updateCustomerPassword(adminNationalId, hashedPassword);

            logger.info("=====================================");
            logger.info("Admin password initialized successfully");
            logger.info("AdminUserAsEmail: {}", admin.getEmail());
            logger.info("Default Password: {}", defaultAdminPassword);
            logger.info("Please change this password after first login!");
            logger.info("=====================================");



        } catch (Exception e) {
            logger.error("Failed to initialize admin password", e);
            // Don't throw - allow application to start even if admin initialization fails
        }
    }

    private void generateDefaultPasswordHash() {
        String plainPassword = "password123!";
        // Set default password using PasswordService (BCrypt strength 12)
        String hashedPassword = passwordService.hashPassword(defaultAdminPassword);

        logger.info("================================================");
        logger.info("BCrypt Password Hash (Strength 12)");
        logger.info("================================================");
        logger.info("Plain: {}", plainPassword);
        logger.info("Hash: {}", hashedPassword);
        logger.info("================================================");
    }

    //Just for testing and debugging
    private void setTinaPassword() {
        try {
            Optional<Customer> tinaOpt = customerService.findByEmail("tinamooh@example.com");
            if (tinaOpt.isPresent()) {
                String hashedPassword = passwordService.hashPassword("password123!");
                Customer tina = tinaOpt.get();
                customerService.updateCustomerPassword(tina.getNationalId(), hashedPassword);
                logger.info("Password set for tinamooh@example.com");
            }
        } catch (Exception e) {
            logger.error("Failed to set Tina's password", e);
        }
    }

}
