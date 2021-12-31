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
    public static final String SAVE_ERROR = "Wystąpił nieznany błąd zapisu.";
    public static final String INTEGRITY = "Nie można usunąć ze względu na więzy integralności.";
    public static final String DECRYPTION_ERROR = "Wystąpił nieznany błąd podczas deszyfrowania.";
    public static final String COPIED_TO_CLIPBOARD = "Skopiowano hasło do schowka.";
    public static final String CODE_NO_LONGER_VALID = "Upłynął czas na wprowadzenie kodu. Wygeneruj nowy kod.";
}
