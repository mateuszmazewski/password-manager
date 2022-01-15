package com.github.mateuszmazewski.passwordmanager.security;

import com.github.mateuszmazewski.passwordmanager.data.Messages;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.PasswordField;

import javax.servlet.http.HttpServletRequest;

import static com.github.mateuszmazewski.passwordmanager.data.entity.User.MIN_PASSWORD_LENGTH;

public class Util {
    public static final int PASSWORD_STRENGTH_MIN = 0;
    public static final int PASSWORD_STRENGTH_MAX = 10;

    public static int passwordStrength(String password) {
        int strength = 0;

        if (password == null) {
            return 0;
        } else if (password.length() >= MIN_PASSWORD_LENGTH) {
            strength += 2;
        }

        boolean containsDigit = false;
        boolean containsUppercase = false;
        boolean containsLowercase = false;
        boolean containsSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c) && !containsDigit) {
                containsDigit = true;
                strength += 2;
                continue;
            }
            if (Character.isUpperCase(c) && !containsUppercase) {
                containsUppercase = true;
                strength += 2;
                continue;
            }
            if (Character.isLowerCase(c) && !containsLowercase) {
                containsLowercase = true;
                strength += 2;
                continue;
            }
            if (!Character.isLetterOrDigit(c) && !containsSpecial) {
                containsSpecial = true;
                strength += 2;
            }

            if (strength == 10) {
                break;
            }
        }

        return strength;
    }

    public static boolean validatePassword(PasswordField passwordField, ProgressBar passwordStrength, boolean requireStrongPassword) {
        int strength = passwordStrength(passwordField.getValue());
        passwordStrength.setValue(strength);

        if (strength < 4) {
            passwordStrength.removeThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            passwordStrength.addThemeVariants(ProgressBarVariant.LUMO_ERROR);
        } else if (strength < 10) {
            passwordStrength.removeThemeVariants(ProgressBarVariant.LUMO_ERROR, ProgressBarVariant.LUMO_SUCCESS);
        } else {
            passwordStrength.removeThemeVariants(ProgressBarVariant.LUMO_ERROR);
            passwordStrength.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        }

        if (passwordField.getValue() == null || passwordField.getValue().isEmpty()) {
            passwordField.setInvalid(true);
            passwordField.setErrorMessage(Messages.EMPTY);
            return false;
        } else {
            if (passwordField.getValue().length() > 255) {
                passwordField.setInvalid(true);
                passwordField.setErrorMessage(Messages.LENGTH_255);
                return false;
            } else {
                passwordField.setInvalid(false);
                passwordField.setErrorMessage(null);
            }

        }
        if (!requireStrongPassword || strength == 10) {
            passwordField.setInvalid(false);
            passwordField.setErrorMessage(null);
            return true;
        } else {
            passwordField.setInvalid(true);
            passwordField.setErrorMessage(Messages.PASSWORD_NOT_STRONG);
            return false;
        }
    }

    public static String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
