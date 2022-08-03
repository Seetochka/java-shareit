package ru.practicum.shareit.exception;

/**
 * Исключение неподдержимаевого статуса
 */
public class UnsupportedStatusException extends LoggingException {
    public UnsupportedStatusException(String message, String className) {
        super(message, className);
    }
}
