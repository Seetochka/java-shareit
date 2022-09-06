package ru.practicum.shareit.exception;

/**
 * Исключение валидации
 */
public class ValidationException extends LoggingException {
    public ValidationException(String message, String className) {
        super(message, className);
    }
}
