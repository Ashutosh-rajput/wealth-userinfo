package com.WealthManager.UserInfo.Util;


import com.WealthManager.UserInfo.exception.InvalidFileTypeException;
import jakarta.validation.ValidationException;

import java.util.Locale;
import java.util.Set;

public class Validator {

    private static final String REGEX_NAME;
    private static final String REGEX_EMAIL;
    private static final String REGEX_PHONE;
    private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

    static {
        REGEX_NAME = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@(.+)$";
        REGEX_PHONE = "^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\\s?){6,15}[0-9]{1}$";
    }

    public static void validateName(String name) throws ValidationException {
        if (name == null)
            throw new ValidationException("Name cannot be null");
        if (!name.matches(REGEX_NAME))
            throw new ValidationException("Invalid name");
    }

    public static void validateEmail(String email) throws ValidationException {
        if (email == null)
            throw new ValidationException("Email cannot be null");
        if (!email.matches(REGEX_EMAIL))
            throw new ValidationException("Invalid email");
    }



    public static void validateSubject(String subject) throws ValidationException {
        if (subject == null)
            throw new ValidationException("Subject cannot be null");
        if (subject.length() > 100)
            throw new ValidationException("Subject length must not be more than 100");
    }

    public static void validateMessage(String message) throws ValidationException {
        if (message == null)
            throw new ValidationException("Message cannot be null");
        if (message.length() > 500)
            throw new ValidationException("Message length must not be more than 500");
    }

    public static void validatePhone(String phone) throws ValidationException {
        if (phone == null)
            throw new ValidationException("Phone number cannot be null");
        if (!phone.matches(REGEX_PHONE))
            throw new ValidationException("Invalid phone number");
    }

    public static void validateCountry(String country) throws ValidationException {
        if (country == null)
            throw new ValidationException("Country cannot be null");
        if (!ISO_COUNTRIES.contains(country))
            throw new ValidationException("Invalid country");
    }

    public static void validateImageFormat(String imageFormat) throws ValidationException {
        if (!imageFormat.matches("image/(png|jpg|jpeg)"))
            throw new InvalidFileTypeException("Invalid image format");
    }

    public static void validateFileFormat(String resumeFormat) throws ValidationException {
        if (!resumeFormat.matches("application/pdf|doc|docx|png|jpg|jpeg"))
            throw new InvalidFileTypeException("Invalid File format");
    }

//    public static void validateCertificateFormat(String certificateFormat) throws ValidationException {
//        if (!certificateFormat.matches("application/(pdf|doc|docx)|image/(png|jpg|jpeg)"))
//            throw new InvalidFileTypeException("Invalid certificate format");
//    }

}
