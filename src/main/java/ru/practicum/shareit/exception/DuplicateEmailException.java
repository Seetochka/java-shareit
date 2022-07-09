package ru.practicum.shareit.exception;

/**
 * Исключение дублирования электронной почты
 */
public class DuplicateEmailException extends LoggingException {
    public DuplicateEmailException(String message, String className) {
        super(message, className);
    }
}
