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
            // Mask JSON format: "key":"value" or "key": "value"
            msg = msg.replaceAll("\"" + key + "\"\\s*:\\s*\"([^\"]{2})([^\"]*)([^\"]{2})\"",
                    "\"" + key + "\":\"$1****$3\"");

            // Mask key=value format
            msg = msg.replaceAll("(?i)" + key + "=([^\\s,]{2})([^\\s,]*)([^\\s,]{2})",
                    key + "=$1****$3");

            // Mask key: value format
            msg = msg.replaceAll("(?i)" + key + ":\\s*([^\\s,]{2})([^\\s,]*)([^\\s,]{2})",
                    key + ": $1****$3");
        }
        return msg;
    }
}
