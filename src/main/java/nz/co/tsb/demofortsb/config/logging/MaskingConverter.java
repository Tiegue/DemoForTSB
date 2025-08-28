package nz.co.tsb.demofortsb.config.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskingConverter extends MessageConverter {
    private static final String[] SENSITIVE_KEYS = {"password", "secret", "ssn", "nationalId", "email",
            "phoneNumber", "dateOfBirth"};

    @Override
    public String convert(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        if (msg == null) return null;
        for (String key : SENSITIVE_KEYS) {
            msg = msg.replaceAll("(?i)" + key + "=[^\\s]+", key + "=[PROTECTED]");
        }
        return msg;
    }
}
