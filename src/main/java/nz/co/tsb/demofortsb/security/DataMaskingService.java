package nz.co.tsb.demofortsb.security;

import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class DataMaskingService {

    private static final String MASK_CHARS = "****";

    public String maskNationalId(String nationalId) {
        if (nationalId == null) {
            return MASK_CHARS;
        }
        if (nationalId.trim().isEmpty()) {
            return MASK_CHARS;
        }

        String cleanId = nationalId.trim();
        if (cleanId.length() < 6) {
            return MASK_CHARS;
        }

        return cleanId.substring(0, 2) + MASK_CHARS + cleanId.substring(cleanId.length() - 3);
    }

    public String maskPhone(String phone) {
        if (phone == null) {
            return MASK_CHARS;
        }
        if (phone.trim().isEmpty()) {
            return MASK_CHARS;
        }

        String cleanPhone = phone.trim().replaceAll("[^0-9+]", "");
        if (cleanPhone.length() < 4) {
            return MASK_CHARS;
        }

        return "***-***-" + cleanPhone.substring(cleanPhone.length() - 4);
    }

    public String maskEmail(String email) {
        if (email == null) {
            return "****@****.***";
        }
        if (email.trim().isEmpty() || !email.contains("@")) {
            return "****@****.***";
        }

        String cleanEmail = email.trim().toLowerCase();
        String[] parts = cleanEmail.split("@");
        if (parts.length != 2 || parts[0].length() == 0) {
            return "****@****.***";
        }

        return parts[0].charAt(0) + "***@" + parts[1];
    }

    public String maskFirstName(String firstName) {
        if (firstName == null) {
            return MASK_CHARS;
        }
        if (firstName.trim().isEmpty()) {
            return MASK_CHARS;
        }

        String cleanName = firstName.trim();
        if (cleanName.length() == 1) {
            return cleanName + "***";
        }

        return cleanName.charAt(0) + "***";
    }

    public String maskLastName(String lastName) {
        if (lastName == null) {
            return MASK_CHARS;
        }
        if (lastName.trim().isEmpty()) {
            return MASK_CHARS;
        }

        String cleanName = lastName.trim();
        if (cleanName.length() == 1) {
            return cleanName + "***";
        }

        return cleanName.charAt(0) + "***";
    }

    public String maskFullName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return MASK_CHARS;
        }

        String maskedFirst = firstName != null ? maskFirstName(firstName) : "";
        String maskedLast = lastName != null ? maskLastName(lastName) : "";

        if (maskedFirst.isEmpty()) return maskedLast;
        if (maskedLast.isEmpty()) return maskedFirst;

        return maskedFirst + " " + maskedLast;
    }

    public String maskDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return "****-**-**";
        }

        return "Year: " + dateOfBirth.getYear();
    }

    public String maskDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null) {
            return "****-**-**";
        }
        if (dateOfBirth.trim().isEmpty()) {
            return "****-**-**";
        }

        try {
            LocalDate date = LocalDate.parse(dateOfBirth.trim());
            return maskDateOfBirth(date);
        } catch (Exception e) {
            return "****-**-**";
        }
    }

    public String maskAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return "****-****-****-****";
        }
        if (accountNumber.trim().isEmpty()) {
            return "****-****-****-****";
        }

        String cleanAccount = accountNumber.trim().replaceAll("[^0-9]", "");
        if (cleanAccount.length() < 4) {
            return "****-****-****-****";
        }

        return "****-****-****-" + cleanAccount.substring(cleanAccount.length() - 4);
    }

    public String maskCreditCard(String cardNumber) {
        return maskAccountNumber(cardNumber);
    }

    public String maskPassword(String password) {
        if (password == null) {
            return "null";
        }
        if (password.isEmpty()) {
            return "[empty]";
        }

        return "************";
    }

    public String maskGeneric(String value, int showFirst, int showLast) {
        if (value == null) {
            return MASK_CHARS;
        }
        if (value.trim().isEmpty()) {
            return MASK_CHARS;
        }

        String cleanValue = value.trim();
        if (cleanValue.length() <= showFirst + showLast) {
            return MASK_CHARS;
        }

        return cleanValue.substring(0, showFirst) +
                MASK_CHARS +
                cleanValue.substring(cleanValue.length() - showLast);
    }
}