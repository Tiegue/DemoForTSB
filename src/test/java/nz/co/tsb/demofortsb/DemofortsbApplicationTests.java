package nz.co.tsb.demofortsb;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.MDC;
import java.util.UUID;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DemofortsbApplicationTests {
    @BeforeEach
    void addMdc() {
        String corrId = UUID.randomUUID().toString();
        MDC.put("correlationId", "test-" + corrId);
        MDC.put("traceId", corrId.replace("-", ""));
        MDC.put("spanId", UUID.randomUUID().toString().substring(0, 16));
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

	@Test
	void contextLoads() {
	}

}
