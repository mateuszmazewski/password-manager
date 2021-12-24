package com.github.mateuszmazewski.passwordmanager.data;

import static com.github.mateuszmazewski.passwordmanager.data.entity.User.MIN_PASSWORD_LENGTH;

public class Messages {
    public static final String EMPTY = "Nie może być puste.";
    public static final String USERNAME_NOT_UNIQUE = "Taki użytkownik już istnieje.";
    public static final String PASSWORD_NOT_STRONG = "Hasło musi zawierać co najmniej " + MIN_PASSWORD_LENGTH + " znaków, a w tym: " +
            "min. 1 cyfrę, min. 1 dużą literę, min. 1 małą literę, min. 1 znak specjalny.";
    public static final String AUTHENTICATED_USER_ERROR = "Nieoczekiwany błąd. Nie udało się pobrać aktualnie zalogowanego użytkownika.";
    public static final String INVALID_MASTER_PASSWORD = "Podane hasło główne jest nieprawidłowe";
    public static final String MASTER_PASSWORD_REQUIRED = "Ta operacja wymaga podania hasła głównego";
}
