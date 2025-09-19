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
        String reqId = UUID.randomUUID().toString();
        MDC.put("reqId", "test-" + reqId);
        MDC.put("traceId", reqId.replace("-", ""));
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
