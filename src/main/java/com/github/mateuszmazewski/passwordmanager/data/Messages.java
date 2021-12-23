package com.github.mateuszmazewski.passwordmanager.data;

import static com.github.mateuszmazewski.passwordmanager.data.entity.User.MIN_PASSWORD_LENGTH;

public class Messages {
    public static final String EMPTY = "Nie może być puste.";
    public static final String USERNAME_NOT_UNIQUE = "Taki użytkownik już istnieje.";
    public static final String PASSWORD_NOT_STRONG = "Hasło musi zawierać co najmniej " + MIN_PASSWORD_LENGTH + " znaków, a w tym: " +
            "min. 1 cyfrę, min. 1 dużą literę, min. 1 małą literę, min. 1 znak specjalny.";
}
